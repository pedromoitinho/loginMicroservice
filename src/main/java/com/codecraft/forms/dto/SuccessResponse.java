package com.codecraft.forms.dto;

public class SuccessResponse {
	private String message;
	private boolean success;

	public SuccessResponse(String message) {
		this.message = message;
		this.success = true;
	}

	public SuccessResponse(String message, boolean success) {
		this.message = message;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
