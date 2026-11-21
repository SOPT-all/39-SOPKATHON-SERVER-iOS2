package org.sopt.sopkathon.domain.kid.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum KidSuccessCode implements BaseSuccessCode {
    KID_LIST_OK(HttpStatus.OK, "KID2001", "아이 목록 조회에 성공했습니다."),
    KID_CREATED(HttpStatus.OK, "KID2002", "아이 등록에 성공했습니다."),
    KID_UPDATED_OK(HttpStatus.OK, "KID2003", "아이 정보를 수정했습니다."),
    KID_DELETED_OK(HttpStatus.OK, "KID2004", "아이 정보를 삭제했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
