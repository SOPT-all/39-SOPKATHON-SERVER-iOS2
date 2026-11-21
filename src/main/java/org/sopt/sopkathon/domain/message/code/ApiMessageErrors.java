package org.sopt.sopkathon.domain.message.code;

import org.sopt.sopkathon.global.swagger.ErrorCodeMarker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ErrorCodeMarker
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMessageErrors {
    MessageErrorCode[] value();
}
