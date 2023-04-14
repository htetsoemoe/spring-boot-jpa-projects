package com.ninja.spring.manytoMany.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.ninja.spring.manytoMany.exception.ResourceNotFoundException;
import com.ninja.spring.manytoMany.model.Tag;
import com.ninja.spring.manytoMany.model.Tutorial;
import com.ninja.spring.manytoMany.repository.TagRepository;
import com.ninja.spring.manytoMany.repository.TutorialRepository;

@Controller
@RequestMapping("/api")
public class TagController {
	
	@Autowired
	private TutorialRepository tutorialRepo;
	@Autowired
	private TagRepository tagRepo;
	
	@PostMapping("/tutorials/{tutorialId}/tags")
	public ResponseEntity<Tag> getAllTagsByTutorialId(@PathVariable long tutorialId, @RequestBody Tag requestTag) {
		
		Tag tag = tutorialRepo.findById(tutorialId).map(tutorial -> {
			
			// Get existed tagId from @RequestBody
			long tagId = requestTag.getId();
			
			// If Tag was already exits
			if (tagId != 0L) {
				Tag existedTag = tagRepo.findById(tagId)
						.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tag with ID %d", tagId)));
				
				// add existed tag to tutorial's tag
				tutorial.addTag(existedTag);
				tutorialRepo.save(tutorial);
				
				return existedTag;
			}
			
			// or Tag is new, add and create new Tag
			tutorial.addTag(requestTag);
			return tagRepo.save(requestTag);
			
		}).orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tutorial with ID %d", tutorialId)));
		
		return new ResponseEntity<Tag>(tag, HttpStatus.CREATED);
	}
	
	@GetMapping("/tags")
	public ResponseEntity<List<Tag>> getAllTags() {
		List<Tag> tags = new ArrayList<Tag>();
		
		tagRepo.findAll().forEach(tags::add);
		
		// If there is no tag in database
		if (tags.isEmpty()) {
			return new ResponseEntity<List<Tag>>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK);
	}
	
	@GetMapping("/tutorials/{tutorialId}/tags")
	public ResponseEntity<List<Tag>> getAllTagsByTutorialId(@PathVariable long tutorialId) {
		if (!tutorialRepo.existsById(tutorialId)) {
			throw new ResourceNotFoundException(String.format("Not found tutorial with ID %d", tutorialId));
		}
		
		List<Tag> tags = tagRepo.findTagsByTutorialsId(tutorialId);
		return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK);
	}
	
	@GetMapping("/tags/{id}")
	public ResponseEntity<Tag> getTagsById(@PathVariable long id) {
		Tag tag = tagRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tag with ID %d", id)));
		
		return new ResponseEntity<Tag>(tag, HttpStatus.OK);
	}
	
	@GetMapping("/tags/{tagId}/tutorials")
	public ResponseEntity<List<Tutorial>> getAllTutorialsByTagId(@PathVariable long tagId) {
		if (!tagRepo.existsById(tagId)) {
			throw new ResourceNotFoundException(String.format("Not found tag with ID %d", tagId));
		}
		
		List<Tutorial> tutorials = tutorialRepo.findTutorialsByTagsId(tagId);
		return new ResponseEntity<List<Tutorial>>(tutorials, HttpStatus.OK);
	}

	@PutMapping("/tags/{tagId}")
	public ResponseEntity<Tag> updateTag(@PathVariable long tagId, @RequestBody Tag requestTag) {
		Tag tag = tagRepo.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tag with ID %d", tagId)));
		
		tag.setName(requestTag.getName());
		return new ResponseEntity<Tag>(tagRepo.save(tag), HttpStatus.OK);
	}
	
	// Delete tag from specific Tutorial using id, there is no remove from tag table
	@DeleteMapping("/tutorials/{tutorialId}/tags/{tagId}")
	public ResponseEntity<HttpStatus> deleteTagFromTutorial(@PathVariable long tutorialId, @PathVariable long tagId) {
		Tutorial tutorial = tutorialRepo.findById(tutorialId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Not found tutorial with ID %d", tutorialId)));
		
		tutorial.removeTag(tagId);
		tutorialRepo.save(tutorial);
		
		return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
	}
	
	// Delete tag from tag table
	@DeleteMapping("/tags/{tagId}")
	public ResponseEntity<HttpStatus> deleteTag(@PathVariable long tagId) {
		if (tagRepo.existsById(tagId)) {
			tagRepo.deleteById(tagId);
			return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
		}
		throw new ResourceNotFoundException(String.format("Not found tag with ID %d", tagId));
	}
}
