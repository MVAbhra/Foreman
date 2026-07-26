package com.foreman.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkspaceCreationAndUpdationDto {

	@NotBlank(message = "Workspace name is required")
	@Size(min = 1, max = 50, message = "Workspace name should be between 1 to 50 characters")
	private String name;
//	private Long ownerId;
}
