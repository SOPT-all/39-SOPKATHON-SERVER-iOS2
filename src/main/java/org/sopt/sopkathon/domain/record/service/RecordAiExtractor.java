package org.sopt.sopkathon.domain.record.service;

import lombok.extern.slf4j.Slf4j;
import org.sopt.sopkathon.domain.record.code.RecordErrorCode;
import org.sopt.sopkathon.domain.record.domain.RecordCategory;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordExtraction;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordItem;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RecordAiExtractor {

    private final GoogleGenAiChatModel googleGenAiChatModel;
    private final ObjectMapper objectMapper;
    private final String recordExtractionPrompt;

    public RecordAiExtractor(
            GoogleGenAiChatModel googleGenAiChatModel,
            ObjectMapper objectMapper,
            @Value("${app.gemini.record-extraction-prompt}") String recordExtractionPrompt
    ) {
        this.googleGenAiChatModel = googleGenAiChatModel;
        this.objectMapper = objectMapper;
        this.recordExtractionPrompt = recordExtractionPrompt;
    }

    public AiRecordExtraction extract(String text, List<String> assignedKidNames) {
        return extract(text, assignedKidNames, true, true);
    }

    public AiRecordExtraction extract(
            String text,
            List<String> assignedKidNames,
            boolean kidNameRequired,
            boolean categoryRequired
    ) {
        Set<String> normalizedKidNames = normalizeKidNames(assignedKidNames);
        validateInput(text, normalizedKidNames);

        long start = System.nanoTime();
        log.info("Starting Gemini record extraction");

        try {
            String response = googleGenAiChatModel.call(
                    createPrompt(text, normalizedKidNames)
            );
            AiRecordExtraction extraction = parseResponse(response);
            validateExtraction(
                    extraction,
                    normalizedKidNames,
                    kidNameRequired,
                    categoryRequired
            );

            log.info("Gemini record extraction completed ({} ms, {} records)",
                    elapsedMillis(start), extraction.records().size());
            return extraction;
        } catch (GeneralException exception) {
            log.warn("Gemini record extraction rejected ({} ms, {})",
                    elapsedMillis(start), exception.getErrorCode().getCode());
            throw exception;
        } catch (RuntimeException exception) {
            log.warn("Gemini record extraction failed ({} ms, {})",
                    elapsedMillis(start), exception.getClass().getSimpleName());
            throw new GeneralException(RecordErrorCode.AI_SERVER_RESPONSE_FAILED);
        }
    }

    private Set<String> normalizeKidNames(List<String> assignedKidNames) {
        if (assignedKidNames == null) {
            return Set.of();
        }

        return assignedKidNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void validateInput(String text, Set<String> assignedKidNames) {
        if (assignedKidNames.isEmpty()) {
            throw new GeneralException(RecordErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED);
        }
        if (text == null || text.isBlank()) {
            throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
        }
    }

    private String createPrompt(String text, Set<String> assignedKidNames) {
        String kids = assignedKidNames.stream()
                .map(name -> "- " + name)
                .collect(Collectors.joining("\n"));

        return recordExtractionPrompt
                .replace("{assignedKids}", kids)
                .replace("{text}", text.trim());
    }

    private AiRecordExtraction parseResponse(String response) {
        if (response == null || response.isBlank()) {
            throw new GeneralException(RecordErrorCode.AI_SERVER_RESPONSE_FAILED);
        }

        JsonNode records;
        try {
            records = objectMapper.readTree(extractJsonObject(response)).path("records");
        } catch (JacksonException exception) {
            throw new GeneralException(RecordErrorCode.AI_SERVER_RESPONSE_FAILED);
        }
        if (!records.isArray()) {
            throw new GeneralException(RecordErrorCode.AI_SERVER_RESPONSE_FAILED);
        }

        List<AiRecordItem> items = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            JsonNode item = records.get(i);
            items.add(new AiRecordItem(
                    stringOrNull(item.path("kidName")),
                    toCategory(item.path("category"))
            ));
        }
        return new AiRecordExtraction(items);
    }

    // 코드 블록이나 앞뒤 설명 문장이 붙어도 첫 '{' ~ 마지막 '}'만 사용
    private String extractJsonObject(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start < 0 || end < start) {
            return response;
        }
        return response.substring(start, end + 1);
    }

    private String stringOrNull(JsonNode node) {
        return node.isString() ? node.asString() : null;
    }

    // 대소문자는 무시하고, 목록에 없는 값은 null (분류 실패로 처리)
    private RecordCategory toCategory(JsonNode node) {
        String value = stringOrNull(node);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return RecordCategory.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void validateExtraction(
            AiRecordExtraction extraction,
            Set<String> assignedKidNames,
            boolean kidNameRequired,
            boolean categoryRequired
    ) {
        if (extraction == null
                || extraction.records() == null
                || extraction.records().isEmpty()) {
            throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
        }

        for (AiRecordItem record : extraction.records()) {
            if (record == null) {
                throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
            }
            if (kidNameRequired
                    && (record.kidName() == null || record.kidName().isBlank())) {
                throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
            }
            if (categoryRequired && record.category() == null) {
                throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
            }
        }
    }

    private long elapsedMillis(long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}
