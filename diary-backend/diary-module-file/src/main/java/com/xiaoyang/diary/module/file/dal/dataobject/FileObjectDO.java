package com.xiaoyang.diary.module.file.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import com.xiaoyang.diary.module.file.enums.FileCategoryEnum;
import com.xiaoyang.diary.module.file.enums.FileObjectStatusEnum;
import com.xiaoyang.diary.module.file.enums.FileVisibilityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName(value = "file_object", autoResultMap = true)
@KeySequence("file_object_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileObjectDO extends BaseDO {

    @TableId
    private Long id;
    private String bucket;
    private String objectKey;
    private String originalName;
    private String extension;
    private String contentType;
    /**
     * @see FileCategoryEnum#getCategory()
     */
    private String fileCategory;
    private Long size;
    private String sha256;
    private Long ownerUserId;
    private String businessType;
    private String businessId;
    /**
     * @see FileVisibilityEnum#getVisibility()
     */
    private String visibility;
    private Boolean previewSupported;
    /**
     * @see FileObjectStatusEnum#getStatus()
     */
    private Integer status;

}
