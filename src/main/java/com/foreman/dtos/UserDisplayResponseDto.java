package com.foreman.dtos;

import com.foreman.enums.ProjectRole;
import com.foreman.enums.WorkspaceRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserDisplayResponseDto {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private Long workspaceId;
	private WorkspaceRole workspaceRole;
	private Long projectId;
	private ProjectRole projectRole;
}
