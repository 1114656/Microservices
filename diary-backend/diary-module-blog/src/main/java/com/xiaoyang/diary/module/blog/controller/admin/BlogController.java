package com.xiaoyang.diary.module.blog.controller.admin;

import cn.hutool.core.collection.CollUtil;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticlePageReqVO;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticleRespVO;
import com.xiaoyang.diary.module.blog.controller.admin.vo.BlogArticleSaveReqVO;
import com.xiaoyang.diary.module.blog.dal.dataobject.BlogArticleDO;
import com.xiaoyang.diary.module.blog.service.BlogArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;
import static com.xiaoyang.diary.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - Blog")
@RestController
@RequestMapping("/admin-api/blog")
@Validated
public class BlogController {

    @Resource
    private BlogArticleService blogArticleService;

    @PostMapping("/create")
    @Operation(summary = "Create blog article")
    @PreAuthorize("@ss.hasPermission('blog:article:create')")
    public CommonResult<Long> createArticle(@Valid @RequestBody BlogArticleSaveReqVO reqVO) {
        return success(blogArticleService.createArticle(reqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update blog article")
    @PreAuthorize("@ss.hasPermission('blog:article:update')")
    public CommonResult<Boolean> updateArticle(@Valid @RequestBody BlogArticleSaveReqVO reqVO) {
        blogArticleService.updateArticle(reqVO, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete blog article")
    @Parameter(name = "id", description = "Article ID", required = true)
    @PreAuthorize("@ss.hasPermission('blog:article:delete')")
    public CommonResult<Boolean> deleteArticle(@RequestParam("id") Long id) {
        blogArticleService.deleteArticle(id, getLoginUserId());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "Get blog article")
    @Parameter(name = "id", description = "Article ID", required = true)
    public CommonResult<BlogArticleRespVO> getArticle(@RequestParam("id") Long id) {
        return success(convert(blogArticleService.getArticle(id, getLoginUserId())));
    }

    @GetMapping("/page")
    @Operation(summary = "Get blog article page")
    @PermitAll
    public CommonResult<PageResult<BlogArticleRespVO>> getArticlePage(@Valid BlogArticlePageReqVO pageReqVO) {
        PageResult<BlogArticleDO> pageResult = blogArticleService.getArticlePage(pageReqVO, getLoginUserId());
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        List<BlogArticleRespVO> list = pageResult.getList().stream().map(this::convert).toList();
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    private BlogArticleRespVO convert(BlogArticleDO article) {
        BlogArticleRespVO respVO = new BlogArticleRespVO();
        respVO.setId(article.getId());
        respVO.setTitle(article.getTitle());
        respVO.setContentMarkdown(article.getContentMarkdown());
        respVO.setSummary(article.getSummary());
        respVO.setCoverFileId(article.getCoverFileId());
        respVO.setStatus(article.getStatus());
        respVO.setVisibility(article.getVisibility());
        respVO.setCreateTime(article.getCreateTime());
        respVO.setUpdateTime(article.getUpdateTime());
        return respVO;
    }

}
