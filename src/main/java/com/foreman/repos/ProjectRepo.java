package com.foreman.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foreman.entities.Project;

public interface ProjectRepo extends JpaRepository<Project, Long> {

	List<Project> findByWorkspace_Id(Long wrkspcId);

	Optional<Project> findByIdAndWorkspace_Id(Long projId, Long wrkspcId);

}
