package org.sopt.sopkathon.domain.briefing.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BriefingSuccessCode implements BaseSuccessCode {
    // 200
    BRIEFING_LIST_OK(HttpStatus.OK, "BRIEFING2001", "일일 브리핑 조회에 성공했습니다."),
    BRIEFING_REFINED(HttpStatus.OK, "BRIEFING2002", "AI 메시지 다듬기에 성공했습니다."),

    // 201
    BRIEFING_CREATED(HttpStatus.CREATED, "BRIEFING2011", "AI 메시지 생성에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
