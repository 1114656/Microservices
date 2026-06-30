CREATE DATABASE IF NOT EXISTS diary_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'diary_system'@'%' IDENTIFIED BY 'diary_system_pwd';
GRANT ALL PRIVILEGES ON diary_system.* TO 'diary_system'@'%';
FLUSH PRIVILEGES;
