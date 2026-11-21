package org.sopt.sopkathon.domain.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.sopkathon.domain.record.dto.response.HomeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "홈 화면 조회", description = "홈 화면 조회 API")
@RequestMapping("/api/v1/home")
public interface HomeApi {

    @Operation(
            summary = "홈 화면 조회"
    )
    @PostMapping
    HomeResponse getHome(
            @RequestHeader("Member-Id") Long memberId
    );
}
