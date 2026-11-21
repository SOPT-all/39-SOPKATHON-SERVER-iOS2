package org.sopt.sopkathon.domain.kid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Kid;

public record KidResponse(
        @Schema(description = "아이 ID", example = "1")
        Long kidId,

        @Schema(description = "아이 이름", example = "노정완")
        String name
) {
    public static KidResponse of(Kid kid) {
        return new KidResponse(kid.getId(), kid.getName());
    }
}
