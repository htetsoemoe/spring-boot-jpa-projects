package com.ninja.spring.manytoone.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ninja.spring.manytoone.model.Tutorial;
import com.ninja.spring.manytoone.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class TutorialController {
	
	@Autowired
	private TutorialRepository tutorialRepo;
	
	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial, BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			Tutorial createdTutorial = tutorialRepo.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), tutorial.isPublished()));
			return new ResponseEntity<>(createdTutorial, HttpStatus.CREATED);
		}
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data %s", getErrors(bindingResult)));
	}

	private String getErrors(BindingResult bindingResult) {
		return bindingResult.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(", "));
	}
	
	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorial(@RequestParam(required = false) String title) {
		List<Tutorial> tutorials = new ArrayList<Tutorial>();
		
		if (title == null) {
			tutorialRepo.findAll().forEach(tutorials::add);
		} else {
			tutorialRepo.findByTitleContaining(title).forEach(tutorials::add);
		}
		
		// If there is no record in database
		if (tutorials.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Tutorial>>(tutorials, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable Long id) {
		Tutorial tutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Not found tutorial with ID %d", id)));
					
		return new ResponseEntity<Tutorial>(tutorial, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> getPublishedTutorial() {
		List<Tutorial> publishedTutorials = tutorialRepo.findByPublished(true);
		
		if (publishedTutorials.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(publishedTutorials, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Tutorial>>(publishedTutorials, HttpStatus.OK);
	}
	
	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorialWithId(@PathVariable long id, @RequestBody Tutorial tutorial) {
		Tutorial foundTutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Not found tutorial with ID %d", id)));
		
		foundTutorial.setTitle(tutorial.getTitle());
		foundTutorial.setDescription(tutorial.getDescription());
		foundTutorial.setPublished(tutorial.isPublished());
		
		return new ResponseEntity<Tutorial>(tutorialRepo.save(foundTutorial), HttpStatus.OK);
	}
	
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<HttpStatus> deleteTutorialById(@PathVariable long id) {
		if (tutorialRepo.existsById(id)) {
			tutorialRepo.deleteById(id);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}
		
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		tutorialRepo.deleteAll();
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}

}
