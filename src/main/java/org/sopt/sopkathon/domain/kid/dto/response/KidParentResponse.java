package org.sopt.sopkathon.domain.kid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.sopt.sopkathon.domain.kid.domain.Relationship;

public record KidParentResponse(
        @Schema(description = "학부모 이름", example = "박채영")
        String name,

        @Schema(description = "학부모 휴대폰 번호", example = "01012345678")
        String phone,

        @Schema(description = "아이와의 관계 (MOTHER, FATHER, GUARDIAN)", example = "MOTHER")
        Relationship relationship
) {
    public static KidParentResponse of(Parent parent) {
        return new KidParentResponse(parent.getName(), parent.getPhone(), parent.getRelationship());
    }
}
