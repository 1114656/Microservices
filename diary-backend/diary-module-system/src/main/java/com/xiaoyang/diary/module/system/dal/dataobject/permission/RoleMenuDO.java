package com.xiaoyang.diary.module.system.dal.dataobject.permission;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("system_role_menu")
@KeySequence("system_role_menu_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleMenuDO extends BaseDO {

    @TableId
    private Long id;
    private Long roleId;
    private Long menuId;

}
