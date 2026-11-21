package org.sopt.sopkathon.domain.kid.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.kid.dto.request.CreateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.response.UpdateKidResponse;
import org.springframework.web.bind.annotation.PatchMapping;
import org.sopt.sopkathon.domain.kid.dto.response.CreateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.DeleteKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidDetailResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidListResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "아이 API", description = "아이 관련 API 입니다")
@RequestMapping(path = "/api/v1/kids")
public interface KidApi {
    @Operation(summary = "아이 목록 조회 및 검색", description = "내가 담당하는 아이 목록을 이름순으로 조회합니다. keyword가 있으면 이름에 keyword가 포함된 아이만 조회합니다.")
    @GetMapping
    KidListResponse getKids(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Parameter(description = "아이 이름 검색어, 미입력 시 전체", example = "정완")
            @RequestParam(required = false) String keyword
    );

    @Operation(summary = "아이 정보 조회", description = "아이 정보 수정 화면에 채울 기존 아이 정보와 학부모 연락처를 조회합니다.")
    @GetMapping("/{kidId}")
    KidDetailResponse getKid(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Parameter(description = "아이 ID", example = "1")
            @PathVariable Long kidId
    );

    @Operation(summary = "아이 등록", description = "아이 정보와 학부모 연락처를 등록합니다.")
    @PostMapping
    CreateKidResponse createKid(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Valid @RequestBody CreateKidRequest request
    );

    @Operation(summary = "아이 정보 수정", description = "아이 정보와 학부모 연락처를 수정합니다. 보내지 않은 필드는 기존 값을 유지합니다.")
    @PatchMapping("/{kidId}")
    UpdateKidResponse updateKid(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Parameter(description = "아이 ID", example = "1")
            @PathVariable Long kidId,
            @Valid @RequestBody UpdateKidRequest request
    );

    @Operation(summary = "아이 삭제", description = "아이를 삭제 합니다. soft delete를 사용합니다.")
    @DeleteMapping("/{kidId}")
    DeleteKidResponse deleteKid(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Parameter(description = "아이 ID", example = "1")
            @PathVariable Long kidId
    );
}
