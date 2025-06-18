package com.codecraft.forms.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_responses", indexes = {
		@Index(name = "idx_form_id", columnList = "form_id"),
		@Index(name = "idx_question_id", columnList = "question_id"),
		@Index(name = "idx_form_question", columnList = "form_id, question_id"),
		@Index(name = "idx_form_user_group", columnList = "form_id, user_group"),
		@Index(name = "idx_form_user_setor", columnList = "form_id, user_setor"),
		@Index(name = "idx_form_user_empresa", columnList = "form_id, user_empresa"),
		@Index(name = "idx_question_option", columnList = "question_id, option_id"),
		@Index(name = "idx_question_user_group", columnList = "question_id, user_group"),
		@Index(name = "idx_question_user_setor", columnList = "question_id, user_setor"),
		@Index(name = "idx_question_user_empresa", columnList = "question_id, user_empresa")
})
public class UserResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id", nullable = false)
	private Form form;

	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;

	@ManyToOne
	@JoinColumn(name = "option_id")
	private QuestionOption selectedOption;

	@Column(name = "response_text", columnDefinition = "TEXT")
	private String responseText;

	@Column(name = "response_number")
	private Double responseNumber;

	@Column(name = "user_identifier")
	private String userIdentifier;

	@Column(name = "user_group")
	private String userGroup;

	@Column(name = "user_setor")
	private String userSetor;

	@Column(name = "user_empresa")
	private String userEmpresa;

	@CreationTimestamp
	private LocalDateTime submittedAt;

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

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public QuestionOption getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(QuestionOption selectedOption) {
		this.selectedOption = selectedOption;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public Double getResponseNumber() {
		return responseNumber;
	}

	public void setResponseNumber(Double responseNumber) {
		this.responseNumber = responseNumber;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserSetor() {
		return userSetor;
	}

	public void setUserSetor(String userSetor) {
		this.userSetor = userSetor;
	}

	public String getUserEmpresa() {
		return userEmpresa;
	}

	public void setUserEmpresa(String userEmpresa) {
		this.userEmpresa = userEmpresa;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

}