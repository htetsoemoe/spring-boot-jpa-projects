package com.ninja.spring.manytoMany.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninja.spring.manytoMany.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
	
	List<Tag> findTagsByTutorialsId(Long tutorialId);

}
