package org.sopt.sopkathon.domain.kid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Gender;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.Parent;

import java.time.LocalDate;

public record KidDetailResponse(
        @Schema(description = "아이 ID", example = "1")
        Long kidId,

        @Schema(description = "아이 이름", example = "노정완")
        String name,

        @Schema(description = "생년월일 (YYYY-MM-DD)", example = "2023-03-15")
        LocalDate birthDate,

        @Schema(description = "성별 (MALE, FEMALE)", example = "FEMALE")
        Gender gender,

        @Schema(description = "특이사항 및 알레르기 정보, 없으면 null", example = "우유 알레르기가 있습니다.", nullable = true)
        String note,

        @Schema(description = "학부모 정보, 등록된 학부모가 없으면 null", nullable = true)
        KidParentResponse parent
) {
    public static KidDetailResponse of(Kid kid, Parent parent) {
        return new KidDetailResponse(
                kid.getId(),
                kid.getName(),
                kid.getBirthDate(),
                kid.getGender(),
                kid.getNote(),
                parent == null ? null : KidParentResponse.of(parent)
        );
    }
}
