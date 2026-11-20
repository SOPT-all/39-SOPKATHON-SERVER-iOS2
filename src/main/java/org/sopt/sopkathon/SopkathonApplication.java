package org.sopt.sopkathon;

import org.sopt.sopkathon.global.config.TimeConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SopkathonApplication {
    public static void main(String[] args) {
        // JVM 기본 시간대를 KST로 지정
        TimeZone.setDefault(TimeZone.getTimeZone(TimeConfig.KST));
        SpringApplication.run(SopkathonApplication.class, args);
    }
}
