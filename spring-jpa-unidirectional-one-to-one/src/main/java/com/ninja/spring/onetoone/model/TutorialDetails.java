package com.ninja.spring.onetoone.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tutorial_details")
public class TutorialDetails implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	
	private Date createOn;
	private String createdBy;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@PrimaryKeyJoinColumn(name = "tutorial_id")
	private Tutorial tutorial;

	public TutorialDetails() {
	}

	public TutorialDetails(String createdBy) {
		super();
		this.createdBy = createdBy;
	}

	public Date getCreateOn() {
		return createOn;
	}

	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Tutorial getTutorial() {
		return tutorial;
	}

	public void setTutorial(Tutorial tutorial) {
		this.tutorial = tutorial;
	}

}
