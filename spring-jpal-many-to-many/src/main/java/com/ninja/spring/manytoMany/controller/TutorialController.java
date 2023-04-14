package com.ninja.spring.manytoMany.controller;

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

import com.ninja.spring.manytoMany.exception.ResourceNotFoundException;
import com.ninja.spring.manytoMany.model.Tutorial;
import com.ninja.spring.manytoMany.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class TutorialController {
	
	@Autowired
	private TutorialRepository tutorialRepo;

	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial, BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			Tutorial createdTutorial = tutorialRepo.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), tutorial.isPublished()));
			return new ResponseEntity<Tutorial>(createdTutorial, HttpStatus.CREATED);
		}
				
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data %s", getErrors(bindingResult)));
	}

	private String getErrors(BindingResult bindingResult) {
		return bindingResult.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(", "));
	}
	
	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		List<Tutorial> tutorials = new ArrayList<Tutorial>();
		
		if (title == null) {
			tutorialRepo.findAll().forEach(tutorials::add);
		} else {
			tutorialRepo.findByTitleContaining(title).forEach(tutorials::add);
		}
		
		// There is no data in database
		if (tutorials.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Tutorial>>(tutorials, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable long id) {
		Tutorial tutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tutorial with ID : %d", id)));
		
		return new ResponseEntity<Tutorial>(tutorial, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> getTutorialByPublished() {
		List<Tutorial> tutorials = tutorialRepo.findByPublished(true);
		
		if (tutorials.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Tutorial>>(tutorials, HttpStatus.OK);
	}
	
	@PutMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable long id, @RequestBody Tutorial tutorial) {
		Tutorial searchedTutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tutorial with ID : %d", id)));
		
		searchedTutorial.setTitle(tutorial.getTitle());
		searchedTutorial.setDescription(tutorial.getDescription());
		searchedTutorial.setPublished(tutorial.isPublished());
		
		return new ResponseEntity<Tutorial>(tutorialRepo.save(searchedTutorial), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<HttpStatus> deleteTutorials(@PathVariable long id) {
		if (tutorialRepo.existsById(id)) {
			tutorialRepo.deleteById(id);
			return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		tutorialRepo.deleteAll();
		
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}
}
