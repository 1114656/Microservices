package com.xiaoyang.diary.module.diary.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName(value = "diary_category", autoResultMap = true)
@KeySequence("diary_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCategoryDO extends BaseDO {

    @TableId
    private Long id;
    private Long ownerUserId;
    private String name;
    private Integer sort;
    private Integer status;

}
