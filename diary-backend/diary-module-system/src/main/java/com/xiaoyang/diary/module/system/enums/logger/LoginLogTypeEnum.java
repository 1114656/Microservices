package com.xiaoyang.diary.module.system.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginLogTypeEnum {

    LOGIN_USERNAME(100),

    LOGOUT_SELF(200),
    LOGOUT_DELETE(202);

    private final Integer type;

}
