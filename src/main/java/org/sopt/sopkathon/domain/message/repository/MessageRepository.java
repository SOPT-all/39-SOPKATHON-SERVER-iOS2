package org.sopt.sopkathon.domain.message.repository;

import org.sopt.sopkathon.domain.message.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByKid_IdAndBriefingDate(Long kidId, LocalDate briefingDate);

}
