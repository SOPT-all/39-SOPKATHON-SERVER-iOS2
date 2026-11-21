package org.sopt.sopkathon.domain.kid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.sopt.sopkathon.domain.kid.domain.Relationship;

public record ParentRequest(
        @Schema(description = "학부모 이름 (최대 100자)", example = "박채영")
        @NotBlank(message = "학부모 이름은 필수입니다.")
        @Size(max = 100, message = "학부모 이름은 100자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "학부모 휴대폰 번호", example = "01012345678")
        @NotBlank(message = "학부모 휴대폰 번호는 필수입니다.")
        @Pattern(regexp = "^\\d{2,3}-?\\d{3,4}-?\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        String phone,

        @Schema(description = "아이와의 관계 (MOTHER, FATHER, GUARDIAN)", example = "MOTHER")
        @NotNull(message = "아이와의 관계는 필수입니다.")
        Relationship relationship
) {
}
