package com.foreman.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "workspaces")
public class Workspace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "workspace_id")
	private Long id;
	
	@Column(name = "workspace_name", length = 100, nullable = false)
	private String name;
	
	@Column(name = "created_on")
	private LocalDate createdOn;
	
	
	public Workspace(String name) {

		super();
		this.id = null;
		this.name = name;
		this.createdOn = LocalDate.now();
	}


	@Override
	public String toString() {
		return "Workspace [id=" + id + ", name=" + name + ", createdOn=" + createdOn + "]";
	}
}
