package com.xiaoyang.diary.module.diary.service;

import com.xiaoyang.diary.framework.common.exception.ServiceException;
import com.xiaoyang.diary.framework.test.core.ut.BaseMockitoUnitTest;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategoryRespVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategorySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryCategoryMapper;
import com.xiaoyang.diary.module.diary.dal.mysql.DiaryEntryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static com.xiaoyang.diary.module.diary.enums.ErrorCodeConstants.DIARY_CATEGORY_HAS_ENTRIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiaryCategoryServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private DiaryCategoryMapper diaryCategoryMapper;
    @Mock
    private DiaryEntryMapper diaryEntryMapper;

    private DiaryCategoryServiceImpl diaryCategoryService;

    @BeforeEach
    void setUp() {
        diaryCategoryService = new DiaryCategoryServiceImpl(diaryCategoryMapper, diaryEntryMapper);
    }

    @Test
    void shouldReturnCategoryCountsSplitByDiaryStatus() {
        DiaryCategoryDO category = DiaryCategoryDO.builder()
                .id(10L)
                .ownerUserId(1L)
                .name("Life")
                .sort(1)
                .status(1)
                .build();
        when(diaryCategoryMapper.selectListByOwnerUserId(1L)).thenReturn(List.of(category));
        when(diaryEntryMapper.selectCategoryStatusCounts(1L, List.of(10L)))
                .thenReturn(List.of(
                        new DiaryEntryMapper.CategoryStatusCount(10L, 0, 2L),
                        new DiaryEntryMapper.CategoryStatusCount(10L, 1, 3L)
                ));

        List<DiaryCategoryRespVO> categories = diaryCategoryService.getCategoryList(1L);

        assertEquals(1, categories.size());
        DiaryCategoryRespVO respVO = categories.get(0);
        assertEquals(10L, respVO.getId());
        assertEquals("Life", respVO.getName());
        assertEquals(5L, respVO.getTotalCount());
        assertEquals(2L, respVO.getDraftCount());
        assertEquals(3L, respVO.getPublishedCount());
    }

    @Test
    void shouldRejectDeleteWhenCategoryHasAnyDiaryEntries() {
        when(diaryCategoryMapper.selectById(10L)).thenReturn(DiaryCategoryDO.builder()
                .id(10L)
                .ownerUserId(1L)
                .name("Life")
                .build());
        when(diaryEntryMapper.selectCategoryCounts(1L, List.of(10L))).thenReturn(Map.of(10L, 1L));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> diaryCategoryService.deleteCategory(10L, 1L));

        assertEquals(DIARY_CATEGORY_HAS_ENTRIES.getCode(), exception.getCode());
    }

    @Test
    void shouldCreateCategoryForOwner() {
        DiaryCategorySaveReqVO reqVO = new DiaryCategorySaveReqVO();
        reqVO.setName("Work");
        reqVO.setSort(2);
        reqVO.setStatus(1);
        when(diaryCategoryMapper.insert(org.mockito.ArgumentMatchers.<DiaryCategoryDO>any()))
                .thenAnswer(invocation -> {
                    DiaryCategoryDO category = invocation.getArgument(0);
                    category.setId(11L);
                    return 1;
                });

        Long id = diaryCategoryService.createCategory(reqVO, 1L);

        assertEquals(11L, id);
        ArgumentCaptor<DiaryCategoryDO> categoryCaptor = ArgumentCaptor.forClass(DiaryCategoryDO.class);
        verify(diaryCategoryMapper).insert(categoryCaptor.capture());
        DiaryCategoryDO category = categoryCaptor.getValue();
        assertEquals(1L, category.getOwnerUserId());
        assertEquals("Work", category.getName());
        assertEquals(2, category.getSort());
        assertEquals(1, category.getStatus());
    }
}
