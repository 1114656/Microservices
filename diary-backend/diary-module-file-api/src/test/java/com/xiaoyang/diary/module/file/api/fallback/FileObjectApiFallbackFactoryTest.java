package com.xiaoyang.diary.module.file.api.fallback;

import com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.file.api.FileObjectApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileObjectApiFallbackFactoryTest {

    @Test
    void shouldReturnRecognizableDegradeResult() {
        FileObjectApiFallbackFactory factory = new FileObjectApiFallbackFactory();
        FileObjectApi fallback = factory.create(new IllegalStateException("file service down"));

        CommonResult<Boolean> result = fallback.validateOwnerFile(10L, 1L);

        assertEquals(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), result.getCode());
        assertTrue(result.getMsg().contains("file-service unavailable"));
    }
}
