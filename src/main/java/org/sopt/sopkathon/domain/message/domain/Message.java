package org.sopt.sopkathon.domain.message.domain;

import jakarta.persistence.Column;
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
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Table(
        name = "message",
        uniqueConstraints = @UniqueConstraint(columnNames = {"kid_id", "briefing_date"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kid_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Kid kid;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDate briefingDate;

    @Builder
    private Message(Kid kid, String content, LocalDate briefingDate) {
        this.kid = kid;
        this.content = content;
        this.briefingDate = briefingDate;
    }
}
