package com.foreman.dtos;

import java.time.LocalDateTime;

import com.foreman.enums.TaskPriority;
import com.foreman.enums.TaskStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskCreAndUpDto {

	private String title;
	private String description;
	private TaskPriority priority;
	private LocalDateTime dueDate;
	private TaskStatus status;
	
	private Long userId;	
}
