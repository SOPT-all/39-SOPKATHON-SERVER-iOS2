package org.sopt.sopkathon.domain.kid.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.sopt.sopkathon.domain.kid.domain.Relationship;

public record UpdateParentRequest(
        @Schema(description = "학부모 이름 (선택, 최대 100자)", example = "박채영")
        @Pattern(regexp = ".*\\S.*", message = "학부모 이름은 공백일 수 없습니다.")
        @Size(max = 100, message = "학부모 이름은 100자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "학부모 휴대폰 번호 (선택)", example = "01012345678")
        @Pattern(regexp = "^\\d{2,3}-?\\d{3,4}-?\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        String phone,

        @Schema(description = "아이와의 관계 (선택, MOTHER, FATHER, GUARDIAN)", example = "MOTHER")
        Relationship relationship
) {
}
