package com.xiaoyang.diary.module.file.dal.mysql;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xiaoyang.diary.module.file.dal.dataobject.FileOutboxEventDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FileOutboxEventMapper extends BaseMapperX<FileOutboxEventDO> {

    default List<FileOutboxEventDO> selectPending(LocalDateTime now, int limit) {
        return selectPage(new Page<>(1, limit), new LambdaQueryWrapperX<FileOutboxEventDO>()
                .eq(FileOutboxEventDO::getStatus, 0)
                .le(FileOutboxEventDO::getNextRetryTime, now)
                .orderByAsc(FileOutboxEventDO::getId)).getRecords();
    }

    default void markSent(Long id, LocalDateTime sentTime) {
        update(null, new LambdaUpdateWrapper<FileOutboxEventDO>()
                .eq(FileOutboxEventDO::getId, id)
                .set(FileOutboxEventDO::getStatus, 1)
                .set(FileOutboxEventDO::getSentTime, sentTime)
                .set(FileOutboxEventDO::getLastError, null));
    }

    default void markFailure(Long id, int retryCount, LocalDateTime nextRetryTime, String lastError, int status) {
        update(null, new LambdaUpdateWrapper<FileOutboxEventDO>()
                .eq(FileOutboxEventDO::getId, id)
                .set(FileOutboxEventDO::getStatus, status)
                .set(FileOutboxEventDO::getRetryCount, retryCount)
                .set(FileOutboxEventDO::getNextRetryTime, nextRetryTime)
                .set(FileOutboxEventDO::getLastError, lastError));
    }
}
