package com.foreman.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectCreationAndUpdationDto {

	@NotBlank(message = "Project title cannot be blank")
	@Size(min = 1, max = 50, message = "Title must be between 1 to 50 characters")
	private String title;
	
	@NotBlank(message = "Please provide a meaningful description")
	@Size(min = 1, max = 200, message = "Description must be between 1 to 200 characters")
	private String description;
	
//	private Long managerId;
}
