package org.sopt.sopkathon.domain.record.service;

import lombok.extern.slf4j.Slf4j;
import org.sopt.sopkathon.domain.record.code.RecordErrorCode;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordExtraction;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordItem;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecordAiExtractor {

    private static final String JSON_CODE_BLOCK_PREFIX = "```json";
    private static final String CODE_BLOCK_PREFIX = "```";
    private static final String CODE_BLOCK_SUFFIX = "```";

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
        Set<String> normalizedKidNames = normalizeKidNames(assignedKidNames);
        validateInput(text, normalizedKidNames);

        long start = System.nanoTime();
        log.info("Starting Gemini record extraction");

        try {
            String response = googleGenAiChatModel.call(
                    createPrompt(text, normalizedKidNames)
            );
            AiRecordExtraction extraction = parseResponse(response);
            validateExtraction(extraction, normalizedKidNames);

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

        return objectMapper.readValue(
                removeCodeBlock(response),
                AiRecordExtraction.class
        );
    }

    private String removeCodeBlock(String response) {
        String normalized = response.trim();

        if (normalized.startsWith(JSON_CODE_BLOCK_PREFIX)) {
            normalized = normalized.substring(JSON_CODE_BLOCK_PREFIX.length()).trim();
        } else if (normalized.startsWith(CODE_BLOCK_PREFIX)) {
            normalized = normalized.substring(CODE_BLOCK_PREFIX.length()).trim();
        }

        if (normalized.endsWith(CODE_BLOCK_SUFFIX)) {
            normalized = normalized.substring(
                    0,
                    normalized.length() - CODE_BLOCK_SUFFIX.length()
            ).trim();
        }

        return normalized;
    }

    private void validateExtraction(
            AiRecordExtraction extraction,
            Set<String> assignedKidNames
    ) {
        if (extraction == null
                || extraction.records() == null
                || extraction.records().isEmpty()) {
            throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
        }

        for (AiRecordItem record : extraction.records()) {
            if (record == null
                    || record.kidName() == null
                    || record.kidName().isBlank()
                    || record.category() == null
                    || record.content() == null
                    || record.content().isBlank()) {
                throw new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED);
            }
            if (!assignedKidNames.contains(record.kidName())) {
                throw new GeneralException(RecordErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED);
            }
        }
    }

    private long elapsedMillis(long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}
