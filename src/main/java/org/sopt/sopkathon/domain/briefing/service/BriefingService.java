package org.sopt.sopkathon.domain.briefing.service;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.briefing.code.BriefingErrorCode;
import org.sopt.sopkathon.domain.briefing.domain.Briefing;
import org.sopt.sopkathon.domain.briefing.dto.request.BriefingCreateRequest;
import org.sopt.sopkathon.domain.briefing.dto.request.BriefingRefineRequest;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingContentResponse;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingKidResponse;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingListResponse;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingParentResponse;
import org.sopt.sopkathon.domain.briefing.repository.BriefingQueryRepository;
import org.sopt.sopkathon.domain.briefing.repository.BriefingRepository;
import org.sopt.sopkathon.domain.briefing.repository.RecordCount;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.sopt.sopkathon.domain.gemini.service.GeminiService;
import org.sopt.sopkathon.domain.kid.repository.KidRepository;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.kid.repository.ParentRepository;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.domain.member.repository.MemberRepository;
import org.sopt.sopkathon.domain.record.domain.Record;
import org.sopt.sopkathon.domain.record.repository.RecordRepository;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BriefingService {
    private final BriefingQueryRepository briefingQueryRepository;
    private final BriefingRepository briefingRepository;
    private final KidRepository kidRepository;
    private final ParentRepository parentRepository;
    private final MemberRepository memberRepository;
    private final MemberKidRepository memberKidRepository;
    private final RecordRepository recordRepository;
    private final GeminiService geminiService;
    private final Clock clock;

    // 일일 브리핑 목록 조회
    public BriefingListResponse getBriefings(Long memberId) {
        validateMemberExists(memberId);

        LocalDate date = LocalDate.now(clock);

        Map<Long, Map<String, Long>> summaries = briefingQueryRepository
                .countMyKidsRecords(memberId, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .stream()
                .collect(Collectors.groupingBy(
                        RecordCount::kidId,
                        Collectors.toMap(RecordCount::category, RecordCount::count,
                                Long::sum, LinkedHashMap::new)
                ));

        if (summaries.isEmpty()) {
            return BriefingListResponse.of(date, List.of());
        }

        Set<Long> kidIds = summaries.keySet();
        Map<Long, Parent> parents = findFirstParents(kidIds);
        Map<Long, Briefing> briefings = briefingRepository.findAllByKidIdInAndBriefingDate(kidIds, date).stream()
                .collect(Collectors.toMap(briefing -> briefing.getKid().getId(), Function.identity()));

        List<BriefingKidResponse> kids = kidRepository.findAllById(kidIds).stream()
                .sorted(Comparator.comparing(Kid::getName).thenComparing(Kid::getId))
                .map(kid -> toKidResponse(kid, date, summaries.get(kid.getId()),
                        parents.get(kid.getId()), briefings.get(kid.getId())))
                .toList();

        return BriefingListResponse.of(date, kids);
    }

    // 돌봄 기록으로 일일 브리핑 생성 (같은 날짜 브리핑이 있으면 덮어씀)
    @Transactional
    public BriefingContentResponse createBriefing(Long memberId, BriefingCreateRequest request) {
        Long kidId = request.kidId();
        if (!memberKidRepository.existsByMemberIdAndKidId(memberId, kidId)) {
            throw new GeneralException(BriefingErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED);
        }
        Kid kid = kidRepository.findByIdAndDeletedAtIsNull(kidId)
                .orElseThrow(() -> new GeneralException(BriefingErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED));

        LocalDate date = request.date();
        List<Record> records = recordRepository
                .findByKid_IdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
                        kid.getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay()
                );
        if (records.isEmpty()) {
            throw new GeneralException(BriefingErrorCode.NO_CARE_RECORDS);
        }

        String recordsText = records.stream()
                .map(this::formatCareRecord)
                .collect(Collectors.joining("\n"));
        String content = askAi(() -> geminiService.ask(recordsText));

        Briefing briefing = briefingRepository.findByKidIdAndBriefingDate(kid.getId(), date)
                .map(existing -> {
                    existing.updateContent(content);
                    return existing;
                })
                .orElseGet(() -> briefingRepository.save(Briefing.builder()
                        .kid(kid)
                        .briefingDate(date)
                        .content(content)
                        .build()));

        return BriefingContentResponse.of(briefing);
    }

    // AI로 브리핑 메시지 다듬기
    @Transactional
    public BriefingContentResponse refineBriefing(Long memberId, Long briefingId, BriefingRefineRequest request) {
        Briefing briefing = briefingRepository.findById(briefingId)
                .orElseThrow(() -> new GeneralException(BriefingErrorCode.BRIEFING_NOT_FOUND));

        Kid kid = briefing.getKid();
        if (kid.isDeleted() || !memberKidRepository.existsByMemberIdAndKidId(memberId, kid.getId())) {
            throw new GeneralException(BriefingErrorCode.BRIEFING_NOT_FOUND);
        }

        String refinedContent = askAi(() -> geminiService.refine(briefing.getContent(), request.instruction()));

        briefing.updateContent(refinedContent);
        return BriefingContentResponse.of(briefing);
    }

    // AI 호출 실패나 빈 응답은 AI_RESPONSE_FAILED로 변환
    private String askAi(Supplier<String> call) {
        String result;
        try {
            result = call.get();
        } catch (RuntimeException exception) {
            GeneralException error = new GeneralException(BriefingErrorCode.AI_RESPONSE_FAILED);
            error.initCause(exception);
            throw error;
        }
        if (result == null || result.isBlank()) {
            throw new GeneralException(BriefingErrorCode.AI_RESPONSE_FAILED);
        }
        return result;
    }

    private String formatCareRecord(Record record) {
        return "%s %s".formatted(record.getRecordedAt(), record.getCategory());
    }

    private BriefingKidResponse toKidResponse(
            Kid kid,
            LocalDate date,
            Map<String, Long> summary,
            Parent parent,
            Briefing briefing
    ) {
        long recordCount = summary.values().stream().mapToLong(Long::longValue).sum();
        return new BriefingKidResponse(
                kid.getId(),
                kid.getName(),
                ageAt(kid.getBirthDate(), date),
                recordCount,
                summary,
                parent == null ? null : BriefingParentResponse.of(parent),
                briefing == null ? null : BriefingContentResponse.of(briefing)
        );
    }

    // 브리핑 날짜 기준 만 나이
    private int ageAt(LocalDate birthDate, LocalDate date) {
        return Math.max(0, Period.between(birthDate, date).getYears());
    }

    // 아이마다 먼저 등록된 학부모 1명
    private Map<Long, Parent> findFirstParents(Set<Long> kidIds) {
        return parentRepository.findAllByKidIdInOrderByIdAsc(kidIds).stream()
                .collect(Collectors.toMap(parent -> parent.getKid().getId(), Function.identity(),
                        (first, ignored) -> first));
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }
}
