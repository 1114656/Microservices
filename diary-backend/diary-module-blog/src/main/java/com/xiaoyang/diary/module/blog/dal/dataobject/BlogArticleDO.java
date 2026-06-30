package com.xiaoyang.diary.module.blog.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName(value = "blog_article", autoResultMap = true)
@KeySequence("blog_article_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleDO extends BaseDO {

    @TableId
    private Long id;
    private Long ownerUserId;
    private String title;
    private String contentMarkdown;
    private String summary;
    private Long coverFileId;
    private Integer status;
    private Integer visibility;

}
