package org.sopt.sopkathon.domain.kid.repository;

import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberKidRepository extends JpaRepository<MemberKid, Long> {

    List<MemberKid> findAllByMemberId(Long memberId);

    Optional<MemberKid> findByMemberIdAndKidId(Long memberId, Long kidId);
    boolean existsByMemberIdAndKidId(Long memberId, Long kidId);
}
