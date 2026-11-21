package org.sopt.sopkathon.domain.briefing.repository;

import org.sopt.sopkathon.domain.briefing.domain.Briefing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BriefingRepository extends JpaRepository<Briefing, Long> {

    List<Briefing> findAllByKidIdInAndBriefingDate(Collection<Long> kidIds, LocalDate briefingDate);

    Optional<Briefing> findByKidIdAndBriefingDate(Long kidId, LocalDate briefingDate);
}
