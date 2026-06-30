package com.xiaoyang.diary.module.blog.service;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.test.core.ut.BaseMockitoUnitTest;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticlePageReqVO;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticleSaveReqVO;
import com.xiaoyang.diary.module.blog.dal.dataobject.BlogArticleDO;
import com.xiaoyang.diary.module.blog.dal.mysql.BlogArticleMapper;
import com.xiaoyang.diary.module.file.api.FileObjectApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.List;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BlogArticleServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private BlogArticleMapper blogArticleMapper;
    @Mock
    private FileObjectApi fileObjectApi;

    private BlogArticleServiceImpl blogArticleService;

    @BeforeEach
    void setUp() {
        blogArticleService = new BlogArticleServiceImpl(blogArticleMapper, fileObjectApi);
    }

    @Test
    void shouldCreateArticleAndKeepMarkdownCodeFenceUnchanged() {
        String markdown = """
                # Upload API

                ```java
                @PostMapping("/upload")
                public CommonResult<Long> upload(MultipartFile file) {
                    return success(fileService.upload(file));
                }
                ```
                """;
        BlogArticleSaveReqVO reqVO = new BlogArticleSaveReqVO();
        reqVO.setTitle("Upload API");
        reqVO.setContentMarkdown(markdown);
        reqVO.setCoverFileId(10L);
        when(fileObjectApi.validateOwnerFile(10L, 1L)).thenReturn(success(true));
        when(blogArticleMapper.insert(any(BlogArticleDO.class))).thenAnswer(invocation -> {
            BlogArticleDO article = invocation.getArgument(0);
            article.setId(100L);
            return 1;
        });

        Long articleId = blogArticleService.createArticle(reqVO, 1L);

        assertEquals(100L, articleId);
        verify(fileObjectApi).validateOwnerFile(10L, 1L);
        ArgumentCaptor<BlogArticleDO> articleCaptor = ArgumentCaptor.forClass(BlogArticleDO.class);
        verify(blogArticleMapper).insert(articleCaptor.capture());
        BlogArticleDO article = articleCaptor.getValue();
        assertEquals("Upload API", article.getTitle());
        assertEquals(markdown, article.getContentMarkdown());
        assertEquals(10L, article.getCoverFileId());
        assertEquals(1L, article.getOwnerUserId());
        assertEquals(1, article.getStatus());
        assertEquals(1, article.getVisibility());
    }

    @Test
    void shouldCreateArticleWithCustomStatusAndVisibility() {
        BlogArticleSaveReqVO reqVO = new BlogArticleSaveReqVO();
        reqVO.setTitle("Private draft");
        reqVO.setContentMarkdown("draft");
        reqVO.setStatus(0);
        reqVO.setVisibility(0);
        when(blogArticleMapper.insert(any(BlogArticleDO.class))).thenAnswer(invocation -> {
            BlogArticleDO article = invocation.getArgument(0);
            article.setId(101L);
            return 1;
        });

        blogArticleService.createArticle(reqVO, 1L);

        ArgumentCaptor<BlogArticleDO> articleCaptor = ArgumentCaptor.forClass(BlogArticleDO.class);
        verify(blogArticleMapper).insert(articleCaptor.capture());
        BlogArticleDO article = articleCaptor.getValue();
        assertEquals(0, article.getStatus());
        assertEquals(0, article.getVisibility());
    }

    @Test
    void shouldAllowAnonymousArticlePageQuery() {
        BlogArticlePageReqVO reqVO = new BlogArticlePageReqVO();
        reqVO.setStatus(0);
        reqVO.setVisibility(0);
        reqVO.setPageSize(200);
        when(blogArticleMapper.selectPage(eq(reqVO), isNull(Long.class))).thenReturn(new PageResult<>(List.of(), 0L));

        PageResult<BlogArticleDO> pageResult = blogArticleService.getArticlePage(reqVO, null);

        assertEquals(0L, pageResult.getTotal());
        verify(blogArticleMapper).selectPage(eq(reqVO), isNull(Long.class));
        assertEquals(1, reqVO.getStatus());
        assertEquals(2, reqVO.getVisibility());
        assertEquals(50, reqVO.getPageSize());
    }

    @Test
    void shouldLimitLoggedInArticlePageSize() {
        BlogArticlePageReqVO reqVO = new BlogArticlePageReqVO();
        reqVO.setPageSize(200);
        when(blogArticleMapper.selectPage(eq(reqVO), eq(1L))).thenReturn(new PageResult<>(List.of(), 0L));

        blogArticleService.getArticlePage(reqVO, 1L);

        verify(blogArticleMapper).selectPage(eq(reqVO), eq(1L));
        assertEquals(50, reqVO.getPageSize());
    }
}
