package com.xiaoyang.diary.module.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryContentBlockReqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.xiaoyang.diary.module.diary.controller.admin.vo.DiaryContentBlockReqVO.TYPE_FILE;

@Service
@RequiredArgsConstructor
public class DiaryContentBlockService {

    private static final TypeReference<List<DiaryContentBlockReqVO>> CONTENT_BLOCK_TYPE =
            new TypeReference<>() {
            };

    private final ObjectMapper objectMapper;

    public String serialize(List<DiaryContentBlockReqVO> blocks) {
        try {
            return objectMapper.writeValueAsString(blocks == null ? List.of() : blocks);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Serialize diary content blocks failed", e);
        }
    }

    public List<DiaryContentBlockReqVO> deserialize(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(content, CONTENT_BLOCK_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Deserialize diary content blocks failed", e);
        }
    }

    public List<FileReference> extractFileReferences(List<DiaryContentBlockReqVO> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return List.of();
        }
        List<FileReference> references = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            DiaryContentBlockReqVO block = blocks.get(i);
            if (block != null && TYPE_FILE.equals(block.getType()) && block.getFileId() != null) {
                references.add(new FileReference(block.getFileId(), i));
            }
        }
        return references;
    }

    public record FileReference(Long fileId, Integer sort) {
    }
}
