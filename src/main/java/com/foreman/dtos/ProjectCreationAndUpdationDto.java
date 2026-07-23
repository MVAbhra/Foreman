package com.foreman.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectCreationAndUpdationDto {

	private String title;
	private String description;
	
	private Long managerId;
}
