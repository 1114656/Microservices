package com.xiaoyang.diary.module.system.dal.dataobject.site;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("site_config")
@KeySequence("site_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class SiteConfigDO extends BaseDO {

    @TableId
    private Long id;

    private String configKey;

    private String configValue;

    private String remark;

}
