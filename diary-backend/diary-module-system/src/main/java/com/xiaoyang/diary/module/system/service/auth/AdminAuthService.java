package com.xiaoyang.diary.module.system.service.auth;

import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.xiaoyang.diary.module.system.controller.admin.auth.vo.AuthRegisterReqVO;
import com.xiaoyang.diary.module.system.dal.dataobject.user.AdminUserDO;
import jakarta.validation.Valid;

public interface AdminAuthService {

    AdminUserDO authenticate(String username, String password);

    AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO);

    void logout(String token, Integer logType);

    AuthLoginRespVO refreshToken(String refreshToken);

    AuthLoginRespVO register(AuthRegisterReqVO createReqVO);

}
