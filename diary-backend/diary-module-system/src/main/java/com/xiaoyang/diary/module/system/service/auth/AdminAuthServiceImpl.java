package com.xiaoyang.diary.module.system.service.auth;

import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.common.enums.UserTypeEnum;
import com.xiaoyang.diary.framework.common.util.object.BeanUtils;
import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthRegisterReqVO;
import com.xiaoyang.diary.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.xiaoyang.diary.module.system.dal.dataobject.user.AdminUserDO;
import com.xiaoyang.diary.module.system.enums.logger.LoginLogTypeEnum;
import com.xiaoyang.diary.module.system.enums.oauth2.OAuth2ClientConstants;
import com.xiaoyang.diary.module.system.service.oauth2.OAuth2TokenService;
import com.xiaoyang.diary.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;
import static com.xiaoyang.diary.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_USER_DISABLED;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private AdminUserService userService;
    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Override
    public AdminUserDO authenticate(String username, String password) {
        AdminUserDO user = userService.getUserByUsername(username);
        if (user == null || !userService.isPasswordMatch(password, user.getPassword())) {
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(user.getId(), getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        userService.updateUserLogin(user.getId(), null);
        return BeanUtils.toBean(accessTokenDO, AuthLoginRespVO.class);
    }

    @Override
    public AuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken,
                OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return BeanUtils.toBean(accessTokenDO, AuthLoginRespVO.class);
    }

    @Override
    public void logout(String token, Integer logType) {
        oauth2TokenService.removeAccessToken(token);
    }

    @Override
    public AuthLoginRespVO register(AuthRegisterReqVO registerReqVO) {
        Long userId = userService.registerUser(registerReqVO);
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        return BeanUtils.toBean(accessTokenDO, AuthLoginRespVO.class);
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.ADMIN;
    }

}
