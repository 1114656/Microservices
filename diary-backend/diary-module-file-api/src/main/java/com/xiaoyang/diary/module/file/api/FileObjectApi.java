package com.xiaoyang.diary.module.file.api;

import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.file.api.fallback.FileObjectApiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "diary-file-service",
        path = "/internal-api/file",
        fallbackFactory = FileObjectApiFallbackFactory.class
)
public interface FileObjectApi {

    @GetMapping("/validate-owner-file")
    CommonResult<Boolean> validateOwnerFile(@RequestParam("id") Long id,
                                            @RequestParam("ownerUserId") Long ownerUserId);

}
