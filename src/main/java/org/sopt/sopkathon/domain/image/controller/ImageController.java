package org.sopt.sopkathon.domain.image.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.image.code.ApiImageErrors;
import org.sopt.sopkathon.domain.image.code.ImageErrorCode;
import org.sopt.sopkathon.domain.image.dto.request.PresignedUrlRequest;
import org.sopt.sopkathon.domain.image.dto.response.PresignedUrlResponse;
import org.sopt.sopkathon.domain.image.service.ImageService;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.code.CommonSuccessCode;
import org.sopt.sopkathon.global.swagger.ApiCommonErrors;
import org.sopt.sopkathon.global.swagger.ApiCommonSuccess;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController implements ImageApi {

    private final ImageService imageService;

    @Override
    @ApiCommonSuccess(CommonSuccessCode.OK)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiImageErrors({ImageErrorCode.S3_PRESIGNED_URL_FAILED})
    public PresignedUrlResponse getPresignedUrl(PresignedUrlRequest request) {
        return imageService.getPresignedUrl(request);
    }
}
