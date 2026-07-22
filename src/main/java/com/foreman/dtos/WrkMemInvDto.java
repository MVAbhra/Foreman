package com.foreman.dtos;

import com.foreman.enums.WorkspaceRole;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WrkMemInvDto {

	private Long userId;
	private WorkspaceRole workspaceRole;
}
