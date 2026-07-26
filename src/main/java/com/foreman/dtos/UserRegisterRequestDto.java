package com.foreman.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class UserRegisterRequestDto {

	@NotBlank(message = "First name cannot be empty or blank")
	@Size(min = 1, max = 20, message = "First name must be between 1 to 20 characters")
	private String firstName;
	
	@NotBlank(message = "Last name cannot be empty or blank")
	@Size(min = 1, max = 20, message = "Last name must be between 1 to 20 characters")
	private String lastName;
	
	@Email(message = "Must be a valid email address")
	@NotBlank(message = "Email cannot be empty or blank")
	@Size(max = 254, message = "Email address cannot exceed 254 characters")
	private String email;
	
	@NotBlank
	private String password;
}
