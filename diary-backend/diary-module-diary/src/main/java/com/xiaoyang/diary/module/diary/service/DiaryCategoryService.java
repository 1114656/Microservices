package com.xiaoyang.diary.module.diary.service;

import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategoryRespVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategorySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;

import java.util.List;

public interface DiaryCategoryService {

    Long createCategory(DiaryCategorySaveReqVO reqVO, Long ownerUserId);

    void updateCategory(DiaryCategorySaveReqVO reqVO, Long ownerUserId);

    void deleteCategory(Long id, Long ownerUserId);

    DiaryCategoryDO getCategory(Long id, Long ownerUserId);

    List<DiaryCategoryRespVO> getCategoryList(Long ownerUserId);

}
