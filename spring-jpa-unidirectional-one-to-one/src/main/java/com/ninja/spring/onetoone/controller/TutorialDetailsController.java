package com.ninja.spring.onetoone.controller;

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

import com.ninja.spring.onetoone.exception.ResourceNotFoundException;
import com.ninja.spring.onetoone.model.Tutorial;
import com.ninja.spring.onetoone.model.TutorialDetails;
import com.ninja.spring.onetoone.repository.TutorialDetailsRepo;
import com.ninja.spring.onetoone.repository.TutorialRepo;

@RestController
@RequestMapping("/api")
public class TutorialDetailsController {
	
	@Autowired
	private TutorialDetailsRepo tutorialDetailRepo;
	@Autowired
	private TutorialRepo tutorialRepo;
	
	@PostMapping("/tutorials/{tutorialId}/details")
	public ResponseEntity<TutorialDetails> createTutorialDetails(@PathVariable long tutorialId, @RequestBody TutorialDetails tutorialDetails) {
		// find existed tutorial with tutorialId
		Tutorial tutorial = tutorialRepo.findById(tutorialId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with %d".formatted(tutorialId)));
		
		tutorialDetails.setCreateOn(new java.util.Date());
		tutorialDetails.setTutorial(tutorial);
		TutorialDetails details = tutorialDetailRepo.save(tutorialDetails);
		
		return new ResponseEntity<TutorialDetails>(details, HttpStatus.CREATED);
	}
	
	@GetMapping("/tutorials/{tutorialId}/details")
	public ResponseEntity<TutorialDetails> getTutorialDetails(@PathVariable long tutorialId) {
		TutorialDetails tutorialDetails = tutorialDetailRepo.findById(tutorialId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Tutorila with %d".formatted(tutorialId)));
		
		return new ResponseEntity<TutorialDetails>(tutorialDetails, HttpStatus.OK);
	}
	
	// update details of specific tutorial
	@PutMapping("/details/{id}")
	public ResponseEntity<TutorialDetails> updateDetails(@PathVariable long id, @RequestBody TutorialDetails tutorialDetails) {
		// find TutorialDetails by Id
		TutorialDetails details = tutorialDetailRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with %d".formatted(id)));
		
		details.setCreatedBy(tutorialDetails.getCreatedBy());
		
		return new ResponseEntity<TutorialDetails>(tutorialDetailRepo.save(details), HttpStatus.OK);
	}
	
	// delete details of specific tutorial
	@DeleteMapping("/details/{id}")
	public ResponseEntity<HttpStatus> deleteTutorialDetails(@PathVariable long id) {
		
		if (tutorialDetailRepo.existsById(id)) {
			tutorialDetailRepo.deleteById(id);
			return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
		}
			
		return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/tutorials/{tutorialId}/details")
	public ResponseEntity<TutorialDetails> deleteDetailsByTutorialId(@PathVariable long tutorialId) {
		// 	true for there is no tutorial with id
		if (!tutorialRepo.existsById(tutorialId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		tutorialDetailRepo.deleteById(tutorialId);
		return new ResponseEntity<TutorialDetails>(HttpStatus.NO_CONTENT);
	}

}
