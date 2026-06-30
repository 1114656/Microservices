package com.xiaoyang.diary.module.blog.service;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticlePageReqVO;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticleSaveReqVO;
import com.xiaoyang.diary.module.blog.dal.dataobject.BlogArticleDO;

public interface BlogArticleService {

    Long createArticle(BlogArticleSaveReqVO reqVO, Long ownerUserId);

    void updateArticle(BlogArticleSaveReqVO reqVO, Long ownerUserId);

    void deleteArticle(Long id, Long ownerUserId);

    BlogArticleDO getArticle(Long id, Long ownerUserId);

    PageResult<BlogArticleDO> getArticlePage(BlogArticlePageReqVO reqVO, Long ownerUserId);

}
