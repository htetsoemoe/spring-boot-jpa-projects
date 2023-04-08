package com.ninja.spring.manytoone.controller;

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
import org.springframework.web.server.ResponseStatusException;

import com.ninja.spring.manytoone.exception.ResourceNotFoundException;
import com.ninja.spring.manytoone.model.Comment;
import com.ninja.spring.manytoone.repository.CommentRepository;
import com.ninja.spring.manytoone.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class TutorialCommentController {
	
	@Autowired
	private TutorialRepository tutorialRepo;
	@Autowired
	private CommentRepository commentRepo;
	
	
	@PostMapping("/tutorials/{tutorialId}/comments")
	public ResponseEntity<Comment> createComment(@PathVariable long tutorialId, @RequestBody Comment commentRequest) {
		Comment comment = tutorialRepo.findById(tutorialId).map(tutorial -> {
			commentRequest.setTutorial(tutorial);
			return commentRepo.save(commentRequest);
		}).orElseThrow(() -> new ResourceNotFoundException(String.format("Not found Tutorial with ID : %d", tutorialId)));
			
		return new ResponseEntity<Comment>(comment, HttpStatus.CREATED);
	}
	
	@GetMapping("/tutorials/{tutorialId}/comments")
	public ResponseEntity<List<Comment>> getAllCommentsByTutorialId(@PathVariable long tutorialId) {
		if (!tutorialRepo.existsById(tutorialId)) {
			throw new ResourceNotFoundException(String.format("Not found Tutorial with ID : %d", tutorialId));
		}
		
		List<Comment> comments = commentRepo.findByTutorialId(tutorialId);		
		return new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
	}
	
	@GetMapping("/comments/{id}")
	public ResponseEntity<Comment> getCommentById(@PathVariable long id) {
		Comment comment = commentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found comment with ID : %d", id)));
		
		return new ResponseEntity<Comment>(comment, HttpStatus.OK);
	}
	
	@PutMapping("/comments/{id}")
	public ResponseEntity<Comment> updateComment(@PathVariable long id, @RequestBody Comment commentRequest) {
		Comment comment = commentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found comment with ID : %d", id)));
		
		comment.setContent(commentRequest.getContent());
		return new ResponseEntity<Comment>(commentRepo.save(comment), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/tutorials/{tutorialId}/comments")
	public ResponseEntity<HttpStatus> deleteAllCommentsOfTutorial(@PathVariable long tutorialId) {
		if (!tutorialRepo.existsById(tutorialId)) {
			throw new ResourceNotFoundException(String.format("Not found comment with ID : %d", tutorialId));
		}
		
		commentRepo.deleteByTutorialId(tutorialId);
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/comments/{id}")
	public ResponseEntity<HttpStatus> deleteComment(@PathVariable long id) {
		if (!commentRepo.existsById(id)) {
			throw new ResourceNotFoundException(String.format("Not found comment with ID : %d", id));
		}
		
		commentRepo.deleteById(id);
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}

}
