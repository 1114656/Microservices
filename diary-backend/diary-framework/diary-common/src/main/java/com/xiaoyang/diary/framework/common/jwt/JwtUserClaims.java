package com.xiaoyang.diary.framework.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtUserClaims {

    private Long userId;

    private Integer userType;

    private Map<String, String> userInfo;

    private Long tenantId;

    private List<String> scopes;

    private LocalDateTime expiresTime;

}
