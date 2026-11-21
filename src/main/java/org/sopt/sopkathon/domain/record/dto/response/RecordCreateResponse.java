package org.sopt.sopkathon.domain.record.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.record.domain.Record;

import java.util.List;

public record RecordCreateResponse(
        @Schema(description = "저장된 돌봄 기록 목록")
        List<RecordCreateItemResponse> records
) {

    public static RecordCreateResponse of(List<Record> records) {
        return new RecordCreateResponse(
                records.stream()
                        .map(RecordCreateItemResponse::of)
                        .toList()
        );
    }
}
