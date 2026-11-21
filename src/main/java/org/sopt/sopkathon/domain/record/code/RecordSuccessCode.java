package org.sopt.sopkathon.domain.record.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.global.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecordSuccessCode implements BaseSuccessCode {

    RECORD_CREATED(HttpStatus.CREATED, "RECORD2011", "돌봄 기록 저장에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
