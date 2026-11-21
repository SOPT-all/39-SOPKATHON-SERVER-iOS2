package org.sopt.sopkathon.domain.record.service;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.record.code.RecordErrorCode;
import org.sopt.sopkathon.domain.record.domain.Record;
import org.sopt.sopkathon.domain.record.domain.RecordCategory;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordExtraction;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordItem;
import org.sopt.sopkathon.domain.record.dto.request.RecordCreateRequest;
import org.sopt.sopkathon.domain.record.dto.response.RecordCreateResponse;
import org.sopt.sopkathon.domain.record.repository.RecordRepository;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private final MemberKidRepository memberKidRepository;
    private final RecordRepository recordRepository;
    private final RecordAiExtractor recordAiExtractor;
    private final Clock clock;

    @Transactional
    public RecordCreateResponse createRecords(Long memberId, RecordCreateRequest request) {
        LocalDateTime recordedAt = request.recordedAt() != null
                ? request.recordedAt()
                : LocalDateTime.now(clock);

        List<Record> records = isDirectInput(request)
                ? createDirectRecord(memberId, request, recordedAt)
                : createAiRecords(memberId, request, recordedAt);

        return RecordCreateResponse.of(recordRepository.saveAll(records));
    }

    private boolean isDirectInput(RecordCreateRequest request) {
        return request.kidId() != null && request.category() != null;
    }

    private List<Record> createDirectRecord(
            Long memberId,
            RecordCreateRequest request,
            LocalDateTime recordedAt
    ) {
        Kid kid = findAssignedKid(memberId, request.kidId());

        return List.of(createRecord(
                kid,
                request.category(),
                recordedAt
        ));
    }

    private List<Record> createAiRecords(
            Long memberId,
            RecordCreateRequest request,
            LocalDateTime recordedAt
    ) {
        Kid fixedKid = request.kidId() != null
                ? findAssignedKid(memberId, request.kidId())
                : null;
        List<MemberKid> assignedMemberKids = memberKidRepository.findAllByMemberId(memberId);
        List<String> assignedKidNames = fixedKid != null
                ? List.of(fixedKid.getName())
                : assignedMemberKids.stream()
                        .map(memberKid -> memberKid.getKid().getName())
                        .toList();

        AiRecordExtraction extraction = recordAiExtractor.extract(
                request.text(),
                assignedKidNames,
                fixedKid == null,
                request.category() == null
        );

        return extraction.records().stream()
                .map(item -> createRecord(
                        fixedKid != null
                                ? fixedKid
                                : findAssignedKidByName(assignedMemberKids, item),
                        request.category() != null
                                ? request.category()
                                : item.category(),
                        recordedAt
                ))
                .toList();
    }

    private Kid findAssignedKid(Long memberId, Long kidId) {
        return memberKidRepository.findByMemberIdAndKidId(memberId, kidId)
                .map(MemberKid::getKid)
                .orElseThrow(() -> new GeneralException(
                        RecordErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED
                ));
    }

    private Kid findAssignedKidByName(
            List<MemberKid> assignedMemberKids,
            AiRecordItem item
    ) {
        List<Kid> matchedKids = assignedMemberKids.stream()
                .map(MemberKid::getKid)
                .filter(kid -> kid.getName().equals(item.kidName()))
                .toList();

        if (matchedKids.size() != 1) {
            throw new GeneralException(RecordErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED);
        }

        return matchedKids.getFirst();
    }

    private Record createRecord(
            Kid kid,
            RecordCategory category,
            LocalDateTime recordedAt
    ) {
        return Record.builder()
                .kid(kid)
                .category(category.name())
                .recordedAt(recordedAt)
                .build();
    }
}
