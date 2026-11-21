package org.sopt.sopkathon.domain.image.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {

    S3_PRESIGNED_URL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE5001", "이미지 업로드 URL을 발급하지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
