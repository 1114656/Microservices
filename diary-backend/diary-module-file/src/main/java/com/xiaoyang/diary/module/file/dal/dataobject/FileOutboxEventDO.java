package com.xiaoyang.diary.module.file.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaoyang.diary.framework.mybatis.core.dataobject.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName(value = "file_outbox_event", autoResultMap = true)
@KeySequence("file_outbox_event_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileOutboxEventDO extends BaseDO {

    @TableId
    private Long id;
    private String eventId;
    private String eventType;
    private String topic;
    private String tag;
    private String payload;
    /**
     * 0 pending, 1 sent, 2 failed.
     */
    private Integer status;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
    private LocalDateTime sentTime;
    private String lastError;

}
