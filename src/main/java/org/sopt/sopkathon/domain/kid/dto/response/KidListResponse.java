package org.sopt.sopkathon.domain.kid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Kid;

import java.util.List;

public record KidListResponse(
        @Schema(description = "아이 목록")
        List<KidResponse> kids
) {
    public static KidListResponse of(List<Kid> kids) {
        return new KidListResponse(kids.stream()
                .map(KidResponse::of)
                .toList());
    }
}
