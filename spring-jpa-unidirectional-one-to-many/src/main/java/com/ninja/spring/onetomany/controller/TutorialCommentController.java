package com.ninja.spring.onetomany.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ninja.spring.onetomany.exception.ResourceNotFoundException;
import com.ninja.spring.onetomany.model.Remark;
import com.ninja.spring.onetomany.model.Tutorial;
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

	@GetMapping("/tutorials/{tutorialId}/comments")
	public ResponseEntity<List<Remark>> getAllTutorialComments(@PathVariable long tutorialId) {
		Tutorial tutorial = tutorialRepo.findById(tutorialId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found tutorial with ID %d".formatted(tutorialId)));
		
		List<Remark> comments = new ArrayList<Remark>();
		comments.addAll(tutorial.getComments());
		
		return new ResponseEntity<>(comments, HttpStatus.OK);
	}
	
	@GetMapping("/comments/{id}")
	public ResponseEntity<Remark> getCommentsByTutorialId(@PathVariable long id) {
		
		Remark comment = commentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found tutorial with ID %s".formatted(id)));
		
		return new ResponseEntity<>(comment, HttpStatus.OK);
	}
	
	@PutMapping("/comments/{id}")
	public ResponseEntity<Remark> updateComment(@PathVariable long id, @RequestBody Remark commentRequest) {
		Remark comment = commentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment ID " + id + "not found."));
		
		comment.setContent(commentRequest.getContent());
		
		return new ResponseEntity<>(commentRepo.save(comment), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/tutorials/{tutorialId}/comments")
	public ResponseEntity<List<Remark>> deleteAllCommentOfTutorial(@PathVariable long tutorialId) {
		Tutorial tutorial = tutorialRepo.findById(tutorialId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found tutorial with ID %d".formatted(tutorialId)));

		tutorial.getComments().removeAll(tutorial.getComments());
		tutorialRepo.save(tutorial);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/comments/{id}")
	public ResponseEntity<HttpStatus> deleteComment(@PathVariable long id) {
		if (commentRepo.existsById(id)) {
			commentRepo.deleteById(id);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}
		
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}
}
