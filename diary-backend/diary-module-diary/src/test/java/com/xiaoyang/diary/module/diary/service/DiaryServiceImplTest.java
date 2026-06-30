package com.xiaoyang.diary.module.diary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.test.core.ut.BaseMockitoUnitTest;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryContentBlockReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryPageReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiarySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryEntryDO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryFileDO;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryCategoryMapper;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryEntryMapper;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryFileMapper;
import com.xiaoyang.diary.module.file.dal.dataobject.FileObjectDO;
import com.xiaoyang.diary.module.file.service.FileObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Collection;
import java.util.List;

import static com.xiaoyang.diary.module.diary.enums.ErrorCodeConstants.DIARY_CATEGORY_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiaryServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private DiaryEntryMapper diaryEntryMapper;
    @Mock
    private DiaryFileMapper diaryFileMapper;
    @Mock
    private DiaryCategoryMapper diaryCategoryMapper;
    @Mock
    private FileObjectService fileObjectService;

    private DiaryServiceImpl diaryService;

    @BeforeEach
    void setUp() {
        diaryService = new DiaryServiceImpl(diaryEntryMapper, diaryFileMapper, diaryCategoryMapper, fileObjectService,
                new DiaryContentBlockService(new ObjectMapper()));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void shouldCreateDiaryWithOrderedFileReferences() {
        DiarySaveReqVO reqVO = new DiarySaveReqVO();
        reqVO.setTitle("A day with audio");
        reqVO.setCategoryId(10L);
        reqVO.setContentBlocks(List.of(
                DiaryContentBlockReqVO.text("before audio"),
                DiaryContentBlockReqVO.file(10L),
                DiaryContentBlockReqVO.text("after audio before image"),
                DiaryContentBlockReqVO.file(20L)
        ));
        when(diaryCategoryMapper.selectById(10L)).thenReturn(DiaryCategoryDO.builder()
                .id(10L).ownerUserId(1L).status(1).build());
        when(fileObjectService.getFile(10L, 1L)).thenReturn(FileObjectDO.builder().id(10L).build());
        when(fileObjectService.getFile(20L, 1L)).thenReturn(FileObjectDO.builder().id(20L).build());
        when(diaryEntryMapper.insert(any(DiaryEntryDO.class))).thenAnswer(invocation -> {
            DiaryEntryDO diary = invocation.getArgument(0);
            diary.setId(100L);
            return 1;
        });
        when(diaryFileMapper.insertBatch(any(Collection.class))).thenReturn(true);

        Long diaryId = diaryService.createDiary(reqVO, 1L);

        assertEquals(100L, diaryId);
        verify(fileObjectService).getFile(10L, 1L);
        verify(fileObjectService).getFile(20L, 1L);

        ArgumentCaptor<DiaryEntryDO> diaryCaptor = ArgumentCaptor.forClass(DiaryEntryDO.class);
        verify(diaryEntryMapper).insert(diaryCaptor.capture());
        DiaryEntryDO diary = diaryCaptor.getValue();
        assertEquals("A day with audio", diary.getTitle());
        assertEquals(1L, diary.getOwnerUserId());
        assertEquals(10L, diary.getCategoryId());
        assertEquals(1, diary.getStatus());
        assertEquals(1, diary.getVisibility());
        assertTrue(diary.getContent().indexOf("before audio") < diary.getContent().indexOf("\"fileId\":10"));
        assertTrue(diary.getContent().indexOf("\"fileId\":10") < diary.getContent().indexOf("after audio before image"));
        assertTrue(diary.getContent().indexOf("after audio before image") < diary.getContent().indexOf("\"fileId\":20"));

        ArgumentCaptor<Collection<DiaryFileDO>> filesCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(diaryFileMapper).insertBatch(filesCaptor.capture());
        List<DiaryFileDO> files = filesCaptor.getValue().stream().toList();
        assertEquals(2, files.size());
        assertEquals(100L, files.get(0).getDiaryId());
        assertEquals(10L, files.get(0).getFileId());
        assertEquals(1, files.get(0).getSort());
        assertEquals(20L, files.get(1).getFileId());
        assertEquals(3, files.get(1).getSort());
    }

    @Test
    void shouldCreateDiaryWithCustomStatusAndVisibility() {
        DiarySaveReqVO reqVO = new DiarySaveReqVO();
        reqVO.setTitle("Private draft");
        reqVO.setCategoryId(10L);
        reqVO.setStatus(0);
        reqVO.setVisibility(0);
        reqVO.setContentBlocks(List.of(DiaryContentBlockReqVO.text("draft")));
        when(diaryCategoryMapper.selectById(10L)).thenReturn(DiaryCategoryDO.builder()
                .id(10L).ownerUserId(1L).status(1).build());
        when(diaryEntryMapper.insert(any(DiaryEntryDO.class))).thenAnswer(invocation -> {
            DiaryEntryDO diary = invocation.getArgument(0);
            diary.setId(101L);
            return 1;
        });

        diaryService.createDiary(reqVO, 1L);

        ArgumentCaptor<DiaryEntryDO> diaryCaptor = ArgumentCaptor.forClass(DiaryEntryDO.class);
        verify(diaryEntryMapper).insert(diaryCaptor.capture());
        DiaryEntryDO diary = diaryCaptor.getValue();
        assertEquals(0, diary.getStatus());
        assertEquals(0, diary.getVisibility());
    }

    @Test
    void shouldRejectCreateDiaryWhenCategoryDoesNotBelongToOwner() {
        DiarySaveReqVO reqVO = new DiarySaveReqVO();
        reqVO.setTitle("No category");
        reqVO.setCategoryId(404L);
        when(diaryCategoryMapper.selectById(404L)).thenReturn(DiaryCategoryDO.builder()
                .id(404L).ownerUserId(2L).status(1).build());

        var exception = assertThrows(com.xiaoyang.diary.framework.common.exception.ServiceException.class,
                () -> diaryService.createDiary(reqVO, 1L));

        assertEquals(DIARY_CATEGORY_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void shouldAllowAnonymousDiaryPageQuery() {
        DiaryPageReqVO reqVO = new DiaryPageReqVO();
        reqVO.setStatus(0);
        reqVO.setVisibility(0);
        reqVO.setPageSize(200);
        when(diaryEntryMapper.selectPage(eq(reqVO), isNull(Long.class))).thenReturn(new PageResult<>(List.of(), 0L));

        PageResult<DiaryEntryDO> pageResult = diaryService.getDiaryPage(reqVO, null);

        assertEquals(0L, pageResult.getTotal());
        verify(diaryEntryMapper).selectPage(eq(reqVO), isNull(Long.class));
        assertEquals(1, reqVO.getStatus());
        assertEquals(2, reqVO.getVisibility());
        assertEquals(50, reqVO.getPageSize());
    }

    @Test
    void shouldLimitLoggedInDiaryPageSize() {
        DiaryPageReqVO reqVO = new DiaryPageReqVO();
        reqVO.setPageSize(200);
        when(diaryEntryMapper.selectPage(eq(reqVO), eq(1L))).thenReturn(new PageResult<>(List.of(), 0L));

        diaryService.getDiaryPage(reqVO, 1L);

        verify(diaryEntryMapper).selectPage(eq(reqVO), eq(1L));
        assertEquals(50, reqVO.getPageSize());
    }
}
