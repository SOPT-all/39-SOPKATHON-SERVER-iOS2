package org.sopt.sopkathon.domain.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PresignedUrlRequest(
        @Schema(description = "업로드할 이미지 파일명", example = "sample.jpg")
        @NotBlank(message = "파일명은 필수입니다.")
        @Size(max = 255, message = "파일명은 255자를 초과할 수 없습니다.")
        String fileName,

        @Schema(description = "이미지의 Content-Type. JPEG와 PNG를 지원합니다.", example = "image/jpeg")
        @NotBlank(message = "Content-Type은 필수입니다.")
        @Pattern(
                regexp = "image/(jpeg|png)",
                message = "JPEG 또는 PNG 이미지만 업로드할 수 있습니다."
        )
        String contentType
) {
}
