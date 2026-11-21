package org.sopt.sopkathon.global.config.s3;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.s3")
public record S3Properties(
        @NotBlank String bucket,
        @NotBlank String region,
        String uploadPrefix,
        @Min(1) long presignedUrlExpirationMinutes
) {
}
