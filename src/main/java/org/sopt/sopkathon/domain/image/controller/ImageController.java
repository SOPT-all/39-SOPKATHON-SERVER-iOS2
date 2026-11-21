package org.sopt.sopkathon.domain.image.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.image.dto.request.PresignedUrlRequest;
import org.sopt.sopkathon.domain.image.dto.response.PresignedUrlResponse;
import org.sopt.sopkathon.domain.image.service.ImageService;
import org.sopt.sopkathon.global.code.CommonSuccessCode;
import org.sopt.sopkathon.global.swagger.ApiCommonSuccess;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController implements ImageApi {

    private final ImageService imageService;

    @Override
    @ApiCommonSuccess(CommonSuccessCode.OK)
    public PresignedUrlResponse getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        return imageService.getPresignedUrl(request);
    }
}
