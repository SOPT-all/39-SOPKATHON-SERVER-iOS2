package org.sopt.sopkathon.domain.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.sopt.sopkathon.domain.record.domain.RecordCategory;

import java.time.LocalDateTime;

public record RecordCreateRequest(
        @Schema(
                description = "직접 선택한 아이 ID. 음성 메모에서는 생략할 수 있습니다.",
                example = "1",
                nullable = true
        )
        @Positive(message = "아이 ID는 양수여야 합니다.")
        Long kidId,

        @Schema(
                description = "직접 선택한 돌봄 기록 카테고리. 음성 메모에서는 생략할 수 있습니다.",
                example = "FEEDING",
                nullable = true
        )
        RecordCategory category,

        @Schema(
                description = "음성에서 변환되었거나 교사가 직접 입력한 돌봄 기록 내용",
                example = "지우는 우유 먹었고 하준이는 낮잠 잤어요"
        )
        @NotBlank(message = "돌봄 기록 내용은 필수입니다.")
        String text,

        @Schema(
                description = "돌봄 기록 시각. 생략하면 요청 시각을 사용합니다.",
                example = "2026-11-21T13:20:00",
                nullable = true
        )
        LocalDateTime recordedAt
) {
}
