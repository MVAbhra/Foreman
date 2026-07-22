package com.foreman.entities;

import java.time.LocalDate;

import com.foreman.enums.WorkspaceRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "workspace_membership")
public class WorkspaceMembership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "membership_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "workspace_id")
	private Workspace workspace;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private User user;
	
	@Column(name = "joined_on", nullable = false)
	private LocalDate joinedOn;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "workspace_role", nullable = false)
	private WorkspaceRole workspaceRole;

	
	public WorkspaceMembership(Workspace workspace, User user, WorkspaceRole workspaceRole) {
		
		super();
		this.id = null;
		this.workspace = workspace;
		this.user = user;
		this.joinedOn = LocalDate.now();
		this.workspaceRole = workspaceRole;
	}


	@Override
	public String toString() {
		return "WorkspaceMembership [id=" + id + ", workspace=" + workspace.getId() + ", user=" + user.getId() + ", joinedOn="
				+ joinedOn + ", role=" + workspaceRole.name() + "]";
	}	
}
