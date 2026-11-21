package org.sopt.sopkathon.domain.kid.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.sopt.sopkathon.domain.kid.domain.QKid.kid;
import static org.sopt.sopkathon.domain.kid.domain.QMemberKid.memberKid;

@Repository
@RequiredArgsConstructor
public class KidQueryRepository {
    private final JPAQueryFactory queryFactory;

    // keyword가 있으면 이름에 포함된 아이만 조회
    public List<Kid> findMyKids(Long memberId, String keyword) {
        return queryFactory
                .select(kid)
                .from(memberKid)
                .join(memberKid.kid, kid)
                .where(
                        memberKid.member.id.eq(memberId),
                        kid.deletedAt.isNull(),
                        nameContains(keyword)
                )
                .orderBy(kid.name.asc(), kid.id.asc())
                .fetch();
    }

    private BooleanExpression nameContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return kid.name.contains(keyword.strip());
    }
}
