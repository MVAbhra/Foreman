package com.foreman.entities;

import java.time.LocalDate;

import com.foreman.enums.TaskPriority;
import com.foreman.enums.TaskStatus;

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
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private Long id;
	
	@Column(name = "title", length = 150, nullable = false)
	private String title;
	
	@Column(name = "description", length = 500)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;
	
	@ManyToOne
	@JoinColumn(name = "assignee_id")
	private User assignee;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "task_priority")
	private TaskPriority taskPriority;

	@Enumerated(EnumType.STRING)
	@Column(name = "task_status")
	private TaskStatus taskStatus;
	
	@Column(name = "created_on")
	private LocalDate createdOn;
	
	public Task(String title, String description, Project project, User assignee, TaskPriority taskPriority, TaskStatus taskStatus) {
		
		super();
		this.id = null;
		this.title = title;
		this.description = description;
		this.project = project;
		this.createdOn = LocalDate.now();
		this.assignee = assignee;
		this.taskPriority = taskPriority;
		this.taskStatus = taskStatus;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", description=" + description + ", project=" + project.getId()
				+ ", assignee=" + assignee.getId() + ", taskPriority=" + taskPriority.name() + ", taskStatus=" + taskStatus.name()
				+ ", createdOn=" + createdOn + "]";
	}
}
