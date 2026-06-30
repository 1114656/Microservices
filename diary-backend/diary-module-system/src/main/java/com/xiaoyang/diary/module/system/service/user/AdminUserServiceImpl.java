package com.xiaoyang.diary.module.system.service.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.common.enums.UserTypeEnum;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.common.util.collection.CollectionUtils;
import com.xiaoyang.diary.framework.common.util.object.BeanUtils;
import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthRegisterReqVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.user.UserPageReqVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.xiaoyang.diary.module.system.dal.dataobject.user.AdminUserDO;
import com.xiaoyang.diary.module.system.dal.mysql.user.AdminUserMapper;
import com.xiaoyang.diary.module.system.service.oauth2.OAuth2TokenService;
import com.xiaoyang.diary.module.system.service.permission.PermissionService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.framework.common.util.collection.CollectionUtils.singleton;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.USER_EMAIL_EXISTS;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.USER_IS_DISABLE;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.USER_MOBILE_EXISTS;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.USER_USERNAME_EXISTS;

@Service("adminUserService")
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper userMapper;
    @Resource
    private PermissionService permissionService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    @Lazy
    private OAuth2TokenService oauth2TokenService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserSaveReqVO createReqVO) {
        validateUserForCreateOrUpdate(null, createReqVO.getUsername(), createReqVO.getMobile(), createReqVO.getEmail());
        AdminUserDO user = BeanUtils.toBean(createReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        user.setPassword(encodePassword(createReqVO.getPassword()));
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public Long registerUser(AuthRegisterReqVO registerReqVO) {
        validateUserForCreateOrUpdate(null, registerReqVO.getUsername(), null, null);
        AdminUserDO user = BeanUtils.toBean(registerReqVO, AdminUserDO.class);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        user.setPassword(encodePassword(registerReqVO.getPassword()));
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserSaveReqVO updateReqVO) {
        updateReqVO.setPassword(null);
        validateUserForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getUsername(),
                updateReqVO.getMobile(), updateReqVO.getEmail());
        userMapper.updateById(BeanUtils.toBean(updateReqVO, AdminUserDO.class));
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        userMapper.updateById(new AdminUserDO().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO) {
        validateUserExists(id);
        validateEmailUnique(id, reqVO.getEmail());
        validateMobileUnique(id, reqVO.getMobile());
        userMapper.updateById(BeanUtils.toBean(reqVO, AdminUserDO.class).setId(id));
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        validateOldPassword(id, reqVO.getOldPassword());
        userMapper.updateById(new AdminUserDO().setId(id).setPassword(encodePassword(reqVO.getNewPassword())));
    }

    @Override
    public void updateUserPassword(Long id, String password) {
        validateUserExists(id);
        userMapper.updateById(new AdminUserDO().setId(id).setPassword(encodePassword(password)));
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        validateUserExists(id);
        userMapper.updateById(new AdminUserDO().setId(id).setStatus(status));
        if (CommonStatusEnum.isDisable(status)) {
            oauth2TokenService.removeAccessToken(id, UserTypeEnum.ADMIN.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        validateUserExists(id);
        userMapper.deleteById(id);
        permissionService.processUserDeleted(id);
        oauth2TokenService.removeAccessToken(id, UserTypeEnum.ADMIN.getValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserList(List<Long> ids) {
        userMapper.deleteByIds(ids);
        ids.forEach(id -> {
            permissionService.processUserDeleted(id);
            oauth2TokenService.removeAccessToken(id, UserTypeEnum.ADMIN.getValue());
        });
    }

    @Override
    public AdminUserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public AdminUserDO getUserByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @Override
    public PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO) {
        Set<Long> userIds = null;
        if (reqVO.getRoleId() != null) {
            userIds = permissionService.getUserRoleIdListByRoleId(singleton(reqVO.getRoleId()));
            if (CollUtil.isEmpty(userIds)) {
                return PageResult.empty();
            }
        }
        return userMapper.selectPage(reqVO, userIds);
    }

    @Override
    public AdminUserDO getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<AdminUserDO> getUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return userMapper.selectByIds(ids);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<AdminUserDO> users = userMapper.selectByIds(ids);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(users, AdminUserDO::getId);
        ids.forEach(id -> {
            AdminUserDO user = userMap.get(id);
            if (user == null) {
                throw exception(USER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(user.getStatus())) {
                throw exception(USER_IS_DISABLE, user.getNickname());
            }
        });
    }

    @Override
    public List<AdminUserDO> getUserListByNickname(String nickname) {
        return userMapper.selectListByNickname(nickname);
    }

    private AdminUserDO validateUserForCreateOrUpdate(Long id, String username, String mobile, String email) {
        AdminUserDO user = validateUserExists(id);
        validateUsernameUnique(id, username);
        validateMobileUnique(id, mobile);
        validateEmailUnique(id, email);
        return user;
    }

    @VisibleForTesting
    AdminUserDO validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        AdminUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    @VisibleForTesting
    void validateUsernameUnique(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        AdminUserDO user = userMapper.selectByUsername(username);
        if (user == null || user.getId().equals(id)) {
            return;
        }
        throw exception(USER_USERNAME_EXISTS);
    }

    @VisibleForTesting
    void validateEmailUnique(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        AdminUserDO user = userMapper.selectByEmail(email);
        if (user == null || user.getId().equals(id)) {
            return;
        }
        throw exception(USER_EMAIL_EXISTS);
    }

    @VisibleForTesting
    void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        AdminUserDO user = userMapper.selectByMobile(mobile);
        if (user == null || user.getId().equals(id)) {
            return;
        }
        throw exception(USER_MOBILE_EXISTS);
    }

    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        AdminUserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isPasswordMatch(oldPassword, user.getPassword())) {
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
    }

    @Override
    public List<AdminUserDO> getUserListByStatus(Integer status) {
        return userMapper.selectListByStatus(status);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
