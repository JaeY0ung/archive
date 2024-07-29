# DROP SCHEMA IF EXISTS archive;
# CREATE SCHEMA IF NOT EXISTS archive DEFAULT CHARACTER SET UTF8mb4;
USE archive;

# user
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (1, 0, null, null, null, '2024-07-22 16:09:44.000000', null, '2024-07-22 16:09:44.000000',
        'user1@ssafy.com', 'user1',
        '0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90', null, 'user1.png',
        '94eecf5b-b597-461a-8b50-4fd47f82cea5', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (2, 0, null, null, null, '2024-07-22 16:09:44.000000', null, '2024-07-22 16:09:44.000000',
        'user2@ssafy.com', 'user2',
        '6025d18fe48abd45168528f18a82e265dd98d421a7084aa09f61b341703901a3', null, 'user2.png',
        'fd8309b6-2fe5-4e40-b738-cb5c684dae7d', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (3, 0, null, null, null, '2024-07-22 16:09:44.000000', null, '2024-07-22 16:09:44.000000',
        'user3@ssafy.com', 'user3',
        '5860faf02b6bc6222ba5aca523560f0e364ccd8b67bee486fe8bf7c01d492ccb', null, 'user3.png',
        'af190ea4-5576-4220-a896-1e53fcdcf79d', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (4, 0, null, null, null, '2024-07-22 16:09:44.000000', null, '2024-07-22 16:09:44.000000',
        'user4@ssafy.com', 'user4',
        '5269ef980de47819ba3d14340f4665262c41e933dc92c1a27dd5d01b047ac80e', null, 'user4.png',
        '2aed3202-438c-4e30-92e8-4cd42804054c', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (5, 0, null, null, null, '2024-07-22 16:09:44.000000', null, '2024-07-22 16:09:44.000000',
        'user5@ssafy.com', 'user5',
        '5a39bead318f306939acb1d016647be2e38c6501c58367fdb3e9f52542aa2442', null, 'user5.png',
        '6c8516cb-b407-4270-bff6-2d0027eb498f', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (6, 0, null, null, null, '2024-07-22 16:09:44.000000', null, '2024-07-22 16:09:44.000000',
        'user6@ssafy.com', 'user6',
        'ecb48a1cc94f951252ec462fe9ecc55c3ef123fadfe935661396c26a45a5809d', null, 'user6.png',
        'e717f88e-9ece-4653-b1d2-ded3b9b5bea6', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (7, 0, true, 'ROLE_USER', '1998-05-28 15:00:00.000000', '2024-07-29 01:21:33.468404', null,
        '2024-07-29 01:21:33.468404', 'ssafy@ssafy.com', 'ssafy',
        '$2a$10$oxOGMt3r0MwpxsdVePPC5OivtlNkYF1Rcu6f2BovY/LveJu/FLd.m', null, 'user7.png',
        'cbcfe839-7031-481e-a2d4-3a1cfbfed90f', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (8, 0, true, 'ROLE_USER', '2024-07-28 15:00:00.000000', '2024-07-29 01:45:52.226517', null,
        '2024-07-29 01:45:52.226517', 'ssafy1@ssafy.com', 'user1',
        '$2a$10$he2lmt.dSoQ15YfSe64aGu12Hi3ukiiC6U3Xypo8WMXruS8CCDyce', null, 'user8.png',
        'c6c6d147-11ad-4803-b164-6260ea0e8f69', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (9, 0, true, 'ROLE_USER', '2024-07-28 15:00:00.000000', '2024-07-29 01:46:11.558280', null,
        '2024-07-29 01:46:11.558280', 'ssafy2@ssafy.com', 'ssafy2',
        '$2a$10$LE3hdry2pBXO8XZ.i6F6S.KVGCvNmZFkeEVnEgh1VbBa.ev5xg0n6', null, 'user9.png',
        'f7f23ed4-0ba6-4f45-9874-9634165453b1', null, null);
INSERT INTO archive.user (user_id, cash, gender, role, birth_date, created_at, deleted_at,
                          modified_at, email, nickname, pwd_hash, token, user_img, uuid,
                          multi_score, single_score)
VALUES (10, 0, true, 'ROLE_USER', '2024-07-28 15:00:00.000000', '2024-07-29 01:46:33.909071', null,
        '2024-07-29 01:46:33.909071', 'ssafy3@ssafy.com', 'ssafy3',
        '$2a$10$E2epoNr8RBjL0jNc8sAbSuqMT20pU0ZYa21WUJpKI/xgF0vlyhCfK', null, 'user10.png',
        '8ee4baae-c512-49c2-bfc6-c61364e9c292', null, null);


#==================================-============================================================
# 팔로우 (follow)
INSERT INTO follow(follow_id, followed_id, follower_id)
VALUES (1, 1, 2),
       (2, 1, 3),
       (3, 1, 4),
       (4, 1, 5),
       (5, 1, 6),

       (6, 2, 1),
       (7, 2, 3),
       (8, 2, 4),
       (9, 2, 5),

       (10, 3, 1),
       (11, 3, 2),
       (12, 3, 4),

       (14, 4, 1),
       (15, 4, 2),

       (16, 3, 1);

# 장르(Genre)
INSERT INTO genre(genre_id, title)
VALUES (1, '클래식'),
       (2, '세미클래식'),
       (3, '재즈'),
       (4, '힙합'),
       (5, '가요'),
       (6, '기타');

# 곡 (Song)
INSERT INTO song(song_id, genre_id, composer, title, img_name)
VALUES (1, 1, 'Max Vogrich', '체르니 100번 1', 'Max Vogrich.jpg'),
       (2, 5, '아이브', 'after like', 'after_like.jpg');

# 악보(sheet)
INSERT INTO sheet(sheet_id, song_id, level, point, price, uploader_id, status, title, file_name,
                  created_at,
                  modified_at,
                  deleted_at)
VALUES (1, 1, 5, 5, 500, 1, 0, '체르니 100 1번', '체르니 100 1번', NOW(), NOW(), NULL);

# 악보 좋아요 (like_sheet)
INSERT INTO like_sheet(like_sheet_id, user_id, sheet_id, created_at)
VALUES (1, 1, 1, NOW()),
       (2, 2, 1, NOW()),
       (3, 3, 1, NOW()),
       (4, 4, 1, NOW()),
       (5, 5, 1, NOW()),
       (6, 6, 1, NOW());

# 알림 타입 (AlertType)
INSERT INTO alert_type(alert_type_id, alert_table_name)
VALUES (1, 'sheet'),
       (2, 'like_sheet'),
       (3, 'follow'),
       (4, 'board'),
       (5, 'battle'),
       (6, 'report_sheet');

# 실시간 알람 (Alert)
INSERT INTO alert(alert_id, receiver_id, alert_type_id, reference_id, read_status, created_at)
VALUES (1, 1, 1, 1, 0, NOW()),
       (2, 2, 2, 1, 0, NOW()),
       (3, 3, 3, 1, 0, NOW());

# 결제 (payment)
INSERT INTO payment(payment_id, name)
VALUES (1, '카카오 페이'),
       (2, '토스'),
       (3, '신용카드');

# 결제 (payment)
INSERT INTO sheet_order(sheet_order_id, user_id, sheet_id, payment_id, created_at)
VALUES (1, 1, 1, 1, NOW()),
       (2, 2, 1, 1, NOW()),
       (3, 3, 1, 1, NOW());

# 별점
INSERT INTO sheet_star_rate(sheet_star_rate_id, sheet_id, user_id, content, star_rate)
VALUES (1, 1, 1, '평가1', 1),
       (2, 1, 2, '평가2', 2),
       (3, 1, 3, '평가3', 3);

# 난이도 평가
INSERT INTO difficulty(user_id, sheet_id, level, content)
VALUES (1, 1, 4, '너무 쉬워요!'),
       (2, 1, 3, '너무 어려워요!'),
       (3, 1, 2, '너무 대박이에요!');