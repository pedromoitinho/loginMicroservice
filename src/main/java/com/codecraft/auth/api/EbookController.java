package com.codecraft.auth.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecraft.auth.dto.EbookDownloadDTO;
import com.codecraft.auth.dto.EbookDownloadRequestDTO;
import com.codecraft.auth.dto.ErrorResponse;
import com.codecraft.auth.service.EbookService;

@RestController
@RequestMapping("/api/ebook")
public class EbookController {

	private static final Logger logger = LoggerFactory.getLogger(EbookController.class);

	@Autowired
	private EbookService ebookService;

	/**
	 * Register a new ebook download
	 * 
	 * @param downloadRequest containing email and company name
	 * @return Download information
	 */
	@PostMapping("/download")
	public ResponseEntity<?> registerDownload(@RequestBody EbookDownloadRequestDTO downloadRequest) {
		try {
			logger.info("Received ebook download request for email: {}", downloadRequest.getEmail());
			EbookDownloadDTO download = ebookService.registerDownload(downloadRequest);
			return ResponseEntity.ok(download);
		} catch (Exception e) {
			logger.error("Error registering ebook download", e);
			return ResponseEntity.badRequest().body(new ErrorResponse("DOWNLOAD_ERROR", e.getMessage()));
		}
	}

	/**
	 * Get all ebook downloads (admin only)
	 * 
	 * @param auth the authentication object
	 * @return List of all downloads
	 */
	@GetMapping("/downloads")
	public ResponseEntity<?> getAllDownloads(Authentication auth) {
		try {
			// Only admin can access this endpoint
			if (auth == null || !auth.getName().equals("admin")) {
				return ResponseEntity.status(403).body(
						new ErrorResponse("ACCESS_DENIED", "Only admin can access ebook downloads"));
			}

			List<EbookDownloadDTO> downloads = ebookService.getAllDownloads();
			return ResponseEntity.ok(downloads);
		} catch (Exception e) {
			logger.error("Error fetching ebook downloads", e);
			return ResponseEntity.status(500).body(new ErrorResponse("FETCH_ERROR", e.getMessage()));
		}
	}
}
