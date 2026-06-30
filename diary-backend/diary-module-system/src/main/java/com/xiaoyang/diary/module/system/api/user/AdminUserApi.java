package com.xiaoyang.diary.module.system.api.user;

import com.xiaoyang.diary.framework.common.util.collection.CollectionUtils;
import com.xiaoyang.diary.module.system.api.user.dto.AdminUserRespDTO;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface AdminUserApi {

    AdminUserRespDTO getUser(Long id);

    List<AdminUserRespDTO> getUserList(Collection<Long> ids);

    default Map<Long, AdminUserRespDTO> getUserMap(Collection<Long> ids) {
        List<AdminUserRespDTO> users = getUserList(ids);
        return CollectionUtils.convertMap(users, AdminUserRespDTO::getId);
    }

    default void validateUser(Long id) {
        validateUserList(Collections.singleton(id));
    }

    void validateUserList(Collection<Long> ids);

}
