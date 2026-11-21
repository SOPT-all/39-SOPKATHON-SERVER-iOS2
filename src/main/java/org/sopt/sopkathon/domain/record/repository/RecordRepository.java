package org.sopt.sopkathon.domain.record.repository;

import org.sopt.sopkathon.domain.record.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByKid_IdAndRecordedAtGreaterThanEqualAndRecordedAtLessThanOrderByRecordedAtAsc(
            Long kidId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        select r
        from Record r
        where r.kid.id in :kidIds
          and r.recordedAt >= :start
          and r.recordedAt < :end
        order by r.createdAt desc, r.id desc
        """)
    List<Record> findHomeRecords(
            @Param("kidIds") List<Long> kidIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
