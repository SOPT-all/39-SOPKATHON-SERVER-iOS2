-- 시드 데이터
-- 브리핑 조회는 "오늘" 기준이라 기록/브리핑 날짜는 CURDATE()로 넣는다.
INSERT INTO member (name) VALUES ('테스트 선생님');

-- 아이
-- 1~4: 오늘 기록 있음 → 브리핑 목록에 나옴 (recordedKidNames 4명)
-- 5: 어제 기록만 있음 → 안 나옴
-- 6: 오늘 기록이 있지만 삭제됨 → 안 나옴
INSERT INTO kid (name, birth_date, gender, note, deleted_at, created_at, updated_at) VALUES
    ('노정완', '2024-03-15', 'MALE',   '견과류 알레르기가 있어요.', NULL, NOW(), NOW()),
    ('김민교', '2023-07-02', 'FEMALE', NULL,                     NULL, NOW(), NOW()),
    ('이하준', '2024-11-30', 'MALE',   '낮잠 전에 애착 인형이 필요해요.', NULL, NOW(), NOW()),
    ('박서아', '2022-05-10', 'FEMALE', NULL,                     NULL, NOW(), NOW()),
    ('최도윤', '2023-01-20', 'MALE',   NULL,                     NULL, NOW(), NOW()),
    ('정하린', '2023-09-08', 'FEMALE', NULL,                     NOW(), NOW(), NOW());

INSERT INTO member_kid (member_id, kid_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6);

INSERT INTO parent (kid_id, name, phone, relationship) VALUES
    (1, '노진구', '010-1234-5678', 'MOTHER'),
    (2, '김대중', '010-2345-6789', 'MOTHER'),
    (3, '이명박', '010-3456-7890', 'FATHER'),
    (4, '박근혜', '010-4567-8901', 'GUARDIAN'),
    (5, '최순실', '010-5678-9012', 'MOTHER'),
    (6, '정유라', '010-6789-0123', 'FATHER');

-- 돌봄 기록
INSERT INTO record (kid_id, category, recorded_at, created_at, updated_at) VALUES
    -- 노정완: FEEDING 3, SLEEP 1, DIAPER 2
    (1, 'FEEDING', TIMESTAMP(CURDATE(), '09:10:00'), NOW(), NOW()),
    (1, 'DIAPER',  TIMESTAMP(CURDATE(), '10:00:00'), NOW(), NOW()),
    (1, 'FEEDING', TIMESTAMP(CURDATE(), '11:30:00'), NOW(), NOW()),
    (1, 'SLEEP',   TIMESTAMP(CURDATE(), '13:00:00'), NOW(), NOW()),
    (1, 'DIAPER',  TIMESTAMP(CURDATE(), '14:40:00'), NOW(), NOW()),
    (1, 'FEEDING', TIMESTAMP(CURDATE(), '15:30:00'), NOW(), NOW()),
    -- 김민교: FEEDING 2, SLEEP 1
    (2, 'FEEDING', TIMESTAMP(CURDATE(), '09:20:00'), NOW(), NOW()),
    (2, 'SLEEP',   TIMESTAMP(CURDATE(), '12:50:00'), NOW(), NOW()),
    (2, 'FEEDING', TIMESTAMP(CURDATE(), '15:00:00'), NOW(), NOW()),
    -- 이하준: FEEDING 2, DIAPER 3 (브리핑 아직 없음)
    (3, 'FEEDING', TIMESTAMP(CURDATE(), '09:00:00'), NOW(), NOW()),
    (3, 'DIAPER',  TIMESTAMP(CURDATE(), '10:30:00'), NOW(), NOW()),
    (3, 'FEEDING', TIMESTAMP(CURDATE(), '12:00:00'), NOW(), NOW()),
    (3, 'DIAPER',  TIMESTAMP(CURDATE(), '13:30:00'), NOW(), NOW()),
    (3, 'DIAPER',  TIMESTAMP(CURDATE(), '16:00:00'), NOW(), NOW()),
    -- 박서아: SLEEP 1 (브리핑 아직 없음)
    (4, 'SLEEP',   TIMESTAMP(CURDATE(), '13:10:00'), NOW(), NOW()),
    -- 최도윤: 어제 기록만
    (5, 'FEEDING', TIMESTAMP(CURDATE() - INTERVAL 1 DAY, '09:30:00'), NOW(), NOW()),
    (5, 'SLEEP',   TIMESTAMP(CURDATE() - INTERVAL 1 DAY, '13:00:00'), NOW(), NOW()),
    -- 정하린: 오늘 기록이지만 삭제된 아이
    (6, 'FEEDING', TIMESTAMP(CURDATE(), '09:40:00'), NOW(), NOW());

-- 브리핑 (오늘: 노정완, 김민교만 생성됨 / 어제: 최도윤)
INSERT INTO briefing (kid_id, content, briefing_date, created_at, updated_at) VALUES
    (1, '안녕하세요! 정완이는 오늘 오전 9시 10분, 11시 30분, 오후 3시 30분에 수유 3회 잘 먹었고, 오후 1시에 낮잠도 푹 잤어요. 기저귀는 2번 갈았고 하루 종일 컨디션이 좋았습니다.',
        CURDATE(), NOW(), NOW()),
    (2, '안녕하세요! 민교는 오늘 오전 9시 20분과 오후 3시에 수유를 잘 마쳤고, 12시 50분부터 낮잠을 잤어요. 편안하게 하루를 보냈습니다.',
        CURDATE(), NOW(), NOW()),
    (5, '안녕하세요! 도윤이는 오늘 오전 9시 30분에 수유를 잘 했고, 오후 1시에 낮잠을 잤어요. 즐거운 하루를 보냈습니다.',
        CURDATE() - INTERVAL 1 DAY, NOW(), NOW());

SELECT 1;
