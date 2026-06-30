package com.xiaoyang.diary.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档地址
 *
 * @author 小杨
 */
@Getter
@AllArgsConstructor
public enum DocumentEnum {

    REDIS_INSTALL("https://gitee.com/zhijiantianya/diary-backend/issues/I4VCSJ", "Redis 安装文档"),
    TENANT("http://localhost", "SaaS 多租户文档");

    private final String url;
    private final String memo;

}
