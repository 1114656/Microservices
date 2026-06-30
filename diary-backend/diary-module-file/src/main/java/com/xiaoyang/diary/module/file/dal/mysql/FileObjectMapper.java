package com.xiaoyang.diary.module.file.dal.mysql;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xiaoyang.diary.module.file.controller.admin.vo.FileObjectPageReqVO;
import com.xiaoyang.diary.module.file.dal.dataobject.FileObjectDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileObjectMapper extends BaseMapperX<FileObjectDO> {

    default PageResult<FileObjectDO> selectPage(FileObjectPageReqVO reqVO, Long ownerUserId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileObjectDO>()
                .eqIfPresent(FileObjectDO::getOwnerUserId, ownerUserId)
                .eqIfPresent(FileObjectDO::getFileCategory, reqVO.getFileCategory())
                .eqIfPresent(FileObjectDO::getBusinessType, reqVO.getBusinessType())
                .eqIfPresent(FileObjectDO::getBusinessId, reqVO.getBusinessId())
                .orderByDesc(FileObjectDO::getId));
    }

}
