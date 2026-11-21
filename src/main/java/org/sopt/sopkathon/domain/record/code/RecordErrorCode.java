package org.sopt.sopkathon.domain.record.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements BaseErrorCode {

    KID_NOT_FOUND_OR_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "KID4041", "존재하지 않거나 담당하지 않는 아이입니다."),
    RECORD_EXTRACTION_FAILED(HttpStatus.BAD_REQUEST, "RECORD4221", "텍스트에서 아이 또는 카테고리를 찾지 못했습니다."),
    AI_SERVER_RESPONSE_FAILED(HttpStatus.BAD_GATEWAY, "AI5021", "AI 서버 응답에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
