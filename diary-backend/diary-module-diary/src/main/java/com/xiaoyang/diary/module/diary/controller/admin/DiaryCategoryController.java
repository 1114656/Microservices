package com.xiaoyang.diary.module.diary.controller.admin;

import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategoryRespVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryCategorySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryCategoryDO;
import com.xiaoyang.diary.module.diary.service.DiaryCategoryService;
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

import java.util.List;

import static com.xiaoyang.diary.framework.common.pojo.CommonResult.success;
import static com.xiaoyang.diary.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - Diary category")
@RestController
@RequestMapping("/admin-api/diary/category")
@Validated
public class DiaryCategoryController {

    @Resource
    private DiaryCategoryService diaryCategoryService;

    @PostMapping("/create")
    @Operation(summary = "Create diary category")
    @PreAuthorize("@ss.hasPermission('diary:category:create')")
    public CommonResult<Long> createCategory(@Valid @RequestBody DiaryCategorySaveReqVO reqVO) {
        return success(diaryCategoryService.createCategory(reqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update diary category")
    @PreAuthorize("@ss.hasPermission('diary:category:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody DiaryCategorySaveReqVO reqVO) {
        diaryCategoryService.updateCategory(reqVO, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete diary category")
    @Parameter(name = "id", description = "Category ID", required = true)
    @PreAuthorize("@ss.hasPermission('diary:category:delete')")
    public CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id) {
        diaryCategoryService.deleteCategory(id, getLoginUserId());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "Get diary category")
    @Parameter(name = "id", description = "Category ID", required = true)
    public CommonResult<DiaryCategoryRespVO> getCategory(@RequestParam("id") Long id) {
        Long ownerUserId = getLoginUserId();
        DiaryCategoryDO category = diaryCategoryService.getCategory(id, ownerUserId);
        return success(diaryCategoryService.getCategoryList(ownerUserId).stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .orElseGet(() -> convert(category)));
    }

    @GetMapping("/list")
    @Operation(summary = "Get diary category list with status counts")
    public CommonResult<List<DiaryCategoryRespVO>> getCategoryList() {
        return success(diaryCategoryService.getCategoryList(getLoginUserId()));
    }

    private DiaryCategoryRespVO convert(DiaryCategoryDO category) {
        DiaryCategoryRespVO respVO = new DiaryCategoryRespVO();
        respVO.setId(category.getId());
        respVO.setName(category.getName());
        respVO.setSort(category.getSort());
        respVO.setStatus(category.getStatus());
        respVO.setDraftCount(0L);
        respVO.setPublishedCount(0L);
        respVO.setTotalCount(0L);
        respVO.setCreateTime(category.getCreateTime());
        respVO.setUpdateTime(category.getUpdateTime());
        return respVO;
    }
}
