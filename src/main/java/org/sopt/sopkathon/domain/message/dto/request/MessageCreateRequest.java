package org.sopt.sopkathon.domain.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MessageCreateRequest(
        @Schema(description = "아이 id", example = "1")
        @NotNull
        Long kidId,
        @Schema(description = "기록 날짜", example = "2026-11-21")
        @NotNull
        LocalDate date
) {
}
