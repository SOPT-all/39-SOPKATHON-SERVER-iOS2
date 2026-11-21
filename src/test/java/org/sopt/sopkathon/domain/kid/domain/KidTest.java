package org.sopt.sopkathon.domain.kid.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sopt.sopkathon.support.fixture.KidFixture.BIRTH_DATE;
import static org.sopt.sopkathon.support.fixture.KidFixture.kid;
import static org.sopt.sopkathon.support.fixture.KidFixture.parent;

@DisplayName("Kid / Parent 도메인 테스트")
class KidTest {

    @Nested
    @DisplayName("아이 기본 정보 수정(updateProfile)")
    class UpdateProfile {

        @Test
        @DisplayName("전달한 값만 바꾸고 null인 값은 기존 값을 유지한다")
        void 전달값만_수정() {
            Kid kid = kid();

            kid.updateProfile("노정완", null, Gender.MALE);

            assertThat(kid.getName()).isEqualTo("노정완");
            assertThat(kid.getBirthDate()).isEqualTo(BIRTH_DATE);
            assertThat(kid.getGender()).isEqualTo(Gender.MALE);
        }

        @Test
        @DisplayName("생년월일을 전달하면 생년월일을 바꾼다")
        void 생년월일_수정() {
            Kid kid = kid();

            kid.updateProfile(null, LocalDate.of(2022, 1, 1), null);

            assertThat(kid.getBirthDate()).isEqualTo(LocalDate.of(2022, 1, 1));
            assertThat(kid.getName()).isEqualTo("김솝트");
        }
    }

    @Nested
    @DisplayName("특이사항 수정(updateNote)")
    class UpdateNote {

        @Test
        @DisplayName("null을 전달하면 특이사항을 삭제한다")
        void null_삭제() {
            Kid kid = kid();

            kid.updateNote(null);

            assertThat(kid.getNote()).isNull();
        }
    }

    @Nested
    @DisplayName("아이 삭제(delete)")
    class Delete {

        @Test
        @DisplayName("삭제하면 deletedAt에 시각이 기록되고 삭제 상태가 된다")
        void 삭제_시각기록() {
            Kid kid = kid();
            LocalDateTime now = LocalDateTime.of(2026, 11, 22, 9, 0);

            kid.delete(now);

            assertThat(kid.getDeletedAt()).isEqualTo(now);
            assertThat(kid.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("학부모 정보 수정(Parent.update)")
    class UpdateParent {

        @Test
        @DisplayName("전달한 값만 바꾸고 null인 값은 기존 값을 유지한다")
        void 전달값만_수정() {
            Parent parent = parent(kid());

            parent.update(null, "01098765432", Relationship.FATHER);

            assertThat(parent.getName()).isEqualTo("김엄마");
            assertThat(parent.getPhone()).isEqualTo("01098765432");
            assertThat(parent.getRelationship()).isEqualTo(Relationship.FATHER);
        }
    }
}
