package org.sopt.sopkathon.domain.record.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.record.domain.Record;

import java.time.LocalDateTime;

public record RecordCreateItemResponse(
        @Schema(description = "저장된 돌봄 기록 ID", example = "11")
        Long recordId,

        @Schema(description = "아이 ID", example = "1")
        Long kidId,

        @Schema(description = "아이 이름", example = "김지우")
        String kidName,

        @Schema(description = "돌봄 기록 카테고리", example = "FEEDING")
        String category,

        @Schema(description = "돌봄 기록 시각", example = "2026-11-21T13:20:00")
        LocalDateTime recordedAt
) {

    public static RecordCreateItemResponse of(Record record) {
        return new RecordCreateItemResponse(
                record.getId(),
                record.getKid().getId(),
                record.getKid().getName(),
                record.getCategory(),
                record.getRecordedAt()
        );
    }
}
