package org.sopt.sopkathon.domain.briefing.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BriefingErrorCode implements BaseErrorCode {
    // 404
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BRIEFING4041", "존재하지 않는 브리핑 메시지입니다."),

    // 422
    KID_NOT_FOUND(HttpStatus.NOT_FOUND, "BRIEFING4221", "해당 날짜에 돌봄 기록이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
