package com.xiaoyang.diary.module.file.api.fallback;

import com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.file.api.FileObjectApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileObjectApiFallbackFactory implements FallbackFactory<FileObjectApi> {

    @Override
    public FileObjectApi create(Throwable cause) {
        return (id, ownerUserId) -> {
            log.warn("File service fallback triggered, id={}, ownerUserId={}", id, ownerUserId, cause);
            return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        };
    }
}
