package com.xiaoyang.diary.module.file.service;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileObjectPageReqVO;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileUploadReqVO;
import com.xiaoyang.diary.module.file.dal.dataobject.FileObjectDO;
import org.springframework.web.multipart.MultipartFile;

public interface FileObjectService {

    FileObjectDO upload(MultipartFile file, FileUploadReqVO reqVO, Long ownerUserId);

    FileObjectDO getFile(Long id, Long ownerUserId);

    PageResult<FileObjectDO> getFilePage(FileObjectPageReqVO reqVO, Long ownerUserId);

    String createPreviewUrl(Long id, Long ownerUserId);

    void deleteFile(Long id, Long ownerUserId);

}
