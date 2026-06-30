package com.xiaoyang.diary.module.file.service;

import cn.hutool.core.util.StrUtil;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileObjectPageReqVO;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileUploadReqVO;
import com.xiaoyang.diary.module.file.dal.dataobject.FileObjectDO;
import com.xiaoyang.diary.module.file.dal.mysql.FileObjectMapper;
import com.xiaoyang.diary.module.file.enums.FileCategoryEnum;
import com.xiaoyang.diary.module.file.enums.FileObjectStatusEnum;
import com.xiaoyang.diary.module.file.enums.FileVisibilityEnum;
import com.xiaoyang.diary.module.file.framework.config.FileStorageProperties;
import com.xiaoyang.diary.module.file.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.module.file.enums.ErrorCodeConstants.FILE_ACCESS_DENIED;
import static com.xiaoyang.diary.module.file.enums.ErrorCodeConstants.FILE_EMPTY;
import static com.xiaoyang.diary.module.file.enums.ErrorCodeConstants.FILE_NOT_EXISTS;
import static com.xiaoyang.diary.module.file.enums.ErrorCodeConstants.FILE_PREVIEW_NOT_SUPPORTED;
import static com.xiaoyang.diary.module.file.enums.ErrorCodeConstants.FILE_UPLOAD_FAIL;

@Service
@RequiredArgsConstructor
public class FileObjectServiceImpl implements FileObjectService {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final FileStorageService fileStorageService;
    private final FileObjectMapper fileObjectMapper;
    private final FileCategoryDetector fileCategoryDetector;
    private final FileStorageProperties fileStorageProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileObjectDO upload(MultipartFile file, FileUploadReqVO reqVO, Long ownerUserId) {
        if (file == null || file.isEmpty()) {
            throw exception(FILE_EMPTY);
        }
        byte[] content = readContent(file);
        String originalName = StrUtil.blankToDefault(file.getOriginalFilename(), "file");
        String contentType = StrUtil.blankToDefault(file.getContentType(), DEFAULT_CONTENT_TYPE);
        String extension = getExtension(originalName);
        FileCategoryEnum category = fileCategoryDetector.detect(originalName, contentType);
        String objectKey = buildObjectKey(category, extension);

        fileStorageService.putObject(objectKey, contentType, content);

        FileObjectDO fileObject = FileObjectDO.builder()
                .bucket(fileStorageProperties.getS3().getBucket())
                .objectKey(objectKey)
                .originalName(originalName)
                .extension(extension)
                .contentType(contentType)
                .fileCategory(category.getCategory())
                .size((long) content.length)
                .sha256(sha256Hex(content))
                .ownerUserId(ownerUserId)
                .businessType(reqVO == null ? null : reqVO.getBusinessType())
                .businessId(reqVO == null ? null : reqVO.getBusinessId())
                .visibility(FileVisibilityEnum.PRIVATE.getVisibility())
                .previewSupported(isPreviewSupported(category, extension))
                .status(FileObjectStatusEnum.NORMAL.getStatus())
                .build();
        fileObjectMapper.insert(fileObject);
        return fileObject;
    }

    @Override
    public FileObjectDO getFile(Long id, Long ownerUserId) {
        FileObjectDO fileObject = getFile(id);
        validateOwner(fileObject, ownerUserId);
        return fileObject;
    }

    private FileObjectDO getFile(Long id) {
        FileObjectDO fileObject = fileObjectMapper.selectById(id);
        if (fileObject == null) {
            throw exception(FILE_NOT_EXISTS);
        }
        return fileObject;
    }

    @Override
    public PageResult<FileObjectDO> getFilePage(FileObjectPageReqVO reqVO, Long ownerUserId) {
        return fileObjectMapper.selectPage(reqVO, ownerUserId);
    }

    @Override
    public String createPreviewUrl(Long id, Long ownerUserId) {
        FileObjectDO fileObject = getFile(id);
        validateOwner(fileObject, ownerUserId);
        if (!Boolean.TRUE.equals(fileObject.getPreviewSupported())) {
            throw exception(FILE_PREVIEW_NOT_SUPPORTED);
        }
        return fileStorageService.createPreviewUrl(fileObject.getObjectKey(),
                fileStorageProperties.getS3().getPreviewUrlExpire());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id, Long ownerUserId) {
        FileObjectDO fileObject = getFile(id);
        validateOwner(fileObject, ownerUserId);
        fileStorageService.deleteObject(fileObject.getObjectKey());
        fileObjectMapper.deleteById(id);
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw exception(FILE_UPLOAD_FAIL);
        }
    }

    private void validateOwner(FileObjectDO fileObject, Long ownerUserId) {
        if (!Objects.equals(fileObject.getOwnerUserId(), ownerUserId)) {
            throw exception(FILE_ACCESS_DENIED);
        }
    }

    private String buildObjectKey(FileCategoryEnum category, String extension) {
        String datePath = DATE_PATH_FORMATTER.format(LocalDate.now());
        String filename = UUID.randomUUID().toString().replace("-", "");
        if (StrUtil.isNotBlank(extension)) {
            filename = filename + "." + extension;
        }
        return category.getCategory() + "/" + datePath + "/" + filename;
    }

    private String getExtension(String filename) {
        if (StrUtil.isBlank(filename) || !filename.contains(".")) {
            return null;
        }
        return StrUtil.subAfter(filename, '.', true).toLowerCase();
    }

    private boolean isPreviewSupported(FileCategoryEnum category, String extension) {
        if (category == FileCategoryEnum.IMAGE || category == FileCategoryEnum.AUDIO
                || category == FileCategoryEnum.VIDEO || category == FileCategoryEnum.TEXT) {
            return true;
        }
        return category == FileCategoryEnum.DOCUMENT && Objects.equals("pdf", extension);
    }

    private String sha256Hex(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw exception(FILE_UPLOAD_FAIL);
        }
    }
}
