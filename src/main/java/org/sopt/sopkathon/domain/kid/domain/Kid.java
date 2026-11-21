package org.sopt.sopkathon.domain.kid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.sopkathon.global.domain.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "kid")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Kid extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDateTime deletedAt;

    @Builder
    private Kid(String name, LocalDate birthDate, Gender gender, String note) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.note = note;
    }

    public void updateProfile(String name, LocalDate birthDate, Gender gender) {
        if (name != null) {
            this.name = name;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (gender != null) {
            this.gender = gender;
        }
    }

    // note(특이사항)은 null로 지울 수 있음
    public void updateNote(String note) {
        this.note = note;
    }

    public void delete(LocalDateTime now) {
        this.deletedAt = now;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
