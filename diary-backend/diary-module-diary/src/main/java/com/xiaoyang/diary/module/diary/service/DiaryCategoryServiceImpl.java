package com.xiaoyang.diary.module.diary.service;

import cn.hutool.core.collection.CollUtil;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategoryRespVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategorySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryCategoryMapper;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.module.diary.enums.ErrorCodeConstants.DIARY_CATEGORY_HAS_ENTRIES;
import static com.xiaoyang.diary.module.diary.enums.ErrorCodeConstants.DIARY_CATEGORY_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class DiaryCategoryServiceImpl implements DiaryCategoryService {

    private static final int STATUS_ENABLED = 1;

    private final DiaryCategoryMapper diaryCategoryMapper;
    private final DiaryEntryMapper diaryEntryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(DiaryCategorySaveReqVO reqVO, Long ownerUserId) {
        DiaryCategoryDO category = DiaryCategoryDO.builder()
                .ownerUserId(ownerUserId)
                .name(reqVO.getName())
                .sort(defaultSort(reqVO.getSort()))
                .status(defaultStatus(reqVO.getStatus()))
                .build();
        diaryCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(DiaryCategorySaveReqVO reqVO, Long ownerUserId) {
        getCategory(reqVO.getId(), ownerUserId);
        DiaryCategoryDO category = DiaryCategoryDO.builder()
                .id(reqVO.getId())
                .ownerUserId(ownerUserId)
                .name(reqVO.getName())
                .sort(defaultSort(reqVO.getSort()))
                .status(defaultStatus(reqVO.getStatus()))
                .build();
        diaryCategoryMapper.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id, Long ownerUserId) {
        getCategory(id, ownerUserId);
        Long total = diaryEntryMapper.selectCategoryCounts(ownerUserId, List.of(id)).getOrDefault(id, 0L);
        if (total > 0) {
            throw exception(DIARY_CATEGORY_HAS_ENTRIES);
        }
        diaryCategoryMapper.deleteById(id);
    }

    @Override
    public DiaryCategoryDO getCategory(Long id, Long ownerUserId) {
        DiaryCategoryDO category = id == null ? null : diaryCategoryMapper.selectById(id);
        if (category == null || !Objects.equals(category.getOwnerUserId(), ownerUserId)) {
            throw exception(DIARY_CATEGORY_NOT_EXISTS);
        }
        return category;
    }

    @Override
    public List<DiaryCategoryRespVO> getCategoryList(Long ownerUserId) {
        List<DiaryCategoryDO> categories = diaryCategoryMapper.selectListByOwnerUserId(ownerUserId);
        if (CollUtil.isEmpty(categories)) {
            return List.of();
        }
        List<Long> categoryIds = categories.stream().map(DiaryCategoryDO::getId).toList();
        Map<Long, StatusCounts> counts = buildStatusCounts(ownerUserId, categoryIds);
        return categories.stream().map(category -> convert(category, counts.get(category.getId()))).toList();
    }

    private Map<Long, StatusCounts> buildStatusCounts(Long ownerUserId, List<Long> categoryIds) {
        Map<Long, StatusCounts> result = new HashMap<>();
        diaryEntryMapper.selectCategoryStatusCounts(ownerUserId, categoryIds).forEach(row -> {
            StatusCounts counts = result.computeIfAbsent(row.getCategoryId(), id -> new StatusCounts());
            if (Objects.equals(row.getStatus(), 0)) {
                counts.draftCount = row.getCount();
            } else if (Objects.equals(row.getStatus(), 1)) {
                counts.publishedCount = row.getCount();
            }
        });
        return result;
    }

    private DiaryCategoryRespVO convert(DiaryCategoryDO category, StatusCounts counts) {
        StatusCounts safeCounts = counts == null ? new StatusCounts() : counts;
        DiaryCategoryRespVO respVO = new DiaryCategoryRespVO();
        respVO.setId(category.getId());
        respVO.setName(category.getName());
        respVO.setSort(category.getSort());
        respVO.setStatus(category.getStatus());
        respVO.setDraftCount(safeCounts.draftCount);
        respVO.setPublishedCount(safeCounts.publishedCount);
        respVO.setTotalCount(safeCounts.draftCount + safeCounts.publishedCount);
        respVO.setCreateTime(category.getCreateTime());
        respVO.setUpdateTime(category.getUpdateTime());
        return respVO;
    }

    private Integer defaultSort(Integer sort) {
        return sort == null ? 0 : sort;
    }

    private Integer defaultStatus(Integer status) {
        return status == null ? STATUS_ENABLED : status;
    }

    private static class StatusCounts {
        private long draftCount;
        private long publishedCount;
    }
}
