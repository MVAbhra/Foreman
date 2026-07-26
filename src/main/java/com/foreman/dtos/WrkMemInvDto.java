package com.foreman.dtos;

import com.foreman.enums.WorkspaceRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WrkMemInvDto {

	@NotBlank(message = "Email required")
	@Email(message = "Email not valid")
	private String email;
	
	@NotNull(message = "Workspace role required")
	private WorkspaceRole workspaceRole;
}
