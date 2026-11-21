package org.sopt.sopkathon.domain.kid.repository;

import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberKidRepository extends JpaRepository<MemberKid, Long> {

    boolean existsByMemberIdAndKidId(Long memberId, Long kidId);
}
