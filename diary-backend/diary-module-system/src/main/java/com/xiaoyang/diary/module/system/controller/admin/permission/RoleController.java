package com.xiaoyang.diary.module.system.controller.admin.permission;

import com.xiaoyang.diary.framework.common.enums.CommonStatusEnum;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.framework.common.util.object.BeanUtils;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.xiaoyang.diary.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.xiaoyang.diary.module.system.dal.dataobject.permission.RoleDO;
import com.xiaoyang.diary.module.system.service.permission.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;
import static java.util.Collections.singleton;

@Tag(name = "管理后台 - 角色")
@RestController
@RequestMapping("/admin-api/system/role")
@Validated
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    @Operation(summary = "创建角色")
    @PreAuthorize("@ss.hasPermission('system:role:create')")
    public CommonResult<Long> createRole(@Valid @RequestBody RoleSaveReqVO createReqVO) {
        return success(roleService.createRole(createReqVO, null));
    }

    @PutMapping("/update")
    @Operation(summary = "修改角色")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public CommonResult<Boolean> updateRole(@Valid @RequestBody RoleSaveReqVO updateReqVO) {
        roleService.updateRole(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "角色编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRole(@RequestParam("id") Long id) {
        roleService.deleteRole(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除角色")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRoleList(@RequestParam("ids") List<Long> ids) {
        roleService.deleteRoleList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<RoleRespVO> getRole(@RequestParam("id") Long id) {
        RoleDO role = roleService.getRole(id);
        return success(BeanUtils.toBean(role, RoleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得角色分页")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<PageResult<RoleRespVO>> getRolePage(RolePageReqVO pageReqVO) {
        PageResult<RoleDO> pageResult = roleService.getRolePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RoleRespVO.class));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取角色精简信息列表")
    public CommonResult<List<RoleRespVO>> getSimpleRoleList() {
        List<RoleDO> list = roleService.getRoleListByStatus(singleton(CommonStatusEnum.ENABLE.getStatus()));
        list.sort(Comparator.comparing(RoleDO::getSort));
        return success(BeanUtils.toBean(list, RoleRespVO.class));
    }

}
