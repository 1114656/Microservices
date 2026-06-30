CREATE DATABASE IF NOT EXISTS diary_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'diary_diary'@'%' IDENTIFIED BY 'diary_diary_pwd';
GRANT ALL PRIVILEGES ON diary_diary.* TO 'diary_diary'@'%';
FLUSH PRIVILEGES;
