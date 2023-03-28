package com.ninja.spring.onetoone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ninja.spring.onetoone.model.TutorialDetails;

@Repository
public interface TutorialDetailsRepo extends JpaRepository<TutorialDetails, Long> {

	@Transactional
	void deleteById(long id);
	
	@Transactional
	void deleteByTutorialId(long tutorialId);
	
}
