package com.foreman.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class CommentDisplayResponseDto {

    private Long id;
    private String message;
    private LocalDateTime createdOn;

    private Long taskId;
    private Long userId;
    private Long projectId;
    private Long workspaceId;
}
