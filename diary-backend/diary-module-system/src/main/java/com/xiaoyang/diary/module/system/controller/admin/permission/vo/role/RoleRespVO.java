package com.xiaoyang.diary.module.system.controller.admin.permission.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 角色信息 Response VO")
@Data
public class RoleRespVO {

    @Schema(description = "角色编号")
    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色标识")
    @NotBlank(message = "角色标识不能为空")
    private String code;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "角色类型")
    private Integer type;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
