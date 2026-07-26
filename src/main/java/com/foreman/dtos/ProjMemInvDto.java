package com.foreman.dtos;

import com.foreman.enums.ProjectRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjMemInvDto {

	@NotBlank(message = "Email is required")
	@Email(message = "Not a validd email")
	private String email;
	
	@NotNull(message = "Project role is required")
	private ProjectRole projectRole;
}
