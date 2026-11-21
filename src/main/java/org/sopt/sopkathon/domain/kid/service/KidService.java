package org.sopt.sopkathon.domain.kid.service;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.kid.code.KidErrorCode;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.sopt.sopkathon.domain.kid.dto.request.CreateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.ParentRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateParentRequest;
import org.sopt.sopkathon.domain.kid.dto.response.CreateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidListResponse;
import org.sopt.sopkathon.domain.kid.dto.response.UpdateKidResponse;
import org.sopt.sopkathon.domain.kid.repository.KidQueryRepository;
import org.sopt.sopkathon.domain.kid.repository.KidRepository;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.kid.repository.ParentRepository;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.domain.member.domain.Member;
import org.sopt.sopkathon.domain.member.repository.MemberRepository;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KidService {
    private final KidRepository kidRepository;
    private final KidQueryRepository kidQueryRepository;
    private final ParentRepository parentRepository;
    private final MemberKidRepository memberKidRepository;
    private final MemberRepository memberRepository;
    private final Clock clock;

    public KidListResponse getKids(Long memberId, String keyword) {
        validateMemberExists(memberId);
        return KidListResponse.of(kidQueryRepository.findMyKids(memberId, keyword));
    }

    // 아이 등록
    @Transactional
    public CreateKidResponse createKid(Long memberId, CreateKidRequest request) {
        Member member = findMember(memberId);

        Kid kid = kidRepository.save(Kid.builder()
                .name(request.name())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .note(request.note())
                .build());

        memberKidRepository.save(MemberKid.builder()
                .member(member)
                .kid(kid)
                .build());

        parentRepository.save(toParent(kid, request.parent()));

        return CreateKidResponse.of(kid);
    }

    // 아이 수정
    @Transactional
    public UpdateKidResponse updateKid(Long memberId, Long kidId, UpdateKidRequest request) {
        Kid kid = findMyKid(memberId, kidId);

        kid.updateProfile(request.name(), request.birthDate(), request.gender());
        if (request.hasNote()) {
            kid.updateNote(request.noteValue());
        }

        UpdateParentRequest parentRequest = request.parent();
        if (parentRequest != null) {
            Parent parent = parentRepository.findFirstByKidIdOrderByIdAsc(kidId)
                    .orElseThrow(() -> new GeneralException(KidErrorCode.PARENT_NOT_FOUND));
            parent.update(parentRequest.name(), parentRequest.phone(), parentRequest.relationship());
        }

        return UpdateKidResponse.of(kid);
    }

    // 아이 삭제
    @Transactional
    public void deleteKid(Long memberId, Long kidId) {
        Kid kid = findMyKid(memberId, kidId);
        kid.delete(LocalDateTime.now(clock));
    }

    private Parent toParent(Kid kid, ParentRequest parent) {
        return Parent.builder()
                .kid(kid)
                .name(parent.name())
                .phone(parent.phone())
                .relationship(parent.relationship())
                .build();
    }

    // 아이 목록 조회 및 검색
    private Kid findMyKid(Long memberId, Long kidId) {
        validateMemberExists(memberId);
        if (!memberKidRepository.existsByMemberIdAndKidId(memberId, kidId)) {
            throw new GeneralException(KidErrorCode.KID_NOT_FOUND);
        }
        return kidRepository.findByIdAndDeletedAtIsNull(kidId)
                .orElseThrow(() -> new GeneralException(KidErrorCode.KID_NOT_FOUND));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }
}
