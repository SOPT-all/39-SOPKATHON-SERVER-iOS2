package org.sopt.sopkathon.support.fixture;

import org.sopt.sopkathon.domain.kid.domain.Gender;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.sopt.sopkathon.domain.kid.domain.Relationship;
import org.sopt.sopkathon.domain.kid.dto.request.CreateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.ParentRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

public final class KidFixture {
    public static final Long KID_ID = 1L;
    public static final Long PARENT_ID = 1L;
    public static final LocalDate BIRTH_DATE = LocalDate.of(2021, 3, 15);

    private KidFixture() {
    }

    public static Kid kid() {
        return kid(KID_ID, "김솝트");
    }

    public static Kid kid(Long id, String name) {
        Kid kid = kid(name);
        ReflectionTestUtils.setField(kid, "id", id);
        return kid;
    }

    // id 없이 만든다. (@DataJpaTest에서 persist용)
    public static Kid kid(String name) {
        return Kid.builder()
                .name(name)
                .birthDate(BIRTH_DATE)
                .gender(Gender.FEMALE)
                .note("견과류 알레르기가 있어요.")
                .build();
    }

    public static Parent parent(Kid kid) {
        Parent parent = Parent.builder()
                .kid(kid)
                .name("김엄마")
                .phone("010-1234-5678")
                .relationship(Relationship.MOTHER)
                .build();
        ReflectionTestUtils.setField(parent, "id", PARENT_ID);
        return parent;
    }

    public static ParentRequest parentRequest() {
        return new ParentRequest("김엄마", "01012345678", Relationship.MOTHER);
    }

    public static CreateKidRequest createKidRequest(ParentRequest parent) {
        return createKidRequest("김솝트", BIRTH_DATE, Gender.FEMALE, parent);
    }

    public static CreateKidRequest createKidRequest(
            String name,
            LocalDate birthDate,
            Gender gender,
            ParentRequest parent
    ) {
        return new CreateKidRequest(name, birthDate, gender, parent, "견과류 알레르기가 있어요.");
    }
}
