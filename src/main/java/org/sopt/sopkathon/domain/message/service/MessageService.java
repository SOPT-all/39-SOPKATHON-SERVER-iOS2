package org.sopt.sopkathon.domain.message.service;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.gemini.service.GeminiService;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.message.code.MessageErrorCode;
import org.sopt.sopkathon.domain.message.domain.Message;
import org.sopt.sopkathon.domain.message.dto.request.MessageRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.sopt.sopkathon.domain.message.repository.MessageRepository;
import org.sopt.sopkathon.domain.record.domain.Record;
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
    private final GeminiService geminiService;

    @Transactional
    public MessageResponse generateDailyBriefing(MessageRequest request) {
        Kid kid = kidRepository.findById(request.kidId())
                .orElseThrow(() -> new GeneralException(CommonErrorCode.NOT_FOUND));

        LocalDate date = request.date();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Record> records = messageRepository
                .findByKid_IdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
                        kid.getId(), start, end
                );

        if (records.isEmpty()) {
            throw new GeneralException(MessageErrorCode.NO_CARE_RECORDS);
        }

        String recordsText = records.stream()
                .map(this::formatCareRecord)
                .collect(Collectors.joining("\n"));

        String briefing = geminiService.ask(recordsText);
        return saveBriefing(kid, date, briefing);
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
