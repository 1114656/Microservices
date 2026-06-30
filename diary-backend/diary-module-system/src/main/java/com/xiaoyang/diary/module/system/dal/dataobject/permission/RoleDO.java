package com.xiaoyang.diary.module.system.dal.dataobject.permission;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import com.xiaoyang.diary.module.system.enums.permission.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "system_role", autoResultMap = true)
@KeySequence("system_role_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleDO extends BaseDO {

    @TableId
    private Long id;
    private String name;
    private String code;
    private Integer sort;
    /**
     * @see CommonStatusEnum
     */
    private Integer status;
    /**
     * @see RoleTypeEnum
     */
    private Integer type;
    private String remark;

}
