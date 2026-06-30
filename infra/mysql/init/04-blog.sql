CREATE DATABASE IF NOT EXISTS diary_blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'diary_blog'@'%' IDENTIFIED BY 'diary_blog_pwd';
GRANT ALL PRIVILEGES ON diary_blog.* TO 'diary_blog'@'%';
FLUSH PRIVILEGES;
