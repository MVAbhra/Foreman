package com.foreman.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class UserLoginRequestDto {

	@Email(message = "Must be a valid email address")
	@NotBlank(message = "Email cannot be empty or blank")
	@Size(max = 254, message = "Email address cannot exceed 254 characters")
	private String email;
	
	@NotBlank
	private String password;
}
