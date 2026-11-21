package org.sopt.sopkathon.domain.kid.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.sopt.sopkathon.domain.kid.domain.Gender;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;

public record UpdateKidRequest(
        @Schema(description = "아이 이름 (선택, 최대 100자)", example = "노정완")
        @Pattern(regexp = ".*\\S.*", message = "아이 이름은 공백일 수 없습니다.")
        @Size(max = 100, message = "아이 이름은 100자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "생년월일 (선택, YYYY-MM-DD)", example = "2023-03-15")
        @PastOrPresent(message = "생년월일은 오늘 이후일 수 없습니다.")
        LocalDate birthDate,

        @Schema(description = "성별 (선택, MALE, FEMALE)", example = "FEMALE")
        Gender gender,

        @Schema(description = "학부모 정보 (선택, 변경할 필드만 전달)")
        @Valid
        UpdateParentRequest parent,

        @Schema(
                description = "특이사항 및 알레르기 정보 (선택, null을 보내면 삭제)",
                example = "우유 알레르기가 있습니다.",
                type = "string",
                nullable = true
        )
        JsonNode note
) {
    // note 필드를 보냈는지 (null을 보낸 것도 포함)
    public boolean hasNote() {
        return note != null;
    }

    // 보낸 note 값. null을 보냈으면 null
    public String noteValue() {
        return note == null || note.isNull() ? null : note.asString();
    }

    @JsonIgnore
    @AssertTrue(message = "특이사항은 문자열 또는 null이어야 합니다.")
    public boolean isNoteStringOrNull() {
        return note == null || note.isNull() || note.isString();
    }
}
