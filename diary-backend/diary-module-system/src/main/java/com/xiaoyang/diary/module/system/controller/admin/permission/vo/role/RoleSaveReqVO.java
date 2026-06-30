package com.xiaoyang.diary.module.system.controller.admin.permission.vo.role;

import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 角色创建/更新 Request VO")
@Data
public class RoleSaveReqVO {

    @Schema(description = "角色编号")
    private Long id;

    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过 30 个字符")
    private String name;

    @Schema(description = "角色标识")
    @NotBlank(message = "角色标识不能为空")
    @Size(max = 100, message = "角色标识长度不能超过 100 个字符")
    private String code;

    @Schema(description = "显示顺序")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remark;

}
