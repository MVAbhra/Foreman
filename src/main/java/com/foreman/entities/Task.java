package com.foreman.entities;

import java.time.LocalDateTime;

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
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;
	
	@ManyToOne
	@JoinColumn(name = "assignee_id", nullable = true)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "task_priority", nullable = false)
	private TaskPriority priority;

	@Enumerated(EnumType.STRING)
	@Column(name = "task_status", nullable = false)
	private TaskStatus status;
	
	@Column(name = "created_on", nullable = false)
	private LocalDateTime createdOn;
	
	@Column(name = "due_date", nullable = false)
	private LocalDateTime dueDate;
	
	public Task(String title, String description, Project project, User assignee, TaskPriority priority, TaskStatus status, LocalDateTime dueDate) {
		
		super();
		this.id = null;
		this.title = title;
		this.description = description;
		this.project = project;
		this.user = assignee;
		this.priority = priority;
		this.status = status;
		this.createdOn = LocalDateTime.now();
		this.dueDate = dueDate;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", description=" + description + ", project=" + project.getId()
				+ ", user=" + user.getId() + ", priority=" + priority.name() + ", status=" + status.name()
				+ ", createdOn=" + createdOn + ", dueDate=" + dueDate + "]";
	}
}
