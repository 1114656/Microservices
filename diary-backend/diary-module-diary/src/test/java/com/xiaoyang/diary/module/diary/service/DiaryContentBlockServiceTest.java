package com.xiaoyang.diary.module.diary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryContentBlockReqVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiaryContentBlockServiceTest {

    private final DiaryContentBlockService service = new DiaryContentBlockService(new ObjectMapper());

    @Test
    void shouldSerializeAndDeserializeBlocksInOriginalOrder() {
        List<DiaryContentBlockReqVO> blocks = List.of(
                DiaryContentBlockReqVO.text("start"),
                DiaryContentBlockReqVO.file(10L),
                DiaryContentBlockReqVO.text("middle"),
                DiaryContentBlockReqVO.file(20L)
        );

        String content = service.serialize(blocks);
        List<DiaryContentBlockReqVO> parsedBlocks = service.deserialize(content);

        assertEquals("text", parsedBlocks.get(0).getType());
        assertEquals("start", parsedBlocks.get(0).getContent());
        assertEquals("file", parsedBlocks.get(1).getType());
        assertEquals(10L, parsedBlocks.get(1).getFileId());
        assertEquals("text", parsedBlocks.get(2).getType());
        assertEquals("middle", parsedBlocks.get(2).getContent());
        assertEquals("file", parsedBlocks.get(3).getType());
        assertEquals(20L, parsedBlocks.get(3).getFileId());
    }

    @Test
    void shouldExtractFileReferencesWithSort() {
        List<DiaryContentBlockReqVO> blocks = List.of(
                DiaryContentBlockReqVO.text("start"),
                DiaryContentBlockReqVO.file(10L),
                DiaryContentBlockReqVO.text("middle"),
                DiaryContentBlockReqVO.file(20L)
        );

        List<DiaryContentBlockService.FileReference> references = service.extractFileReferences(blocks);

        assertEquals(2, references.size());
        assertEquals(10L, references.get(0).fileId());
        assertEquals(1, references.get(0).sort());
        assertEquals(20L, references.get(1).fileId());
        assertEquals(3, references.get(1).sort());
    }
}
