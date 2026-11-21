package org.sopt.sopkathon.domain.message.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessageSuccessCode implements BaseSuccessCode {

    BRIEFING_CREATED(HttpStatus.CREATED, "BRIEFING2011", "AI 메시지 생성에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
