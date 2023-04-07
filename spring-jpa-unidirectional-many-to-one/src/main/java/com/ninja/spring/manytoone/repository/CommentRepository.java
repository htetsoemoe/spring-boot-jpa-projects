package com.ninja.spring.manytoone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninja.spring.manytoone.model.Comment;

import jakarta.transaction.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
	
	List<Comment> findByTutorialId(Long id);
	
	@Transactional
	void deleteByTutorialId(Long tutorialId);

}
