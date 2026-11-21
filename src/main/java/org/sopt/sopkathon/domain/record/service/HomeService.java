package org.sopt.sopkathon.domain.record.service;

import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.domain.member.domain.Member;
import org.sopt.sopkathon.domain.member.repository.MemberRepository;
import org.sopt.sopkathon.domain.record.domain.Record;
import org.sopt.sopkathon.domain.record.dto.response.HomeResponse;
import org.sopt.sopkathon.domain.record.repository.RecordRepository;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HomeService {

    private final MemberRepository memberRepository;
    private final MemberKidRepository memberKidRepository;
    private final RecordRepository recordRepository;
    private final Clock clock;

    public HomeService(
            MemberRepository memberRepository,
            MemberKidRepository memberKidRepository,
            RecordRepository recordRepository,
            Clock clock
    ) {
        this.memberRepository = memberRepository;
        this.memberKidRepository = memberKidRepository;
        this.recordRepository = recordRepository;
        this.clock = clock;
    }

    public HomeResponse getHome(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Kid> kids = memberKidRepository.findAllByMemberIdAndKidDeletedAtIsNull(memberId).stream()
                .map(MemberKid::getKid)
                .toList();

        if (kids.isEmpty()) {
            return HomeResponse.builder()
                    .name(member.getName())
                    .unrecordedKids(List.of())
                    .todayRecords(List.of())
                    .build();
        }

        LocalDate today = LocalDate.now(clock);
        List<Record> records = recordRepository.findHomeRecords(
                kids.stream().map(Kid::getId).toList(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        Set<Long> recordedKidIds = records.stream()
                .map(record -> record.getKid().getId())
                .collect(Collectors.toSet());

        return HomeResponse.builder()
                .name(member.getName())
                .unrecordedKids(kids.stream()
                        .filter(kid -> !recordedKidIds.contains(kid.getId()))
                        .map(kid -> new HomeResponse.UnrecordedKid(kid.getId(), kid.getName()))
                        .toList())
                .todayRecords(records.stream()
                        .map(this::toTodayRecord)
                        .toList())
                .build();
    }

    private HomeResponse.TodayRecord toTodayRecord(Record record) {
        Kid kid = record.getKid();
        return new HomeResponse.TodayRecord(
                record.getId(),
                kid.getName(),
                (int) (kid.getId() % 4) + 1,
                record.getCategory(),
                record.getCreatedAt()
        );
    }
}
