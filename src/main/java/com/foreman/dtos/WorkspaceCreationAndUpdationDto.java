package com.foreman.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkspaceCreationAndUpdationDto {

	private String name;
	private Long ownerId;
}
