package com.foreman.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "projects")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long id;
	
	@Column(name = "title", length = 150, nullable = false)
	private String title;
	
	@Column(name = "description", length = 500)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "workspace_id")
	private Workspace workspace;
	
	@Column(name = "created_on")
	private LocalDate createdOn;

	
	public Project(String title, String description, Workspace workspace) {
		
		super();
		this.id = null;
		this.title = title;
		this.description = description;
		this.workspace = workspace;
		this.createdOn = LocalDate.now();
	}


	@Override
	public String toString() {
		return "Project [id=" + id + ", title=" + title + ", description=" + description + ", workspace="
				+ workspace.getId() + ", createdOn=" + createdOn + "]";
	}
}
