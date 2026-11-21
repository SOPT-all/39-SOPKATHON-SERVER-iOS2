package org.sopt.sopkathon.domain.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.record.dto.request.RecordCreateRequest;
import org.sopt.sopkathon.domain.record.dto.response.RecordCreateResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "돌봄 기록", description = "돌봄 기록 API")
@RequestMapping("/api/v1/records")
public interface RecordApi {

    @Operation(
            summary = "돌봄 기록 생성",
            description = "교사가 입력한 텍스트를 분석하여 하나 이상의 돌봄 기록을 저장합니다."
    )
    @PostMapping
    RecordCreateResponse createRecords(
            @Parameter(description = "요청 회원 ID", example = "1", required = true)
            @RequestHeader("Member-Id") Long memberId,
            @Valid @RequestBody RecordCreateRequest request
    );
}
