package org.sopt.sopkathon.domain.kid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Kid;

public record CreateKidResponse(
        @Schema(description = "아이 ID", example = "1")
        Long kidId
) {
    public static CreateKidResponse of(Kid kid) {
        return new CreateKidResponse(kid.getId());
    }
}

