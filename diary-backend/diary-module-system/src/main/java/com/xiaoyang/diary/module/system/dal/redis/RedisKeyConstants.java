package com.xiaoyang.diary.module.system.dal.redis;

import com.xiaoyang.diary.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;

/**
 * System Redis key constants.
 */
public interface RedisKeyConstants {

    String ROLE = "role";

    String USER_ROLE_ID_LIST = "user_role_ids";

    String MENU_ROLE_ID_LIST = "menu_role_ids";

    String PERMISSION_MENU_ID_LIST = "permission_menu_ids";

    /**
     * Access token cache, backed by dynamic RedisTemplate expiry.
     *
     * @see OAuth2AccessTokenDO
     */
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";

    String JWT_ACCESS_TOKEN_BLACKLIST = "jwt:blacklist:%s";

}
