package com.xiaoyang.diary.module.system.dal.redis.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.xiaoyang.diary.framework.common.util.collection.CollectionUtils;
import com.xiaoyang.diary.framework.common.util.json.JsonUtils;
import com.xiaoyang.diary.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.xiaoyang.diary.module.system.dal.redis.RedisKeyConstants.OAUTH2_ACCESS_TOKEN;
import static com.xiaoyang.diary.module.system.dal.redis.RedisKeyConstants.JWT_ACCESS_TOKEN_BLACKLIST;

/**
 * {@link OAuth2AccessTokenDO} 的 RedisDAO
 *
 * @author 小杨
 */
@Repository
public class OAuth2AccessTokenRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public OAuth2AccessTokenDO get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), OAuth2AccessTokenDO.class);
    }

    public void set(OAuth2AccessTokenDO accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessToken());
        // 清理多余字段，避免缓存
        accessTokenDO.setUpdater(null).setUpdateTime(null).setCreateTime(null).setCreator(null).setDeleted(null);
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), accessTokenDO.getExpiresTime(), ChronoUnit.SECONDS);
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(accessTokenDO), time, TimeUnit.SECONDS);
        }
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.delete(redisKey);
    }

    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollectionUtils.convertList(accessTokens, OAuth2AccessTokenRedisDAO::formatKey);
        stringRedisTemplate.delete(redisKeys);
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(formatBlacklistKey(accessToken)));
    }

    public void blacklist(String accessToken, LocalDateTime expiresTime) {
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), expiresTime, ChronoUnit.SECONDS);
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(formatBlacklistKey(accessToken), "1", time, TimeUnit.SECONDS);
        }
    }

    public void blacklistList(Collection<OAuth2AccessTokenDO> accessTokens) {
        if (accessTokens == null) {
            return;
        }
        accessTokens.forEach(accessToken -> blacklist(accessToken.getAccessToken(), accessToken.getExpiresTime()));
    }

    private static String formatKey(String accessToken) {
        return String.format(OAUTH2_ACCESS_TOKEN, accessToken);
    }

    private static String formatBlacklistKey(String accessToken) {
        return String.format(JWT_ACCESS_TOKEN_BLACKLIST, accessToken);
    }

}
