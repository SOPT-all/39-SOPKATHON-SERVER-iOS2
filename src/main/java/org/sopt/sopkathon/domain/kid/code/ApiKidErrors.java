package org.sopt.sopkathon.domain.kid.code;

import org.sopt.sopkathon.global.swagger.ErrorCodeMarker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ErrorCodeMarker
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiKidErrors {
    KidErrorCode[] value();
}
