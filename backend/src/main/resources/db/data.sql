# DROP SCHEMA IF EXISTS archive;
# CREATE SCHEMA IF NOT EXISTS archive DEFAULT CHARACTER SET UTF8mb4;
USE archive;

# TRUNCATE TABLE customer;
INSERT INTO customer(username, password, role)
VALUES ('wngud', '1234', 'user');

# user
TRUNCATE TABLE user;
INSERT INTO user(user_id, nickname, uuid, pwd_hash, email, token, created_at, modified_at,
                 deleted_at)
VALUES (1, 'user1', '94eecf5b-b597-461a-8b50-4fd47f82cea5',
        '0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90',
        'user1@ssafy.com', NULL, NOW(), NOW(), NULL),
       (2, 'user2', 'fd8309b6-2fe5-4e40-b738-cb5c684dae7d',
        '6025d18fe48abd45168528f18a82e265dd98d421a7084aa09f61b341703901a3',
        'user2@ssafy.com', NULL, NOW(), NOW(), NULL),
       (3, 'user3', 'af190ea4-5576-4220-a896-1e53fcdcf79d',
        '5860faf02b6bc6222ba5aca523560f0e364ccd8b67bee486fe8bf7c01d492ccb',
        'user3@ssafy.com', NULL, NOW(), NOW(), NULL),
       (4, 'user4', '2aed3202-438c-4e30-92e8-4cd42804054c',
        '5269ef980de47819ba3d14340f4665262c41e933dc92c1a27dd5d01b047ac80e',
        'user4@ssafy.com', NULL, NOW(), NOW(), NULL),
       (5, 'user5', '6c8516cb-b407-4270-bff6-2d0027eb498f',
        '5a39bead318f306939acb1d016647be2e38c6501c58367fdb3e9f52542aa2442',
        'user5@ssafy.com', NULL, NOW(), NOW(), NULL),
       (6, 'user6', 'e717f88e-9ece-4653-b1d2-ded3b9b5bea6',
        'ecb48a1cc94f951252ec462fe9ecc55c3ef123fadfe935661396c26a45a5809d',
        'user6@ssafy.com', NULL, NOW(), NOW(), NULL);


# 팔로우 (follow)
TRUNCATE TABLE follow;
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
TRUNCATE TABLE genre;
INSERT INTO genre(genre_id, title)
VALUES (1, '클래식'),
       (2, '세미클래식'),
       (3, '재즈'),
       (4, '힙합'),
       (5, '가요'),
       (6, '기타');

# 곡 (Song)
TRUNCATE TABLE song;
INSERT INTO song(song_id, genre_id, composer, title)
VALUES (1, 1, 'Max Vogrich', '체르니 100번 1');

# 악보(sheet)
TRUNCATE TABLE sheet;
INSERT INTO sheet(sheet_id, song_id, level, point, price, uploader_id, status, title, file_name, created_at,
                  modified_at,
                  deleted_at)
VALUES (1, 1, 5, 5, 500, 1, 0, '체르니 100 1번', '체르니 100 1번', NOW(), NOW(), NULL);

# 악보 좋아요 (like_sheet)
TRUNCATE TABLE like_sheet;
INSERT INTO like_sheet(like_sheet_id, user_id, sheet_id, created_at)
VALUES (1, 1, 1, NOW()),
       (2, 2, 1, NOW()),
       (3, 3, 1, NOW()),
       (4, 4, 1, NOW()),
       (5, 5, 1, NOW()),
       (6, 6, 1, NOW());

# 알림 타입 (AlertType)
TRUNCATE TABLE alert_type;
INSERT INTO alert_type(alert_type_id, alert_table_name)
VALUES (1, 'sheet'),
       (2, 'like_sheet'),
       (3, 'follow'),
       (4, 'board'),
       (5, 'battle'),
       (6, 'report_sheet');

# 실시간 알람 (Alert)
TRUNCATE TABLE alert;
INSERT INTO alert(alert_id, receiver_id, alert_type_id, reference_id, read_status, created_at)
VALUES (1, 1, 1, 1, 0, NOW()),
       (2, 2, 2, 1, 0, NOW()),
       (3, 3, 3, 1, 0, NOW());

# 결제 (payment)
TRUNCATE TABLE payment;
INSERT INTO payment(payment_id, name)
VALUES (1, '카카오 페이'),
       (2, '토스'),
       (3, '신용카드');

# 결제 (payment)
TRUNCATE TABLE sheet_order;
INSERT INTO sheet_order(sheet_order_id, user_id, sheet_id, payment_id, created_at)
VALUES (1, 1, 1, 1, NOW()),
       (2, 2, 1, 1, NOW()),
       (3, 3, 1, 1, NOW());
