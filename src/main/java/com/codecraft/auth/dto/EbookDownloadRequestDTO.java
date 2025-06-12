package com.codecraft.auth.dto;

public class EbookDownloadRequestDTO {
	private String email;
	private String companyName;

	public EbookDownloadRequestDTO() {
	}

	public EbookDownloadRequestDTO(String email, String companyName) {
		this.email = email;
		this.companyName = companyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}
