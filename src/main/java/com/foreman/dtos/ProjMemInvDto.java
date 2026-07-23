package com.foreman.dtos;

import com.foreman.enums.ProjectRole;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjMemInvDto {

	private Long userId;
	private ProjectRole projectRole;
}
