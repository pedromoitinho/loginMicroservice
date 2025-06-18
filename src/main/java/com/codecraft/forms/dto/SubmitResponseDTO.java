package com.codecraft.forms.dto;

import java.util.List;

public class SubmitResponseDTO {
	private Long formId;
	private String userIdentifier;
	private String userGroup;
	private String userEmpresa;
	private List<QuestionResponseDTO> responses;

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
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

	public String getUserEmpresa() {
		return userEmpresa;
	}

	public void setUserEmpresa(String userEmpresa) {
		this.userEmpresa = userEmpresa;
	}

	public List<QuestionResponseDTO> getResponses() {
		return responses;
	}

	public void setResponses(List<QuestionResponseDTO> responses) {
		this.responses = responses;
	}
}
