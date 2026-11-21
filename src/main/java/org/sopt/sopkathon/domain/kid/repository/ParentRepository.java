package org.sopt.sopkathon.domain.kid.repository;

import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    List<Parent> findAllByKidIdOrderByIdAsc(Long kidId);

    Optional<Parent> findFirstByKidIdOrderByIdAsc(Long kidId);
}
