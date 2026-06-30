package com.xiaoyang.diary.module.diary.controller.admin;

import cn.hutool.core.collection.CollUtil;
import com.xiaoyang.diary.framework.common.pojo.CommonResult;
import com.xiaoyang.diary.framework.common.pojo.PageResult;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryContentBlockReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryPageReqVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryRespVO;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiarySaveReqVO;
import com.xiaoyang.diary.module.diary.dal.dataobject.DiaryEntryDO;
import com.xiaoyang.diary.module.diary.service.DiaryContentBlockService;
import com.xiaoyang.diary.module.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
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

@Tag(name = "管理后台 - 日记")
@RestController
@RequestMapping("/diary")
@Validated
public class DiaryController {

    @Resource
    private DiaryService diaryService;
    @Resource
    private DiaryContentBlockService contentBlockService;

    @PostMapping("/create")
    @Operation(summary = "创建日记")
    @PreAuthorize("@ss.hasPermission('diary:entry:create')")
    public CommonResult<Long> createDiary(@Valid @RequestBody DiarySaveReqVO reqVO) {
        return success(diaryService.createDiary(reqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新日记")
    @PreAuthorize("@ss.hasPermission('diary:entry:update')")
    public CommonResult<Boolean> updateDiary(@Valid @RequestBody DiarySaveReqVO reqVO) {
        diaryService.updateDiary(reqVO, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除日记")
    @Parameter(name = "id", description = "日记编号", required = true)
    @PreAuthorize("@ss.hasPermission('diary:entry:delete')")
    public CommonResult<Boolean> deleteDiary(@RequestParam("id") Long id) {
        diaryService.deleteDiary(id, getLoginUserId());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得日记详情")
    @Parameter(name = "id", description = "日记编号", required = true)
    public CommonResult<DiaryRespVO> getDiary(@RequestParam("id") Long id) {
        return success(convert(diaryService.getDiary(id, getLoginUserId())));
    }

    @GetMapping("/page")
    @Operation(summary = "获得日记分页")
    @PermitAll
    public CommonResult<PageResult<DiaryRespVO>> getDiaryPage(@Valid DiaryPageReqVO pageReqVO) {
        PageResult<DiaryEntryDO> pageResult = diaryService.getDiaryPage(pageReqVO, getLoginUserId());
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        List<DiaryRespVO> list = pageResult.getList().stream().map(this::convert).toList();
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    private DiaryRespVO convert(DiaryEntryDO diary) {
        List<DiaryContentBlockReqVO> blocks = contentBlockService.deserialize(diary.getContent());
        DiaryRespVO respVO = new DiaryRespVO();
        respVO.setId(diary.getId());
        respVO.setTitle(diary.getTitle());
        respVO.setCategoryId(diary.getCategoryId());
        respVO.setContentBlocks(blocks);
        respVO.setSummary(diary.getSummary());
        respVO.setStatus(diary.getStatus());
        respVO.setVisibility(diary.getVisibility());
        respVO.setFileIds(blocks.stream()
                .filter(block -> DiaryContentBlockReqVO.TYPE_FILE.equals(block.getType()))
                .map(DiaryContentBlockReqVO::getFileId)
                .toList());
        respVO.setCreateTime(diary.getCreateTime());
        respVO.setUpdateTime(diary.getUpdateTime());
        return respVO;
    }
}
