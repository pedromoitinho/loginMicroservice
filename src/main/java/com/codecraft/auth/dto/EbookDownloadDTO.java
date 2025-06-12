package com.codecraft.auth.dto;

import java.time.LocalDateTime;

import com.codecraft.auth.entity.EbookDownload;

public class EbookDownloadDTO {
	private Long id;
	private String email;
	private String companyName;
	private LocalDateTime downloadDate;

	public EbookDownloadDTO() {
	}

	public EbookDownloadDTO(EbookDownload ebookDownload) {
		this.id = ebookDownload.getId();
		this.email = ebookDownload.getEmail();
		this.companyName = ebookDownload.getCompanyName();
		this.downloadDate = ebookDownload.getDownloadDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(LocalDateTime downloadDate) {
		this.downloadDate = downloadDate;
	}
}
