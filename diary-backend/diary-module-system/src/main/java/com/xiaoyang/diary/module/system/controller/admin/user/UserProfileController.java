package com.xiaoyang.diary.module.system.controller.admin.user;

import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.xiaoyang.diary.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.xiaoyang.diary.module.system.convert.user.UserConvert;
import com.xiaoyang.diary.module.system.dal.dataobject.permission.RoleDO;
import com.xiaoyang.diary.module.system.dal.dataobject.user.AdminUserDO;
import com.xiaoyang.diary.module.system.service.permission.PermissionService;
import com.xiaoyang.diary.module.system.service.permission.RoleService;
import com.xiaoyang.diary.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;
import static com.xiaoyang.diary.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 用户个人中心")
@RestController
@RequestMapping("/system/user/profile")
@Validated
public class UserProfileController {

    @Resource
    private AdminUserService userService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @GetMapping("/get")
    @Operation(summary = "获得登录用户信息")
    public CommonResult<UserProfileRespVO> getUserProfile() {
        AdminUserDO user = userService.getUser(getLoginUserId());
        List<RoleDO> userRoles = roleService.getRoleListFromCache(permissionService.getUserRoleIdListByUserId(user.getId()));
        return success(UserConvert.INSTANCE.convert(user, userRoles));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户个人信息")
    public CommonResult<Boolean> updateUserProfile(@Valid @RequestBody UserProfileUpdateReqVO reqVO) {
        userService.updateUserProfile(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户个人密码")
    public CommonResult<Boolean> updateUserProfilePassword(@Valid @RequestBody UserProfileUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(getLoginUserId(), reqVO);
        return success(true);
    }

}
