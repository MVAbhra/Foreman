package com.foreman.entities;

import java.time.LocalDate;

import com.foreman.enums.ProjectRole;

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
@Table(name = "project_membership")
public class ProjectMembership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "membership_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "project_role", nullable = false)
	private ProjectRole projectRole;
	
	@Column(name = "joined_on", nullable = false)
	private LocalDate joinedOn;

	
	public ProjectMembership(Project project, User user, ProjectRole projectRole) {
		
		super();
		this.id = null;
		this.project = project;
		this.user = user;
		this.projectRole = projectRole;
		this.joinedOn = LocalDate.now();
	}


	@Override
	public String toString() {
		return "ProjectMembership [id=" + id + ", project=" + project.getId() + ", user=" + user.getId() + ", projectRole="
				+ projectRole.name() + ", joinedOn=" + joinedOn + "]";
	}
}
