package com.ninja.spring.onetomany.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ninja.spring.onetomany.exception.ResourceNotFoundException;
import com.ninja.spring.onetomany.model.Remark;
import com.ninja.spring.onetomany.repository.CommentRepository;
import com.ninja.spring.onetomany.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class TutorialCommentController {

	@Autowired
	private TutorialRepository tutorialRepo;
	@Autowired
	private CommentRepository commentRepo;

	@PostMapping("/tutorials/{tutorialId}/comments")
	public ResponseEntity<Remark> createComment(@PathVariable long tutorialId, @RequestBody Remark commentRequest) {

		Remark comment = tutorialRepo.findById(tutorialId).map(tutorial -> {
			tutorial.getComments().add(commentRequest);
			return commentRepo.save(commentRequest);
		}).orElseThrow(() -> new ResourceNotFoundException("Not found tutorial with ID %d".formatted(tutorialId)));

		return new ResponseEntity<Remark>(comment, HttpStatus.CREATED);
	}

}
