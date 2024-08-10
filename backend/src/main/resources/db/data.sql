# DROP SCHEMA IF EXISTS archive;
# CREATE SCHEMA IF NOT EXISTS archive DEFAULT CHARACTER SET UTF8mb4;
USE archive;
#==================================-============================================================
# user
insert into archive.user (created_at, modified_at, birth_date, cash, deleted_at, email,
                          gender, multi_score, nickname, provider, pwd_hash, role, single_score,
                          user_img, uuid, firebase_token)
values ('2024-07-29 01:21:33.468404', '2024-08-05 00:32:35.328245', '1998-05-28 15:00:00.000000',
        0, null, 'ssafy1@ssafy.com', true, 1500, '김춘배', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy1.png', 'cbcfe839-7031-481e-a2d4-3a1cfbfed90f', null),
       ('2024-07-29 01:45:52.226517', '2024-07-29 01:45:52.226517', '2024-07-28 15:00:00.000000',
        0, null, 'ssafy2@ssafy.com', true, 1300, '악카이브', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy2.png', 'c6c6d147-11ad-4803-b164-6260ea0e8f69', null),
       ('2024-07-29 01:46:11.558280', '2024-07-29 01:46:11.558280', '2024-07-28 15:00:00.000000',
        0, null, 'ssafy3@ssafy.com', true, 1500, '송창용', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy3.png', 'f7f23ed4-0ba6-4f45-9874-9634165453b1', null),
       ('2024-07-29 01:46:33.909071', '2024-07-29 01:46:33.909071',
        '2024-07-28 15:00:00.000000', 0, null, 'ssafy4@ssafy.com', true, 1000, '홍주형', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy4.png', '8ee4baae-c512-49c2-bfc6-c61364e9c292', null),
       ('2024-07-22 16:09:44.000000', '2024-07-22 16:09:44.000000', '1998-05-28 15:00:00.000000',
        0, null, 'ssafy5@ssafy.com', true, 1250, '신순호', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy5.png', '94eecf5b-b597-461a-8b50-4fd47f82cea5', null),
       ('2024-07-22 16:09:44.000000', '2024-07-22 16:09:44.000000', '1998-05-28 15:00:00.000000',
        0, null, 'ssafy6@ssafy.com', false, 1000, '정문기', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy6.png', 'fd8309b6-2fe5-4e40-b738-cb5c684dae7d', null),
       ('2024-07-22 16:09:44.000000', '2024-07-22 16:09:44.000000', '1998-05-28 15:00:00.000000',
        0, null, 'ssafy7@ssafy.com', false, 900, '정재영', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy7.png', 'af190ea4-5576-4220-a896-1e53fcdcf79d', null),
       ('2024-07-22 16:09:44.000000', '2024-07-22 16:09:44.000000', '1998-05-28 15:00:00.000000',
        0, null, 'ssafy8@ssafy.com', false, 890, '이혜윤', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy8.png', '2aed3202-438c-4e30-92e8-4cd42804054c', null),
       ('2024-07-22 16:09:44.000000', '2024-07-22 16:09:44.000000', '1998-05-28 15:00:00.000000',
        0, null, 'ssafy9@ssafy.com', false, 1070, '고동연', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy9.png', '6c8516cb-b407-4270-bff6-2d0027eb498f', null),
       ('2024-07-22 16:09:44.000000', '2024-07-22 16:09:44.000000',
        '1998-05-28 15:00:00.000000',
        0, null, 'ssafy10@ssafy.com', false, 1130, '윤서영', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy10.png', 'e717f88e-9ece-4653-b1d2-ded3b9b5bea6', null),
       ('2024-07-29 01:45:52.226517', '2024-07-29 01:45:52.226517', '2024-07-28 15:00:00.000000',
        0, null, 'ssafy11@ssafy.com', true, 1300, '이주형', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_USER', null,
        'ssafy2.png', 'c6c6d147-11ad-4803-b164-6260ea0e8f69', null),
       ('2024-07-29 01:21:33.468404', '2024-08-05 00:32:35.328245',
        '1998-05-28 15:00:00.000000',
        0, null, 'admin@ssafy.com', true, 900, '관리자', null,
        '$2a$10$zM42VizLFc7P.VQ4acYDPe2Q5wLoJAvX8gbpqUhkv3uyeFiC.54My', 'ROLE_ADMIN', null,
        'admin.png', 'cbcfe839-7031-481e-a2d4-3a1cfbfed90f', null);


#==================================-============================================================
# 팔로우 (follow)
INSERT INTO follow(followed_id, follower_id)
VALUES (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (1, 9),
       (1, 10),

       (2, 1),
       (2, 3),
       (2, 4),
       (2, 5),
       (2, 6),
       (2, 7),
       (2, 8),
       (2, 9),
       (2, 10),

       (3, 1),
       (3, 2),
       (3, 4),
       (3, 5),
       (3, 6),
       (3, 7),
       (3, 8),
       (3, 9),
       (3, 10),

       (4, 1),
       (4, 2),
       (4, 3),
       (4, 5),
       (4, 6),
       (4, 7),
       (4, 8),
       (4, 9),
       (4, 10),

       (5, 1),
       (5, 2),
       (5, 3),
       (5, 4),
       (5, 6),
       (5, 7),
       (5, 8),
       (5, 9),
       (5, 10);
#==================================-============================================================
# 장르(Genre)
INSERT INTO genre(title)
VALUES ('랩'),
       ('발라드'),
       ('세미클래식'),
       ('재즈'),
       ('클래식'),
       ('힙합'),
       ('K POP'),
       ('J POP'),
       ('메탈'),
       ('기타');
#==================================-============================================================
# 곡 (Song)
insert into song (song_id, composer, img_name, title, genre_id)
values (1, 'Max Vogrich', 'Max Vogrich.jpg', '체르니 100번 1', 1),
       (2, '아이브', 'after_like.jpg', 'after like', 5),
       (3, '멜로망스', '사랑인가 봐.jpg', '사랑인가 봐', 5),
       (4, '조정석', '아로하.jpg', '아로하', 5),
       (99999999, '기타', '기타.jpg', '기타', 6);

#==================================-============================================================
# 악보(sheet)
insert into sheet (sheet_id, created_at, deleted_at, file_name, level, modified_at,
                   price, status, title, view_count, song_id, uploader_id)
values (1, '2024-07-30 15:18:28.000000', null, '체르니 100 1번.mid', 1, '2024-07-30 15:18:28.000000',
        500, 0, '체르니 100 1번', 263, 1, 1),
       (2, '2024-07-30 15:18:28.000000', null, 'after_like.mid', 2, '2024-07-30 15:18:28.000000',
        500, 1, 'after like', 263, 2, 2),
       (3, '2024-07-30 15:18:28.000000', null, '사랑인가 봐.mid', 3, '2024-07-30 15:18:28.000000',
        500, 2, '사랑인가 봐', 263, 3, 3),
       (4, '2024-07-30 15:18:28.000000', null, '아로하.mid', 4, '2024-07-30 15:18:28.000000',
        500, 1, '아로하', 263, 4, 4),
       (7, '2024-07-31 04:53:13.429411', null, '아로하-v3.mid', 3,
        '2024-07-31 04:53:13.429411', 500, 1, '아로하-조정석', 263, 4, 5),
       (8, '2024-07-31 04:53:40.909672', null, '체르니 4번.mid', 2,
        '2024-07-31 04:53:40.909672', 500, 1, '체르니 100 4번', 263, 1, 6),
       (9, '2024-07-31 06:18:06.293099', null, '아로하 어쿠스틱.mid', 1,
        '2024-07-31 06:18:06.293099', 500, 1, '아로하-어쿠스틱', 263, 1, 7),
       (10, '2024-07-31 06:21:14.046643', null, '아로하-ver2.mid', 1,
        '2024-07-31 06:21:14.046643', 500, 1, '아로하-ver2', 263, 1, 8),
       (11, '2024-07-31 06:21:14.046643', null, 'after_like-편곡.mid', 3,
        '2024-07-31 06:21:14.046643', 500, 1, '아로하-ver2', 263, 1, 7);

#==================================-============================================================
# 악보 좋아요 (like_sheet)
INSERT INTO like_sheet(like_sheet_id, user_id, sheet_id, created_at)
VALUES (1, 1, 1, NOW()),
       (2, 2, 1, NOW()),
       (3, 3, 1, NOW()),
       (4, 4, 1, NOW()),
       (5, 5, 1, NOW()),
       (6, 6, 1, NOW());
#==================================-============================================================
# 알림 타입 (AlertType)
INSERT INTO alert_type(alert_type_id, alert_table_name)
VALUES (1, 'battle'),
       (2, 'sheet'),
       (3, 'like_sheet'),
       (4, 'follow');

#==================================-============================================================
# 실시간 알람 (Alert)
 INSERT INTO alert(alert_id, receiver_id, alert_type_id, reference_id, read_status, created_at)
 VALUES (1, 1, 1, 1, 0, NOW()),
        (2, 2, 2, 1, 0, NOW()),
        (3, 3, 3, 1, 0, NOW());
#==================================-============================================================
# 결제 (payment)
INSERT INTO payment(payment_id, name)
VALUES (1, 'KAKAO_PAY'),
       (2, 'CASH');
#==================================-============================================================
# 결제 (payment)
# INSERT INTO sheet_order(sheet_order_id, user_id, sheet_id, payment_id, created_at)
# VALUES (1, 1, 1, 1, NOW()),
#        (2, 2, 1, 1, NOW()),
#        (3, 3, 1, 1, NOW());

# 별점
# INSERT INTO sheet_star_rate(sheet_star_rate_id, sheet_id, user_id, content, star_rate)
# VALUES (1, 1, 1, '평가1', 1),
#        (2, 1, 2, '평가2', 2),
#        (3, 1, 3, '평가3', 3);

# 난이도 평가
# INSERT INTO difficulty(user_id, sheet_id, level, content)
# VALUES (1, 1, 4, '너무 쉬워요!'),
#        (2, 1, 3, '너무 어려워요!'),
#        (3, 1, 2, '너무 대박이에요!');

# 멀티 플레이 전적
# insert into multi_play_result (multi_result_id, created_at, modified_at, is_draw,
#                                loser_score, play_time, status, winner_score, loser_id,
#                                sheet_id, winner_id)
# values (1, '2024-08-05 09:53:07.000000', '2024-08-05 09:53:08.000000', 0, 79, 3, 1, 81, 1, 1, 2);

# 싱글 플레이 전적
insert into archive.single_play_result (single_result_id, created_at, modified_at, play_time, score,
                                        sheet_id, user_id)
values (1, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 81, 1, 1),
       (2, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 79, 1, 1),
       (3, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 81, 2, 1),
       (4, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 79, 2, 1),
       (5, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 81, 3, 1),
       (6, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 79, 3, 1),
       (7, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 81, 1, 2),
       (8, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 78, 1, 2),
       (9, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 83, 2, 2),
       (10, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 75, 2, 2),
       (11, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 83, 3, 2),
       (12, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 79, 3, 2),
       (13, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 83, 1, 3),
       (14, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 79, 1, 3),
       (15, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 89, 2, 3),
       (16, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 70, 2, 3),
       (17, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 90, 3, 3),
       (18, '2024-08-05 09:56:11.000000', '2024-08-05 09:56:12.000000', 2, 70, 3, 3);