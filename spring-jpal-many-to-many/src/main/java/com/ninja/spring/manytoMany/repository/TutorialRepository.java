package com.ninja.spring.manytoMany.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninja.spring.manytoMany.model.Tutorial;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long>{
	
	List<Tutorial> findTutorialsByTagsId(Long tagId);

}
