package com.ninja.spring.onetoone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninja.spring.onetoone.model.Tutorial;

@Repository
public interface TutorialRepo extends JpaRepository<Tutorial, Long> {
	
	List<Tutorial> findByPublished(boolean published);
	
	List<Tutorial> findByTitleContaining(String title);

}
