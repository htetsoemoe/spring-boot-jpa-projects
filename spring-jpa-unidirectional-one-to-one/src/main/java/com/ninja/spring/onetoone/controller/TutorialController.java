package com.ninja.spring.onetoone.controller;

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

import com.ninja.spring.onetoone.exception.ResourceNotFoundException;
import com.ninja.spring.onetoone.model.Tutorial;
import com.ninja.spring.onetoone.repository.TutorialDetailsRepo;
import com.ninja.spring.onetoone.repository.TutorialRepo;

@RestController
@RequestMapping("/api")
public class TutorialController {
	
	@Autowired
	private TutorialRepo tutorialRepo;
	@Autowired
	private TutorialDetailsRepo tutorialDetailsRepo;
	
	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		List<Tutorial> tutorials = new ArrayList<Tutorial>();
		
		if (title == null) {
			tutorialRepo.findAll().forEach(tutorials::add);
		} else {
			tutorialRepo.findByTitleContaining(title).forEach(tutorials::add);
		}
		
		// if there is no record in database
		if (tutorials.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Tutorial>>(tutorials, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Tutorial tutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with %d".formatted(id)));
		
		return new ResponseEntity<Tutorial>(tutorial, HttpStatus.OK);
	}
	
	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial, BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			Tutorial createdTutorial = tutorialRepo.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
			
			return new ResponseEntity<Tutorial>(createdTutorial, HttpStatus.CREATED);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data %s", getError(bindingResult)));
		}
	}
	
	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable long id, @RequestBody Tutorial tutorial) {
		// find tutorial form database
		Tutorial searchTutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with %d".formatted(id)));
		
		// if tutorial is exist
		searchTutorial.setTitle(tutorial.getTitle());
		searchTutorial.setPublished(tutorial.getPublished());
		searchTutorial.setDescription(tutorial.getDescription());
	
		return new ResponseEntity<Tutorial>(tutorialRepo.save(searchTutorial), HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> findByPublished() {
		
		// find published tutorial value 'true'
		List<Tutorial> publishedTutorial = tutorialRepo.findByPublished(true);
		
		if (publishedTutorial.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Tutorial>>(publishedTutorial, HttpStatus.OK);
	}
	
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable long id) {
		if (tutorialDetailsRepo.existsById(id)) {
			tutorialDetailsRepo.deleteById(id);
		}
		
		tutorialRepo.deleteById(id);
		
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorial() {
		tutorialRepo.deleteAll();
		
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}

	private String getError(BindingResult bindingResult) {		
		return bindingResult.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(", "));
	}

}
