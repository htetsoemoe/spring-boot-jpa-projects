package com.ninja.spring.onetoone.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ninja.spring.onetoone.model.Tutorial;
import com.ninja.spring.onetoone.repository.TutorialRepo;

@RestController
@RequestMapping("/api")
public class TutorialController {
	
	@Autowired
	private TutorialRepo tutorialRepo;
	
	@GetMapping("/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		List<Tutorial> tutorials = new ArrayList<Tutorial>();
		
		if (title == null) {
			tutorialRepo.findAll().forEach(tutorials::add);
		} else {
			tutorialRepo.findByTitleContaining(title).forEach(tutorials::add);
		}
		
		if (tutorials.isEmpty()) {
			return new ResponseEntity<List<Tutorial>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Tutorial>>(tutorials, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Tutorial tutorial = tutorialRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		return new ResponseEntity<Tutorial>(tutorial, HttpStatus.OK);
	}
	
	@PostMapping("/tutorials")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial, BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			Tutorial createdTutorial = tutorialRepo.save(new Tutorial(tutorial.getId(), tutorial.getTitle(), tutorial.getDescription(), true));
			
			return new ResponseEntity<Tutorial>(createdTutorial, HttpStatus.CREATED);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data %s", getError(bindingResult)));
		}
	}

	private String getError(BindingResult bindingResult) {		
		return bindingResult.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(", "));
	}

}