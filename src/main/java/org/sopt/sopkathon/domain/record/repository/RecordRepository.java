package org.sopt.sopkathon.domain.record.repository;

import org.sopt.sopkathon.domain.record.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByKid_IdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
            Long kidId,
            LocalDateTime start,
            LocalDateTime end
    );
}
