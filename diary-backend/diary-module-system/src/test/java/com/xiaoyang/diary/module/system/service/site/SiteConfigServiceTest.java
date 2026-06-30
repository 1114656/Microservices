package com.xiaoyang.diary.module.system.service.site;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SiteConfigServiceTest {

    @Test
    void shouldUseDiaryAsDefaultHomePageType() {
        assertEquals("diary", SiteConfigService.DEFAULT_HOME_PAGE_TYPE);
    }
}
