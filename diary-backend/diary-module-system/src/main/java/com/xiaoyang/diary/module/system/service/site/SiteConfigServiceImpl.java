package com.xiaoyang.diary.module.system.service.site;

import com.xiaoyang.diary.module.system.dal.dataobject.site.SiteConfigDO;
import com.xiaoyang.diary.module.system.dal.mysql.site.SiteConfigMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SiteConfigServiceImpl implements SiteConfigService {

    private static final Set<String> HOME_PAGE_TYPES = Set.of("diary", "blog");

    @Resource
    private SiteConfigMapper siteConfigMapper;

    @Override
    public String getHomePageType() {
        SiteConfigDO config = siteConfigMapper.selectByConfigKey(HOME_PAGE_TYPE_KEY);
        if (config == null || !HOME_PAGE_TYPES.contains(config.getConfigValue())) {
            return DEFAULT_HOME_PAGE_TYPE;
        }
        return config.getConfigValue();
    }

    @Override
    public void updateHomePageType(String homePageType) {
        if (!HOME_PAGE_TYPES.contains(homePageType)) {
            throw new IllegalArgumentException("Unsupported home page type: " + homePageType);
        }
        SiteConfigDO config = siteConfigMapper.selectByConfigKey(HOME_PAGE_TYPE_KEY);
        if (config == null) {
            config = new SiteConfigDO();
            config.setConfigKey(HOME_PAGE_TYPE_KEY);
            config.setConfigValue(homePageType);
            config.setRemark("Home page content type: diary or blog");
            siteConfigMapper.insert(config);
            return;
        }
        config.setConfigValue(homePageType);
        siteConfigMapper.updateById(config);
    }

}
