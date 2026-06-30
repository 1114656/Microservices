package com.xiaoyang.diary.module.system.dal.mysql.site;

import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.module.system.dal.dataobject.site.SiteConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteConfigMapper extends BaseMapperX<SiteConfigDO> {

    default SiteConfigDO selectByConfigKey(String configKey) {
        return selectOne(SiteConfigDO::getConfigKey, configKey);
    }

}
