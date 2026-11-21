package org.sopt.sopkathon.global.config.s3;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.s3")
@Validated
public record S3Properties(
        @NotBlank String bucket,
        @NotBlank String region,
        String uploadPrefix,
        @Min(1) long presignedUrlExpirationMinutes
) {
}
