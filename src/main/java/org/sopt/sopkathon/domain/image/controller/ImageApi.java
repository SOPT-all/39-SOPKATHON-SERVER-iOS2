package org.sopt.sopkathon.domain.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.image.dto.request.PresignedUrlRequest;
import org.sopt.sopkathon.domain.image.dto.response.PresignedUrlResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "이미지", description = "이미지 업로드 API")
@RequestMapping("/api/v1/images")
public interface ImageApi {

    @Operation(
            summary = "이미지 업로드 URL 발급",
            description = "S3에 이미지를 직접 업로드할 수 있는 5분짜리 PUT URL을 발급합니다."
    )
    @PostMapping("/presigned-url")
    PresignedUrlResponse getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    );
}
