package com.foreman.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foreman.dtos.TaskDisplayResponseDto;
import com.foreman.entities.Task;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

	@Query("""
			SELECT new com.foreman.dtos.TaskDisplayResponseDto(
				t.id, t.title, t.description, 
				t.createdOn, t.priority, t.dueDate, t.status, 
				t.project.id, t.user.id, t.project.workspace.id
			)
			FROM Task t
			WHERE t.project.id = :projId
			""")
	List<TaskDisplayResponseDto> getAllTasksInProject(Long projId);

	
	Optional<Task> findByIdAndProject_Id(Long taskId, Long projId);
}
