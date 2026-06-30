package com.xiaoyang.diary.module.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户信息 Response VO")
@Data
public class UserRespVO {

    @Schema(description = "用户编号")
    private Long id;

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "用户性别")
    private Integer sex;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "最后登录 IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
