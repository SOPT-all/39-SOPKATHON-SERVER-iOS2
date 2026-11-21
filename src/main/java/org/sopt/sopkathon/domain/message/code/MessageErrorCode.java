package org.sopt.sopkathon.domain.message.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessageErrorCode implements BaseErrorCode {

    KID_NOT_FOUND_OR_NOT_ASSIGNED(HttpStatus.NOT_FOUND, "KID4041", "존재하지 않거나 담당하지 않는 아이입니다."),
    NO_CARE_RECORDS(HttpStatus.UNPROCESSABLE_CONTENT, "BRIEFING4221", "해당 날짜에 돌봄 기록이 없습니다."),
    AI_RESPONSE_FAILED(HttpStatus.BAD_GATEWAY, "AI5021", "AI 서버 응답에 실패했습니다.");;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
