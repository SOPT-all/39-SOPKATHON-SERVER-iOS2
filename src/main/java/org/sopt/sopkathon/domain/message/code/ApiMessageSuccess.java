package org.sopt.sopkathon.domain.message.code;

import org.sopt.sopkathon.global.swagger.SuccessCodeMarker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuccessCodeMarker
public @interface ApiMessageSuccess {

    MessageSuccessCode value();
}
