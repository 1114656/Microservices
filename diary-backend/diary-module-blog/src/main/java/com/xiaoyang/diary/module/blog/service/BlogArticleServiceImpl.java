package com.xiaoyang.diary.module.blog.service;

import cn.hutool.core.util.StrUtil;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticlePageReqVO;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticleSaveReqVO;
import com.xiaoyang.diary.module.blog.dal.dataobject.BlogArticleDO;
import com.xiaoyang.diary.module.blog.dal.mysql.BlogArticleMapper;
import com.xiaoyang.diary.module.file.service.FileObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.module.blog.enums.ErrorCodeConstants.BLOG_ARTICLE_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class BlogArticleServiceImpl implements BlogArticleService {

    private static final int STATUS_PUBLISHED = 1;
    private static final int VISIBILITY_LOGIN = 1;
    private static final int VISIBILITY_PUBLIC = 2;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int SUMMARY_MAX_LENGTH = 200;

    private final BlogArticleMapper blogArticleMapper;
    private final FileObjectService fileObjectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(BlogArticleSaveReqVO reqVO, Long ownerUserId) {
        validateCoverFile(reqVO.getCoverFileId(), ownerUserId);
        BlogArticleDO article = BlogArticleDO.builder()
                .ownerUserId(ownerUserId)
                .title(reqVO.getTitle())
                .contentMarkdown(reqVO.getContentMarkdown())
                .summary(buildSummary(reqVO.getContentMarkdown()))
                .coverFileId(reqVO.getCoverFileId())
                .status(defaultStatus(reqVO.getStatus()))
                .visibility(defaultVisibility(reqVO.getVisibility()))
                .build();
        blogArticleMapper.insert(article);
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(BlogArticleSaveReqVO reqVO, Long ownerUserId) {
        if (reqVO.getId() == null) {
            throw exception(BLOG_ARTICLE_NOT_EXISTS);
        }
        getArticle(reqVO.getId(), ownerUserId);
        validateCoverFile(reqVO.getCoverFileId(), ownerUserId);
        BlogArticleDO article = BlogArticleDO.builder()
                .id(reqVO.getId())
                .ownerUserId(ownerUserId)
                .title(reqVO.getTitle())
                .contentMarkdown(reqVO.getContentMarkdown())
                .summary(buildSummary(reqVO.getContentMarkdown()))
                .coverFileId(reqVO.getCoverFileId())
                .status(defaultStatus(reqVO.getStatus()))
                .visibility(defaultVisibility(reqVO.getVisibility()))
                .build();
        blogArticleMapper.updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id, Long ownerUserId) {
        getArticle(id, ownerUserId);
        blogArticleMapper.deleteById(id);
    }

    @Override
    public BlogArticleDO getArticle(Long id, Long ownerUserId) {
        BlogArticleDO article = blogArticleMapper.selectById(id);
        if (article == null || !Objects.equals(article.getOwnerUserId(), ownerUserId)) {
            throw exception(BLOG_ARTICLE_NOT_EXISTS);
        }
        return article;
    }

    @Override
    public PageResult<BlogArticleDO> getArticlePage(BlogArticlePageReqVO reqVO, Long ownerUserId) {
        normalizePageQuery(reqVO, ownerUserId);
        return blogArticleMapper.selectPage(reqVO, ownerUserId);
    }

    private void validateCoverFile(Long coverFileId, Long ownerUserId) {
        if (coverFileId != null) {
            fileObjectService.getFile(coverFileId, ownerUserId);
        }
    }

    private String buildSummary(String markdown) {
        String text = StrUtil.blankToDefault(markdown, "")
                .replaceAll("(?s)```.*?```", " ")
                .replaceAll("[#>*_`\\-\\[\\]()]", " ")
                .replaceAll("\\s+", " ")
                .trim();
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

    private void normalizePageQuery(BlogArticlePageReqVO reqVO, Long ownerUserId) {
        if (reqVO.getPageSize() == null || reqVO.getPageSize() > MAX_PAGE_SIZE) {
            reqVO.setPageSize(MAX_PAGE_SIZE);
        }
        if (ownerUserId == null) {
            reqVO.setStatus(STATUS_PUBLISHED);
            reqVO.setVisibility(VISIBILITY_PUBLIC);
        }
    }
}
