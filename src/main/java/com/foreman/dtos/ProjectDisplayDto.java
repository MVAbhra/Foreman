package com.foreman.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class ProjectDisplayDto {

	private Long id;
	private String title;
	private String description;
	
	private Long workspaceId;
	private String workspaceName;
	
//	private Long managerId;
}
