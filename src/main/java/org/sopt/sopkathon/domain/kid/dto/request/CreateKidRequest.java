package org.sopt.sopkathon.domain.kid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.sopt.sopkathon.domain.kid.domain.Gender;

import java.time.LocalDate;

public record CreateKidRequest(
        @Schema(description = "아이 이름 (최대 100자)", example = "노정완")
        @NotBlank(message = "아이 이름은 필수입니다.")
        @Size(max = 100, message = "아이 이름은 100자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "생년월일 (YYYY-MM-DD)", example = "2023-03-15")
        @NotNull(message = "생년월일은 필수입니다.")
        @PastOrPresent(message = "생년월일은 오늘 이후일 수 없습니다.")
        LocalDate birthDate,

        @Schema(description = "성별 (MALE, FEMALE)", example = "FEMALE")
        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @Schema(description = "학부모 정보")
        @NotNull(message = "학부모 정보는 필수입니다.")
        @Valid
        ParentRequest parent,

        @Schema(description = "특이사항 및 알레르기 정보 (선택)", example = "코코넛 알레르기가 있습니다.", nullable = true)
        String note
) {
}
