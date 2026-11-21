package org.sopt.sopkathon.domain.briefing.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.sopt.sopkathon.domain.kid.domain.QKid.kid;
import static org.sopt.sopkathon.domain.kid.domain.QMemberKid.memberKid;
import static org.sopt.sopkathon.domain.record.domain.QRecord.record;

@Repository
@RequiredArgsConstructor
public class BriefingQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<RecordCount> countMyKidsRecords(Long memberId, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .select(Projections.constructor(RecordCount.class,
                        kid.id,
                        record.category,
                        record.count()))
                .from(record)
                .join(record.kid, kid)
                .join(memberKid).on(memberKid.kid.eq(kid))
                .where(
                        memberKid.member.id.eq(memberId),
                        kid.deletedAt.isNull(),
                        record.recordedAt.goe(start),
                        record.recordedAt.lt(end)
                )
                .groupBy(kid.id, record.category)
                .orderBy(kid.id.asc(), record.category.asc())
                .fetch();
    }
}
