package com.xiaoyang.diary.module.system.service.oauth2;

import com.xiaoyang.diary.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;

import java.util.List;

public interface OAuth2TokenService {

    OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes);

    OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId);

    OAuth2AccessTokenDO getAccessToken(String accessToken);

    OAuth2AccessTokenDO checkAccessToken(String accessToken);

    OAuth2AccessTokenDO removeAccessToken(String accessToken);

    void removeAccessToken(Long userId, Integer userType);

}
