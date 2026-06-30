package com.xiaoyang.diary.module.system.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import com.xiaoyang.diary.module.system.enums.common.SexEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@TableName(value = "system_users", autoResultMap = true)
@KeySequence("system_users_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDO extends BaseDO {

    @TableId
    private Long id;
    private String username;
    /**
     * @see BCryptPasswordEncoder
     */
    private String password;
    private String nickname;
    private String remark;
    private String email;
    private String mobile;
    /**
     * @see SexEnum
     */
    private Integer sex;
    /**
     * @see CommonStatusEnum
     */
    private Integer status;
    private String loginIp;
    private LocalDateTime loginDate;

}
