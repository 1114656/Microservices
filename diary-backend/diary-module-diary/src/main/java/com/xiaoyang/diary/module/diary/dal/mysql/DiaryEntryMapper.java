package com.xiaoyang.diary.module.diary.dal.mysql;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryPageReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryEntryDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DiaryEntryMapper extends BaseMapperX<DiaryEntryDO> {

    int STATUS_PUBLISHED = 1;
    int VISIBILITY_LOGIN = 1;
    int VISIBILITY_PUBLIC = 2;

    default PageResult<DiaryEntryDO> selectPage(DiaryPageReqVO reqVO, Long ownerUserId) {
        LambdaQueryWrapperX<DiaryEntryDO> queryWrapper = new LambdaQueryWrapperX<DiaryEntryDO>()
                .likeIfPresent(DiaryEntryDO::getTitle, reqVO.getTitle())
                .eqIfPresent(DiaryEntryDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(DiaryEntryDO::getStatus, reqVO.getStatus())
                .eqIfPresent(DiaryEntryDO::getVisibility, reqVO.getVisibility());
        if (ownerUserId == null) {
            queryWrapper.eq(DiaryEntryDO::getStatus, STATUS_PUBLISHED)
                    .eq(DiaryEntryDO::getVisibility, VISIBILITY_PUBLIC);
        } else {
            queryWrapper.and(wrapper -> wrapper.eq(DiaryEntryDO::getOwnerUserId, ownerUserId)
                    .or(accessWrapper -> accessWrapper
                            .eq(DiaryEntryDO::getStatus, STATUS_PUBLISHED)
                            .in(DiaryEntryDO::getVisibility, List.of(VISIBILITY_LOGIN, VISIBILITY_PUBLIC))));
        }
        return selectPage(reqVO, queryWrapper.orderByDesc(DiaryEntryDO::getId));
    }

    List<CategoryStatusCount> selectCategoryStatusCounts(@Param("ownerUserId") Long ownerUserId,
                                                         @Param("categoryIds") List<Long> categoryIds);

    default Map<Long, Long> selectCategoryCounts(Long ownerUserId, List<Long> categoryIds) {
        Map<Long, Long> result = new HashMap<>();
        selectCategoryStatusCounts(ownerUserId, categoryIds).forEach(row ->
                result.merge(row.getCategoryId(), row.getCount(), Long::sum));
        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CategoryStatusCount {
        private Long categoryId;
        private Integer status;
        private Long count;
    }

}
