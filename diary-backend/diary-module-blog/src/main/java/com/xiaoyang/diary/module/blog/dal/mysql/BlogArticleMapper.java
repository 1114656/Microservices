package com.xiaoyang.diary.module.blog.dal.mysql;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticlePageReqVO;
import com.xiaoyang.diary.module.blog.dal.dataobject.BlogArticleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogArticleMapper extends BaseMapperX<BlogArticleDO> {

    int STATUS_PUBLISHED = 1;
    int VISIBILITY_LOGIN = 1;
    int VISIBILITY_PUBLIC = 2;

    default PageResult<BlogArticleDO> selectPage(BlogArticlePageReqVO reqVO, Long ownerUserId) {
        LambdaQueryWrapperX<BlogArticleDO> queryWrapper = new LambdaQueryWrapperX<BlogArticleDO>()
                .likeIfPresent(BlogArticleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(BlogArticleDO::getStatus, reqVO.getStatus())
                .eqIfPresent(BlogArticleDO::getVisibility, reqVO.getVisibility());
        if (ownerUserId == null) {
            queryWrapper.eq(BlogArticleDO::getStatus, STATUS_PUBLISHED)
                    .eq(BlogArticleDO::getVisibility, VISIBILITY_PUBLIC);
        } else {
            queryWrapper.and(wrapper -> wrapper.eq(BlogArticleDO::getOwnerUserId, ownerUserId)
                    .or(accessWrapper -> accessWrapper
                            .eq(BlogArticleDO::getStatus, STATUS_PUBLISHED)
                            .in(BlogArticleDO::getVisibility, List.of(VISIBILITY_LOGIN, VISIBILITY_PUBLIC))));
        }
        return selectPage(reqVO, queryWrapper.orderByDesc(BlogArticleDO::getId));
    }

}
