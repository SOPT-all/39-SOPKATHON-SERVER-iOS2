package org.sopt.sopkathon.domain.kid.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.sopt.sopkathon.domain.kid.domain.Relationship;
import org.sopt.sopkathon.domain.member.domain.Member;
import org.sopt.sopkathon.global.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sopt.sopkathon.support.fixture.KidFixture.kid;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@DisplayName("KidRepository 테스트")
class KidRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private KidRepository kidRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private MemberKidRepository memberKidRepository;

    @Nested
    @DisplayName("아이 삭제(delete)")
    class Delete {

        @Test
        @DisplayName("아이를 삭제하면 ON DELETE CASCADE로 보호자와 회원 연결이 함께 삭제된다")
        void 아이삭제_연관데이터_함께삭제() {
            Member member = em.persist(Member.builder().name("선생님").build());
            Kid kid = em.persist(kid("김솝트"));
            em.persist(MemberKid.builder().member(member).kid(kid).build());
            em.persist(Parent.builder()
                    .kid(kid)
                    .name("김엄마")
                    .phone("010-1234-5678")
                    .relationship(Relationship.MOTHER)
                    .build());
            em.flush();
            em.clear();

            kidRepository.delete(kidRepository.findById(kid.getId()).orElseThrow());
            em.flush();
            em.clear();

            assertThat(kidRepository.findById(kid.getId())).isEmpty();
            assertThat(parentRepository.findAllByKidIdOrderByIdAsc(kid.getId())).isEmpty();
            assertThat(memberKidRepository.existsByMemberIdAndKidId(member.getId(), kid.getId())).isFalse();
            assertThat(em.find(Member.class, member.getId())).isNotNull();
        }
    }
}
