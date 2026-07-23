package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foreman.entities.Comment;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

}
