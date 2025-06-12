package com.codecraft.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecraft.auth.entity.EbookDownload;

public interface EbookDownloadRepository extends JpaRepository<EbookDownload, Long> {
	// Find all downloads ordered by download date (most recent first)
	List<EbookDownload> findAllByOrderByDownloadDateDesc();

	// Find downloads by email (case-insensitive)
	List<EbookDownload> findByEmailContainingIgnoreCase(String email);

	// Find downloads by company name (case-insensitive)
	List<EbookDownload> findByCompanyNameContainingIgnoreCase(String companyName);
}
