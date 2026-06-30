package com.xiaoyang.diary.module.system.convert.user;

import com.xiaoyang.diary.framework.common.util.collection.CollectionUtils;
import com.xiaoyang.diary.framework.common.util.object.BeanUtils;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.user.UserRespVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.xiaoyang.diary.module.system.dal.dataobject.permission.RoleDO;
import com.xiaoyang.diary.module.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list) {
        return BeanUtils.toBean(list, UserRespVO.class);
    }

    default UserRespVO convert(AdminUserDO user) {
        return BeanUtils.toBean(user, UserRespVO.class);
    }

    default List<UserSimpleRespVO> convertSimpleList(List<AdminUserDO> list) {
        return CollectionUtils.convertList(list, user -> BeanUtils.toBean(user, UserSimpleRespVO.class));
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        return userVO;
    }

}
