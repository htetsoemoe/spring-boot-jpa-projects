package com.ninja.spring.manytoMany.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
			Tutorial createdTutorial = tutorialRepo.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
			return new ResponseEntity<Tutorial>(createdTutorial, HttpStatus.CREATED);
		}
				
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data %s", getErrors(bindingResult)));
	}

	private String getErrors(BindingResult bindingResult) {
		return bindingResult.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(", "));
	}
}
