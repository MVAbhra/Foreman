package com.foreman.entities;

import java.time.LocalDateTime;

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
@Table(name = "comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;
	
	@Column(name = "message", length = 500, nullable = false)
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "task_id", nullable = false)
	private Task task;
	
	@ManyToOne
	@JoinColumn(name = "commenter_id", nullable = false)
	private User user;

	@Column(name = "created_on", nullable = false)
	private LocalDateTime createdOn;

	
	public Comment(String message, Task task, User user) {
	
		super();
		this.id = null;
		this.message = message;
		this.task = task;
		this.user = user;
		this.createdOn = LocalDateTime.now();
	}


	@Override
	public String toString() {
		return "Comment [id=" + id + ", message=" + message + ", task=" + task.getId() + ", user=" + user.getId()
				+ ", createdOn=" + createdOn + "]";
	}	
}
