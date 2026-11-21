package org.sopt.sopkathon.domain.kid.repository;

import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KidRepository extends JpaRepository<Kid, Long> {

    Optional<Kid> findByIdAndDeletedAtIsNull(Long id);
}
