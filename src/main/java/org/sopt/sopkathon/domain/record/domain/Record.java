package org.sopt.sopkathon.domain.record.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.global.domain.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "record",
        indexes = @Index(columnList = "kid_id, recorded_at")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kid_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Kid kid;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Builder
    private Record(Kid kid, String category, LocalDateTime recordedAt) {
        this.kid = kid;
        this.category = category;
        this.recordedAt = recordedAt;
    }
}
