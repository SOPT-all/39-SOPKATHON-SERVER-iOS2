package org.sopt.sopkathon.domain.kid.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.sopt.sopkathon.domain.member.domain.Member;

@Getter
@Entity
@Table(
        name = "member_kid",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "kid_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberKid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kid_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Kid kid;

    @Builder
    private MemberKid(Member member, Kid kid) {
        this.member = member;
        this.kid = kid;
    }
}
