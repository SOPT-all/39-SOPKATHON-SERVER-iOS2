package org.sopt.sopkathon.domain.briefing.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.member.domain.Member;
import org.sopt.sopkathon.domain.record.domain.Record;
import org.sopt.sopkathon.global.config.JpaConfig;
import org.sopt.sopkathon.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sopt.sopkathon.support.fixture.KidFixture.kid;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class, QuerydslConfig.class, BriefingQueryRepository.class})
@DisplayName("BriefingQueryRepository 테스트")
class BriefingQueryRepositoryTest {
    private static final LocalDate DATE = LocalDate.of(2026, 11, 21);
    private static final LocalDateTime START = DATE.atStartOfDay();
    private static final LocalDateTime END = DATE.plusDays(1).atStartOfDay();

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BriefingQueryRepository briefingQueryRepository;

    private Member member;
    private Kid jiwoo;
    private Kid hajun;

    @BeforeEach
    void setUp() {
        member = em.persist(Member.builder().name("선생님").build());
        Member otherMember = em.persist(Member.builder().name("다른 선생님").build());

        jiwoo = em.persist(kid("김민교"));
        hajun = em.persist(kid("이하준"));
        Kid noRecordKid = em.persist(kid("박지호"));
        Kid deletedKid = kid("최지민");
        deletedKid.delete(LocalDateTime.of(2026, 11, 21, 8, 0));
        em.persist(deletedKid);
        Kid otherKid = em.persist(kid("정지아"));

        em.persist(MemberKid.builder().member(member).kid(jiwoo).build());
        em.persist(MemberKid.builder().member(member).kid(hajun).build());
        em.persist(MemberKid.builder().member(member).kid(noRecordKid).build());
        em.persist(MemberKid.builder().member(member).kid(deletedKid).build());
        em.persist(MemberKid.builder().member(otherMember).kid(otherKid).build());

        // 민교: 당일 FEEDING 2, SLEEP 1 (00:00 정각 포함)
        persistRecord(jiwoo, "FEEDING", START);
        persistRecord(jiwoo, "FEEDING", DATE.atTime(12, 0));
        persistRecord(jiwoo, "SLEEP", DATE.atTime(13, 0));
        // 민교: 전날 23:59, 다음날 00:00 정각은 제외
        persistRecord(jiwoo, "FEEDING", START.minusMinutes(1));
        persistRecord(jiwoo, "FEEDING", END);
        // 하준: 당일 DIAPER 1
        persistRecord(hajun, "DIAPER", DATE.atTime(23, 59, 59));
        // 삭제된 아이, 다른 회원의 아이는 제외
        persistRecord(deletedKid, "FEEDING", DATE.atTime(9, 0));
        persistRecord(otherKid, "FEEDING", DATE.atTime(9, 0));

        em.flush();
        em.clear();
    }

    private void persistRecord(Kid kid, String category, LocalDateTime recordedAt) {
        em.persist(Record.builder().kid(kid).category(category).recordedAt(recordedAt).build());
    }

    @Nested
    @DisplayName("담당 아이 기록 집계(countMyKidsRecords)")
    class CountMyKidsRecords {

        @Test
        @DisplayName("해당 날짜 [00:00, 다음날 00:00) 기록만 아이·카테고리별로 센다")
        void 당일기록만_아이별_카테고리별_집계() {
            List<RecordCount> result = briefingQueryRepository.countMyKidsRecords(member.getId(), START, END);

            assertThat(result).containsExactlyInAnyOrder(
                    new RecordCount(jiwoo.getId(), "FEEDING", 2L),
                    new RecordCount(jiwoo.getId(), "SLEEP", 1L),
                    new RecordCount(hajun.getId(), "DIAPER", 1L)
            );
        }

        @Test
        @DisplayName("기록이 없는 날짜면 빈 목록을 반환한다")
        void 기록없는날짜_빈목록() {
            LocalDate otherDate = DATE.minusDays(3);

            List<RecordCount> result = briefingQueryRepository.countMyKidsRecords(
                    member.getId(), otherDate.atStartOfDay(), otherDate.plusDays(1).atStartOfDay());

            assertThat(result).isEmpty();
        }
    }
}
