package com.codecraft.forms.dto;

public class QuestionOptionDTO {
	private Long id;
	private String optionText;
	private Integer optionOrder;

	public QuestionOptionDTO() {
	}

	public QuestionOptionDTO(Long id, String optionText, Integer optionOrder) {
		this.id = id;
		this.optionText = optionText;
		this.optionOrder = optionOrder;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public Integer getOptionOrder() {
		return optionOrder;
	}

	public void setOptionOrder(Integer optionOrder) {
		this.optionOrder = optionOrder;
	}
}
