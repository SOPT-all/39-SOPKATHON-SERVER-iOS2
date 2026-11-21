package org.sopt.sopkathon.domain.message.repository;

import org.sopt.sopkathon.domain.message.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByKid_IdAndBriefingDate(Long kidId, LocalDate briefingDate);

    List<Record> findByKid_IdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
            Long kidId,
            LocalDateTime start,
            LocalDateTime end
    );
}
