package com.codecraft.forms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "form_responses")
public class FormResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_id", nullable = false)
	private Form form;

	@Column(nullable = false)
	private String username; // O usuário que respondeu

	@Column(name = "submitted_at", nullable = false)
	private LocalDateTime submittedAt;

	@OneToMany(mappedBy = "formResponse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<FormAnswer> answers;

	@PrePersist
	protected void onCreate() {
		submittedAt = LocalDateTime.now();
	}

	// Constructors
	public FormResponse() {
	}

	public FormResponse(Form form, String username) {
		this.form = form;
		this.username = username;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public List<FormAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<FormAnswer> answers) {
		this.answers = answers;
	}
}
