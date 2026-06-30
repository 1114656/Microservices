package com.xiaoyang.diary.module.diary.dal.mysql;

import com.xiaoyang.diary.framework.mybatis.core.mapper.BaseMapperX;
import com.xiaoyang.diary.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiaryCategoryMapper extends BaseMapperX<DiaryCategoryDO> {

    default List<DiaryCategoryDO> selectListByOwnerUserId(Long ownerUserId) {
        return selectList(new LambdaQueryWrapperX<DiaryCategoryDO>()
                .eq(DiaryCategoryDO::getOwnerUserId, ownerUserId)
                .orderByAsc(DiaryCategoryDO::getSort)
                .orderByDesc(DiaryCategoryDO::getId));
    }

}
