CREATE DATABASE IF NOT EXISTS diary_file DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'diary_file'@'%' IDENTIFIED BY 'diary_file_pwd';
GRANT ALL PRIVILEGES ON diary_file.* TO 'diary_file'@'%';
FLUSH PRIVILEGES;
