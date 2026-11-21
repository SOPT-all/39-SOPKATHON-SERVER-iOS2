package org.sopt.sopkathon.domain.briefing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Parent;

public record BriefingParentResponse(
        @Schema(description = "학부모 이름", example = "김OO")
        String name,

        @Schema(description = "학부모 연락처, 문자 발송에 사용", example = "010-1234-5678")
        String phone
) {
    public static BriefingParentResponse of(Parent parent) {
        return new BriefingParentResponse(parent.getName(), parent.getPhone());
    }
}
