package com.xiaoyang.diary.module.system.api.user.dto;

import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import lombok.Data;

/**
 * Admin user response DTO.
 */
@Data
public class AdminUserRespDTO {

    private Long id;

    private String nickname;

    /**
     * @see CommonStatusEnum
     */
    private Integer status;

    private String mobile;

}
