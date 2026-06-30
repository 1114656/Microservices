package com.xiaoyang.diary.module.system.service.permission;

import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.xiaoyang.diary.module.system.dal.dataobject.permission.RoleDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

public interface RoleService {

    Long createRole(@Valid RoleSaveReqVO createReqVO, Integer type);

    void updateRole(@Valid RoleSaveReqVO updateReqVO);

    void deleteRole(Long id);

    void deleteRoleList(List<Long> ids);

    RoleDO getRole(Long id);

    RoleDO getRoleFromCache(Long id);

    List<RoleDO> getRoleList(Collection<Long> ids);

    List<RoleDO> getRoleListFromCache(Collection<Long> ids);

    List<RoleDO> getRoleListByStatus(Collection<Integer> statuses);

    List<RoleDO> getRoleList();

    PageResult<RoleDO> getRolePage(RolePageReqVO reqVO);

    boolean hasAnySuperAdmin(Collection<Long> ids);

    void validateRoleList(Collection<Long> ids);

}
