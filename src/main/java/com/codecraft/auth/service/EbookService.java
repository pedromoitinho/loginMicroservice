package com.codecraft.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecraft.auth.dto.EbookDownloadDTO;
import com.codecraft.auth.dto.EbookDownloadRequestDTO;
import com.codecraft.auth.entity.EbookDownload;
import com.codecraft.auth.repository.EbookDownloadRepository;

@Service
public class EbookService {

	private static final Logger logger = LoggerFactory.getLogger(EbookService.class);

	@Autowired
	private EbookDownloadRepository ebookDownloadRepository;

	/**
	 * Register a new ebook download
	 * 
	 * @param downloadRequest containing email and company name
	 * @return DTO with download information
	 */
	public EbookDownloadDTO registerDownload(EbookDownloadRequestDTO downloadRequest) {
		logger.info("Registering ebook download for email: {}", downloadRequest.getEmail());

		// Validate request
		if (downloadRequest.getEmail() == null || downloadRequest.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (downloadRequest.getCompanyName() == null || downloadRequest.getCompanyName().trim().isEmpty()) {
			throw new IllegalArgumentException("Company name is required");
		}

		// Create and save the download entity
		EbookDownload download = new EbookDownload(
				downloadRequest.getEmail().trim(),
				downloadRequest.getCompanyName().trim());

		EbookDownload savedDownload = ebookDownloadRepository.save(download);
		return new EbookDownloadDTO(savedDownload);
	}

	/**
	 * Get all ebook downloads (admin only)
	 * 
	 * @return List of download DTOs
	 */
	public List<EbookDownloadDTO> getAllDownloads() {
		logger.info("Fetching all ebook downloads");

		return ebookDownloadRepository.findAllByOrderByDownloadDateDesc()
				.stream()
				.map(EbookDownloadDTO::new)
				.collect(Collectors.toList());
	}
}
