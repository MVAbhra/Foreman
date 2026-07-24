package com.foreman.dtos;

import java.time.LocalDateTime;

import com.foreman.enums.TaskPriority;
import com.foreman.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class TaskDisplayResponseDto {

	private Long id;
	private String title;
	private String description;
	private LocalDateTime createdOn;
	private TaskPriority priority;
	private LocalDateTime dueDate;
	private TaskStatus status;
	
	private Long projectId;
	private Long userId;
	private Long workspaceId;
}
