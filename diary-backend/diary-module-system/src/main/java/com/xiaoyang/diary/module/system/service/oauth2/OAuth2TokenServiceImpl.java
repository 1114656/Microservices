package com.xiaoyang.diary.module.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.xiaoyang.diary.framework.common.enums.UserTypeEnum;
import com.xiaoyang.diary.framework.common.exception.ServiceException;
import com.xiaoyang.diary.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.xiaoyang.diary.framework.common.jwt.JwtTokenUtils;
import com.xiaoyang.diary.framework.common.jwt.JwtUserClaims;
import com.xiaoyang.diary.framework.common.util.date.DateUtils;
import com.xiaoyang.diary.framework.common.util.object.BeanUtils;
import com.xiaoyang.diary.framework.security.core.LoginUser;
import com.xiaoyang.diary.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.xiaoyang.diary.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.xiaoyang.diary.module.system.dal.dataobject.user.AdminUserDO;
import com.xiaoyang.diary.module.system.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import com.xiaoyang.diary.module.system.dal.mysql.oauth2.OAuth2RefreshTokenMapper;
import com.xiaoyang.diary.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.xiaoyang.diary.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.xiaoyang.diary.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static com.xiaoyang.diary.framework.common.util.collection.CollectionUtils.convertSet;

@Service
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    private static final long ACCESS_TOKEN_SECONDS = Duration.ofHours(2).toSeconds();
    private static final long REFRESH_TOKEN_SECONDS = Duration.ofDays(30).toSeconds();
    private static final String USER_INFO_KEY_USERNAME = "username";

    @Value("${diary.jwt.issuer:diary}")
    private String jwtIssuer;
    @Value("${diary.jwt.secret:diary-local-development-secret-change-me}")
    private String jwtSecret;
    @Value("${diary.jwt.access-token-seconds:7200}")
    private long accessTokenSeconds;
    @Value("${diary.jwt.refresh-token-seconds:2592000}")
    private long refreshTokenSeconds;

    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;
    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;
    @Resource
    @Lazy
    private AdminUserService adminUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2RefreshTokenDO refreshTokenDO = createRefreshToken(userId, userType, clientId, scopes);
        return createAccessToken(refreshTokenDO);
    }

    @Override
    @Transactional(noRollbackFor = ServiceException.class)
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }
        List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenMapper.selectListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            oauth2AccessTokenMapper.deleteByIds(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getId));
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getAccessToken));
            oauth2AccessTokenRedisDAO.blacklistList(accessTokenDOs);
        }
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        }
        return createAccessToken(refreshTokenDO);
    }

    @Override
    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        if (oauth2AccessTokenRedisDAO.isBlacklisted(accessToken)) {
            return null;
        }
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }
        accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }
        return accessTokenDO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }
        oauth2AccessTokenMapper.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        oauth2AccessTokenRedisDAO.blacklist(accessToken, accessTokenDO.getExpiresTime());
        oauth2RefreshTokenMapper.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    @Override
    public void removeAccessToken(Long userId, Integer userType) {
        List<OAuth2AccessTokenDO> accessTokens = oauth2AccessTokenMapper.selectListByUserIdAndUserType(userId, userType);
        if (CollUtil.isEmpty(accessTokens)) {
            return;
        }
        accessTokens.forEach(accessToken -> {
            oauth2AccessTokenMapper.deleteById(accessToken.getId());
            oauth2AccessTokenRedisDAO.delete(accessToken.getAccessToken());
            oauth2AccessTokenRedisDAO.blacklist(accessToken.getAccessToken(), accessToken.getExpiresTime());
            oauth2RefreshTokenMapper.deleteByRefreshToken(accessToken.getRefreshToken());
        });
    }

    private OAuth2AccessTokenDO createAccessToken(OAuth2RefreshTokenDO refreshTokenDO) {
        LocalDateTime expiresTime = LocalDateTime.now().plusSeconds(accessTokenSeconds).withNano(0);
        Map<String, String> userInfo = buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType());
        String accessToken = generateAccessToken(refreshTokenDO, userInfo, expiresTime);
        OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(accessToken)
                .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                .setUserInfo(userInfo)
                .setClientId(refreshTokenDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                .setRefreshToken(refreshTokenDO.getRefreshToken())
                .setExpiresTime(expiresTime);
        oauth2AccessTokenMapper.insert(accessTokenDO);
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    private OAuth2RefreshTokenDO createRefreshToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2RefreshTokenDO refreshToken = new OAuth2RefreshTokenDO().setRefreshToken(generateRefreshToken())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(refreshTokenSeconds).withNano(0));
        oauth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    private Map<String, String> buildUserInfo(Long userId, Integer userType) {
        if (userId == null || userId <= 0) {
            return Collections.emptyMap();
        }
        if (userType.equals(UserTypeEnum.ADMIN.getValue())) {
            AdminUserDO user = adminUserService.getUser(userId);
            return MapUtil.builder(LoginUser.INFO_KEY_NICKNAME, user.getNickname())
                    .put(USER_INFO_KEY_USERNAME, user.getUsername())
                    .build();
        }
        return Collections.emptyMap();
    }

    private String generateAccessToken(OAuth2RefreshTokenDO refreshTokenDO, Map<String, String> userInfo,
                                       LocalDateTime expiresTime) {
        return JwtTokenUtils.createToken(jwtIssuer, jwtSecret, JwtUserClaims.builder()
                .userId(refreshTokenDO.getUserId())
                .userType(refreshTokenDO.getUserType())
                .userInfo(userInfo)
                .scopes(refreshTokenDO.getScopes())
                .expiresTime(expiresTime)
                .build());
    }

    private static String generateRefreshToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

}
