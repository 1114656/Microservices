package com.xiaoyang.diary.module.system.service.site;

public interface SiteConfigService {

    String HOME_PAGE_TYPE_KEY = "home_page_type";
    String DEFAULT_HOME_PAGE_TYPE = "diary";

    String getHomePageType();

    void updateHomePageType(String homePageType);

}
