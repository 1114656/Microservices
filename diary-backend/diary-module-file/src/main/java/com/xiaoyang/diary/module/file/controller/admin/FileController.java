package com.xiaoyang.diary.module.file.controller.admin;

import cn.hutool.core.collection.CollUtil;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileObjectPageReqVO;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileObjectRespVO;
import com.xiaoyang.diary.module.file.controller.admin.vo.FilePreviewUrlRespVO;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileUploadReqVO;
import com.xiaoyang.diary.module.file.dal.dataobject.FileObjectDO;
import com.xiaoyang.diary.module.file.service.FileObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;
import static com.xiaoyang.diary.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 文件")
@RestController
@RequestMapping("/file")
@Validated
public class FileController {

    @Resource
    private FileObjectService fileObjectService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件")
    public CommonResult<FileObjectRespVO> upload(@RequestParam("file") MultipartFile file,
                                                 @Valid FileUploadReqVO reqVO) {
        FileObjectDO fileObject = fileObjectService.upload(file, reqVO, getLoginUserId());
        return success(convert(fileObject));
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件详情")
    @Parameter(name = "id", description = "文件编号", required = true)
    public CommonResult<FileObjectRespVO> getFile(@RequestParam("id") Long id) {
        return success(convert(fileObjectService.getFile(id, getLoginUserId())));
    }

    @GetMapping("/preview-url")
    @Operation(summary = "获得文件预览地址")
    @Parameter(name = "id", description = "文件编号", required = true)
    public CommonResult<FilePreviewUrlRespVO> getPreviewUrl(@RequestParam("id") Long id) {
        String previewUrl = fileObjectService.createPreviewUrl(id, getLoginUserId());
        return success(new FilePreviewUrlRespVO(previewUrl));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    public CommonResult<PageResult<FileObjectRespVO>> getFilePage(@Valid FileObjectPageReqVO pageReqVO) {
        PageResult<FileObjectDO> pageResult = fileObjectService.getFilePage(pageReqVO, getLoginUserId());
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        List<FileObjectRespVO> list = pageResult.getList().stream().map(this::convert).toList();
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "文件编号", required = true)
    public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) {
        fileObjectService.deleteFile(id, getLoginUserId());
        return success(true);
    }

    private FileObjectRespVO convert(FileObjectDO fileObject) {
        FileObjectRespVO respVO = new FileObjectRespVO();
        respVO.setId(fileObject.getId());
        respVO.setOriginalName(fileObject.getOriginalName());
        respVO.setExtension(fileObject.getExtension());
        respVO.setContentType(fileObject.getContentType());
        respVO.setFileCategory(fileObject.getFileCategory());
        respVO.setSize(fileObject.getSize());
        respVO.setSha256(fileObject.getSha256());
        respVO.setBusinessType(fileObject.getBusinessType());
        respVO.setBusinessId(fileObject.getBusinessId());
        respVO.setPreviewSupported(fileObject.getPreviewSupported());
        respVO.setCreateTime(fileObject.getCreateTime());
        return respVO;
    }
}
