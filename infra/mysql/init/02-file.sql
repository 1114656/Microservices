CREATE DATABASE IF NOT EXISTS diary_file DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'diary_file'@'%' IDENTIFIED BY 'diary_file_pwd';
GRANT ALL PRIVILEGES ON diary_file.* TO 'diary_file'@'%';
FLUSH PRIVILEGES;

USE diary_file;

CREATE TABLE IF NOT EXISTS file_outbox_event (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    event_id VARCHAR(64) NOT NULL COMMENT '事件编号',
    event_type VARCHAR(64) NOT NULL COMMENT '事件类型',
    topic VARCHAR(128) NOT NULL COMMENT 'RocketMQ topic',
    tag VARCHAR(64) NOT NULL COMMENT 'RocketMQ tag',
    payload JSON NOT NULL COMMENT '事件载荷',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0待发送 1已发送 2失败',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    next_retry_time DATETIME NOT NULL COMMENT '下次重试时间',
    sent_time DATETIME NULL COMMENT '发送成功时间',
    last_error VARCHAR(512) NULL COMMENT '最后错误',
    creator VARCHAR(64) NULL DEFAULT '',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) NULL DEFAULT '',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    UNIQUE KEY uk_event_id (event_id),
    KEY idx_status_retry (status, next_retry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件服务 outbox 事件表';
