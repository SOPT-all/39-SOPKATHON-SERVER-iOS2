package org.sopt.sopkathon.domain.kid.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum KidErrorCode implements BaseErrorCode {
    KID_NOT_FOUND(HttpStatus.NOT_FOUND, "KID4041", "존재하지 않는 아이입니다."),
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PARENT4041", "아이의 학부모 정보가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
