package com.xiaoyang.diary.module.diary.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryContentBlockReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryPageReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiarySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryEntryDO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryFileDO;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryCategoryMapper;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryEntryMapper;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryFileMapper;
import com.xiaoyang.diary.module.file.service.FileObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.module.diary.enums.ErrorCodeConstants.DIARY_CATEGORY_NOT_EXISTS;
import static com.xiaoyang.diary.module.diary.enums.ErrorCodeConstants.DIARY_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private static final String USAGE_TYPE_CONTENT = "content";
    private static final int STATUS_PUBLISHED = 1;
    private static final int VISIBILITY_LOGIN = 1;
    private static final int VISIBILITY_PUBLIC = 2;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int SUMMARY_MAX_LENGTH = 200;

    private final DiaryEntryMapper diaryEntryMapper;
    private final DiaryFileMapper diaryFileMapper;
    private final DiaryCategoryMapper diaryCategoryMapper;
    private final FileObjectService fileObjectService;
    private final DiaryContentBlockService contentBlockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDiary(DiarySaveReqVO reqVO, Long ownerUserId) {
        List<DiaryContentBlockReqVO> blocks = safeBlocks(reqVO);
        validateCategory(reqVO.getCategoryId(), ownerUserId);
        validateFiles(blocks, ownerUserId);
        DiaryEntryDO diary = DiaryEntryDO.builder()
                .ownerUserId(ownerUserId)
                .categoryId(reqVO.getCategoryId())
                .title(reqVO.getTitle())
                .content(contentBlockService.serialize(blocks))
                .summary(buildSummary(blocks))
                .status(defaultStatus(reqVO.getStatus()))
                .visibility(defaultVisibility(reqVO.getVisibility()))
                .build();
        diaryEntryMapper.insert(diary);
        replaceDiaryFiles(diary.getId(), blocks);
        return diary.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDiary(DiarySaveReqVO reqVO, Long ownerUserId) {
        if (reqVO.getId() == null) {
            throw exception(DIARY_NOT_EXISTS);
        }
        getDiary(reqVO.getId(), ownerUserId);
        List<DiaryContentBlockReqVO> blocks = safeBlocks(reqVO);
        validateCategory(reqVO.getCategoryId(), ownerUserId);
        validateFiles(blocks, ownerUserId);
        DiaryEntryDO diary = DiaryEntryDO.builder()
                .id(reqVO.getId())
                .ownerUserId(ownerUserId)
                .categoryId(reqVO.getCategoryId())
                .title(reqVO.getTitle())
                .content(contentBlockService.serialize(blocks))
                .summary(buildSummary(blocks))
                .status(defaultStatus(reqVO.getStatus()))
                .visibility(defaultVisibility(reqVO.getVisibility()))
                .build();
        diaryEntryMapper.updateById(diary);
        replaceDiaryFiles(reqVO.getId(), blocks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDiary(Long id, Long ownerUserId) {
        getDiary(id, ownerUserId);
        diaryFileMapper.deleteByDiaryId(id);
        diaryEntryMapper.deleteById(id);
    }

    @Override
    public DiaryEntryDO getDiary(Long id, Long ownerUserId) {
        DiaryEntryDO diary = diaryEntryMapper.selectById(id);
        if (diary == null || !Objects.equals(diary.getOwnerUserId(), ownerUserId)) {
            throw exception(DIARY_NOT_EXISTS);
        }
        return diary;
    }

    @Override
    public PageResult<DiaryEntryDO> getDiaryPage(DiaryPageReqVO reqVO, Long ownerUserId) {
        normalizePageQuery(reqVO, ownerUserId);
        return diaryEntryMapper.selectPage(reqVO, ownerUserId);
    }

    private void validateFiles(List<DiaryContentBlockReqVO> blocks, Long ownerUserId) {
        contentBlockService.extractFileReferences(blocks)
                .forEach(reference -> fileObjectService.getFile(reference.fileId(), ownerUserId));
    }

    private void validateCategory(Long categoryId, Long ownerUserId) {
        DiaryCategoryDO category = categoryId == null ? null : diaryCategoryMapper.selectById(categoryId);
        if (category == null || !Objects.equals(category.getOwnerUserId(), ownerUserId)) {
            throw exception(DIARY_CATEGORY_NOT_EXISTS);
        }
    }

    private void replaceDiaryFiles(Long diaryId, List<DiaryContentBlockReqVO> blocks) {
        diaryFileMapper.deleteByDiaryId(diaryId);
        List<DiaryFileDO> files = contentBlockService.extractFileReferences(blocks).stream()
                .map(reference -> DiaryFileDO.builder()
                        .diaryId(diaryId)
                        .fileId(reference.fileId())
                        .sort(reference.sort())
                        .usageType(USAGE_TYPE_CONTENT)
                        .build())
                .toList();
        if (CollUtil.isNotEmpty(files)) {
            diaryFileMapper.insertBatch(files);
        }
    }

    private List<DiaryContentBlockReqVO> safeBlocks(DiarySaveReqVO reqVO) {
        return reqVO.getContentBlocks() == null ? List.of() : reqVO.getContentBlocks();
    }

    private String buildSummary(List<DiaryContentBlockReqVO> blocks) {
        String text = blocks.stream()
                .filter(block -> block != null && DiaryContentBlockReqVO.TYPE_TEXT.equals(block.getType()))
                .map(DiaryContentBlockReqVO::getContent)
                .filter(StrUtil::isNotBlank)
                .reduce("", (left, right) -> left + right);
        if (text.length() <= SUMMARY_MAX_LENGTH) {
            return text;
        }
        return text.substring(0, SUMMARY_MAX_LENGTH);
    }

    private Integer defaultStatus(Integer status) {
        return status == null ? STATUS_PUBLISHED : status;
    }

    private Integer defaultVisibility(Integer visibility) {
        return visibility == null ? VISIBILITY_LOGIN : visibility;
    }

    private void normalizePageQuery(DiaryPageReqVO reqVO, Long ownerUserId) {
        if (reqVO.getPageSize() == null || reqVO.getPageSize() > MAX_PAGE_SIZE) {
            reqVO.setPageSize(MAX_PAGE_SIZE);
        }
        if (ownerUserId == null) {
            reqVO.setStatus(STATUS_PUBLISHED);
            reqVO.setVisibility(VISIBILITY_PUBLIC);
        }
    }
}
