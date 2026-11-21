package org.sopt.sopkathon.domain.kid.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.member.domain.Member;
import org.sopt.sopkathon.global.config.JpaConfig;
import org.sopt.sopkathon.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sopt.sopkathon.support.fixture.KidFixture.kid;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class, QuerydslConfig.class, KidQueryRepository.class})
@DisplayName("KidQueryRepository 테스트")
class KidQueryRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private KidQueryRepository kidQueryRepository;

    private Member member;
    private Kid mingyo;
    private Kid hajun;
    private Kid jiho;

    // 담당 아이 3명(이하준, 김민교, 박지호) + 담당이지만 삭제된 아이 + 다른 회원의 아이
    @BeforeEach
    void setUp() {
        member = em.persist(Member.builder().name("선생님").build());
        Member otherMember = em.persist(Member.builder().name("다른 선생님").build());

        hajun = em.persist(kid("이하준"));
        mingyo = em.persist(kid("김민교"));
        jiho = em.persist(kid("박지호"));
        Kid deletedKid = kid("최지민");
        deletedKid.delete(LocalDateTime.of(2026, 11, 22, 9, 0));
        em.persist(deletedKid);
        Kid otherKid = em.persist(kid("정지아"));

        em.persist(MemberKid.builder().member(member).kid(hajun).build());
        em.persist(MemberKid.builder().member(member).kid(mingyo).build());
        em.persist(MemberKid.builder().member(member).kid(jiho).build());
        em.persist(MemberKid.builder().member(member).kid(deletedKid).build());
        em.persist(MemberKid.builder().member(otherMember).kid(otherKid).build());
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("담당 아이 목록 조회(findMyKids)")
    class FindMyKids {

        @Test
        @DisplayName("keyword가 없으면 담당하고 삭제되지 않은 아이 전체를 이름순으로 조회한다")
        void 키워드없음_전체_이름순조회() {
            List<Kid> result = kidQueryRepository.findMyKids(member.getId(), null);

            assertThat(result).extracting(Kid::getId)
                    .containsExactly(mingyo.getId(), jiho.getId(), hajun.getId());
        }

        @Test
        @DisplayName("keyword가 공백이면 조건 없이 전체를 조회한다")
        void 키워드공백_전체조회() {
            List<Kid> result = kidQueryRepository.findMyKids(member.getId(), "  ");

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("keyword가 있으면 이름에 keyword가 포함된 담당 아이만 이름순으로 조회한다")
        void 키워드있음_포함된아이만조회() {
            List<Kid> result = kidQueryRepository.findMyKids(member.getId(), "지");

            assertThat(result).extracting(Kid::getId)
                    .containsExactly(jiho.getId());
        }
    }
}
