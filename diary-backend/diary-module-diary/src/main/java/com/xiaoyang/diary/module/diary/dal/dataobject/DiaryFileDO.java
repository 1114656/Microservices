package com.xiaoyang.diary.module.diary.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName(value = "diary_file", autoResultMap = true)
@KeySequence("diary_file_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryFileDO extends BaseDO {

    @TableId
    private Long id;
    private Long diaryId;
    private Long fileId;
    private Integer sort;
    private String usageType;

}
