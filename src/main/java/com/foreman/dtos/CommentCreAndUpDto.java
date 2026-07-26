package com.foreman.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentCreAndUpDto {
	
	@NotBlank(message = "Comment cannot be empty or blank")
	@Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
    private String message;
}