package org.sopt.sopkathon.domain.kid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Kid;

public record UpdateKidResponse(
        @Schema(description = "수정한 아이 ID", example = "1")
        Long kidId
) {
    public static UpdateKidResponse of(Kid kid) {
        return new UpdateKidResponse(kid.getId());
    }
}
