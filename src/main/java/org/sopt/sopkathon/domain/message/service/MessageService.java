package org.sopt.sopkathon.domain.message.service;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.gemini.service.GeminiService;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.repository.KidRepository;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.message.code.MessageErrorCode;
import org.sopt.sopkathon.domain.message.domain.Message;
import org.sopt.sopkathon.domain.message.dto.request.MessageCreateRequest;
import org.sopt.sopkathon.domain.message.dto.request.MessagePolishRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.sopt.sopkathon.domain.message.repository.MessageRepository;
import org.sopt.sopkathon.domain.record.domain.Record;
import org.sopt.sopkathon.domain.record.repository.RecordRepository;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final KidRepository kidRepository;
    private final RecordRepository recordRepository;
    private final MemberKidRepository memberKidRepository;
    private final GeminiService geminiService;

    @Transactional
    public MessageResponse generateDailyBriefing(Long memberId, MessageCreateRequest request) {
        Long kidId = request.kidId();

        if (!memberKidRepository.existsByMemberIdAndKidId(memberId, kidId)) {
            throw new GeneralException(MessageErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED);
        }

        Kid kid = kidRepository.findByIdAndDeletedAtIsNull(kidId)
                .orElseThrow(() -> new GeneralException(CommonErrorCode.NOT_FOUND));

        LocalDate date = request.date();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Record> records = recordRepository
                .findByKid_IdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
                        kid.getId(), start, end
                );

        if (records.isEmpty()) {
            throw new GeneralException(MessageErrorCode.NO_CARE_RECORDS);
        }

        String recordsText = records.stream()
                .map(this::formatCareRecord)
                .collect(Collectors.joining("\n"));

        String briefing;
        try {
            briefing = geminiService.ask(recordsText);
        } catch (RuntimeException exception) {
            GeneralException error = new GeneralException(MessageErrorCode.AI_RESPONSE_FAILED);
            error.initCause(exception);
            throw error;
        }

        return saveBriefing(kid, date, briefing);
    }

    @Transactional
    public MessageResponse refineBriefing(
            Long memberId,
            Long briefingId,
            MessagePolishRequest request
    ) {
        Message message = messageRepository.findById(briefingId)
                .orElseThrow(() -> new GeneralException(MessageErrorCode.BRIEFING_NOT_FOUND));

        Kid kid = message.getKid();
        if (kid.isDeleted() || !memberKidRepository.existsByMemberIdAndKidId(memberId, kid.getId())) {
            throw new GeneralException(MessageErrorCode.BRIEFING_NOT_FOUND);
        }

        String refinedContent;
        try {
            refinedContent = geminiService.refine(message.getContent(), request.instruction());
        } catch (RuntimeException exception) {
            GeneralException error = new GeneralException(MessageErrorCode.AI_RESPONSE_FAILED);
            error.initCause(exception);
            throw error;
        }

        if (refinedContent == null || refinedContent.isBlank()) {
            throw new GeneralException(MessageErrorCode.AI_RESPONSE_FAILED);
        }

        message.updateContent(refinedContent);
        return new MessageResponse(message.getId(), message.getContent());
    }

    private MessageResponse saveBriefing(Kid kid, LocalDate date, String content) {
        Message message = messageRepository.findByKid_IdAndBriefingDate(kid.getId(), date)
                .map(existing -> {
                    existing.updateContent(content);
                    return existing;
                })
                .orElseGet(() -> Message.builder()
                        .kid(kid)
                        .briefingDate(date)
                        .content(content)
                        .build());

        Message savedMessage = messageRepository.save(message);
        return new MessageResponse(savedMessage.getId(), savedMessage.getContent());
    }

    private String formatCareRecord(Record record) {
        return "%s %s".formatted(record.getRecordedAt(), record.getCategory());
    }
}
