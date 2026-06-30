package com.xiaoyang.diary.module.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.annotations.VisibleForTesting;
import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.common.util.collection.CollectionUtils;
import com.xiaoyang.diary.framework.common.util.object.BeanUtils;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.xiaoyang.diary.module.system.dal.dataobject.permission.RoleDO;
import com.xiaoyang.diary.module.system.dal.mysql.permission.RoleMapper;
import com.xiaoyang.diary.module.system.dal.redis.RedisKeyConstants;
import com.xiaoyang.diary.module.system.enums.permission.RoleCodeEnum;
import com.xiaoyang.diary.module.system.enums.permission.RoleTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.framework.common.util.collection.CollectionUtils.convertMap;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.ROLE_ADMIN_CODE_ERROR;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.ROLE_CODE_DUPLICATE;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.ROLE_IS_DISABLE;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.ROLE_NAME_DUPLICATE;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.ROLE_NOT_EXISTS;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleSaveReqVO createReqVO, Integer type) {
        validateRoleDuplicate(createReqVO.getName(), createReqVO.getCode(), null);
        RoleDO role = BeanUtils.toBean(createReqVO, RoleDO.class)
                .setType(ObjectUtil.defaultIfNull(type, RoleTypeEnum.CUSTOM.getType()))
                .setStatus(ObjUtil.defaultIfNull(createReqVO.getStatus(), CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(role);
        return role.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#updateReqVO.id")
    public void updateRole(RoleSaveReqVO updateReqVO) {
        validateRoleForUpdate(updateReqVO.getId());
        validateRoleDuplicate(updateReqVO.getName(), updateReqVO.getCode(), updateReqVO.getId());
        roleMapper.updateById(BeanUtils.toBean(updateReqVO, RoleDO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    public void deleteRole(Long id) {
        validateRoleForUpdate(id);
        roleMapper.deleteById(id);
        permissionService.processRoleDeleted(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleList(List<Long> ids) {
        ids.forEach(this::validateRoleForUpdate);
        roleMapper.deleteByIds(ids);
        ids.forEach(id -> permissionService.processRoleDeleted(id));
    }

    @VisibleForTesting
    void validateRoleDuplicate(String name, String code, Long id) {
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        RoleDO role = roleMapper.selectByName(name);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_NAME_DUPLICATE, name);
        }
        if (!StringUtils.hasText(code)) {
            return;
        }
        role = roleMapper.selectByCode(code);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_CODE_DUPLICATE, code);
        }
    }

    @VisibleForTesting
    RoleDO validateRoleForUpdate(Long id) {
        RoleDO role = roleMapper.selectById(id);
        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        if (RoleTypeEnum.SYSTEM.getType().equals(role.getType())) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
        return role;
    }

    @Override
    public RoleDO getRole(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id", unless = "#result == null")
    public RoleDO getRoleFromCache(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public List<RoleDO> getRoleListByStatus(Collection<Integer> statuses) {
        return roleMapper.selectListByStatus(statuses);
    }

    @Override
    public List<RoleDO> getRoleList() {
        return roleMapper.selectList();
    }

    @Override
    public List<RoleDO> getRoleList(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleMapper.selectByIds(ids);
    }

    @Override
    public List<RoleDO> getRoleListFromCache(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        RoleServiceImpl self = getSelf();
        return CollectionUtils.convertList(ids, self::getRoleFromCache);
    }

    @Override
    public PageResult<RoleDO> getRolePage(RolePageReqVO reqVO) {
        return roleMapper.selectPage(reqVO);
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        RoleServiceImpl self = getSelf();
        return ids.stream().anyMatch(id -> {
            RoleDO role = self.getRoleFromCache(id);
            return role != null && RoleCodeEnum.isSuperAdmin(role.getCode());
        });
    }

    @Override
    public void validateRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<RoleDO> roles = roleMapper.selectByIds(ids);
        Map<Long, RoleDO> roleMap = convertMap(roles, RoleDO::getId);
        ids.forEach(id -> {
            RoleDO role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }

    private RoleServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
