package org.sopt.sopkathon.global.exception;

import lombok.Getter;
import org.sopt.sopkathon.global.code.BaseErrorCode;

@Getter
public class GeneralException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public GeneralException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}