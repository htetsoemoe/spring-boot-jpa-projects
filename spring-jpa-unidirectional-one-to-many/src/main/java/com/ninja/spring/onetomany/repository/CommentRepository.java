package com.ninja.spring.onetomany.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninja.spring.onetomany.model.Remark;

@Repository
public interface CommentRepository extends JpaRepository<Remark, Long>{

}
