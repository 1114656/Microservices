package com.xiaoyang.diary.module.diary.service;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryPageReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiarySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryEntryDO;

public interface DiaryService {

    Long createDiary(DiarySaveReqVO reqVO, Long ownerUserId);

    void updateDiary(DiarySaveReqVO reqVO, Long ownerUserId);

    void deleteDiary(Long id, Long ownerUserId);

    DiaryEntryDO getDiary(Long id, Long ownerUserId);

    PageResult<DiaryEntryDO> getDiaryPage(DiaryPageReqVO reqVO, Long ownerUserId);

}
