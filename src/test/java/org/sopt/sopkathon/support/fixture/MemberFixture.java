package org.sopt.sopkathon.support.fixture;

import org.sopt.sopkathon.domain.member.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

public final class MemberFixture {
    public static final Long MEMBER_ID = 1L;

    private MemberFixture() {
    }

    public static Member member() {
        return member(MEMBER_ID);
    }

    public static Member member(Long id) {
        Member member = Member.builder()
                .name("테스트 선생님")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}
