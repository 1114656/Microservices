package com.xiaoyang.diary.module.diary.dal.mysql;

import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryFileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiaryFileMapper extends BaseMapperX<DiaryFileDO> {

    default List<DiaryFileDO> selectListByDiaryId(Long diaryId) {
        return selectList(new LambdaQueryWrapperX<DiaryFileDO>()
                .eq(DiaryFileDO::getDiaryId, diaryId)
                .orderByAsc(DiaryFileDO::getSort));
    }

    default void deleteByDiaryId(Long diaryId) {
        delete(DiaryFileDO::getDiaryId, diaryId);
    }

}
