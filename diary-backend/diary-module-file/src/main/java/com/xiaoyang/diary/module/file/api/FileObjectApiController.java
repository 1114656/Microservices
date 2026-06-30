package com.xiaoyang.diary.module.file.api;

import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.file.service.FileObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/internal-api/file")
@RequiredArgsConstructor
public class FileObjectApiController {

    private final FileObjectService fileObjectService;

    @GetMapping("/validate-owner-file")
    public CommonResult<Boolean> validateOwnerFile(@RequestParam("id") Long id,
                                                   @RequestParam("ownerUserId") Long ownerUserId) {
        fileObjectService.getFile(id, ownerUserId);
        return success(true);
    }
}
