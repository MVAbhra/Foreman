package com.foreman.dtos;

import java.time.LocalDateTime;

import com.foreman.enums.TaskPriority;
import com.foreman.enums.TaskStatus;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaskCreAndUpDto {

	@NotBlank(message = "Title cannot be blank")
	@Size(min = 1, max = 50, message = "Title must be between 1 to 50 characters")
	private String title;
	
	@NotBlank(message = "Please provide a meaningful description")
	@Size(min = 1, max = 200, message = "Description must be between 1 to 200 characters")
	private String description;
	
	@NotNull(message = "Priority is required")
	private TaskPriority priority;
	
	@NotNull(message = "Due date is required")
	@FutureOrPresent(message = "Due date must be today or in the future")
	private LocalDateTime dueDate;
	
	@NotNull(message = "Status is required")
	private TaskStatus status;
	
	@NotNull(message = "ID is required")
	@Positive(message = "ID must be a valid number")
	private Long userId;	
}
