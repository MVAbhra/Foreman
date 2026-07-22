package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foreman.entities.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long> {

}
