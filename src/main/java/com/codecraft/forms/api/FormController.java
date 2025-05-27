package com.codecraft.forms.api;

import com.codecraft.forms.dto.FormAnalyticsResponse;
import com.codecraft.forms.dto.FormDetailsResponse;
import com.codecraft.forms.dto.FormSubmissionRequest;
import com.codecraft.forms.entity.Form;
import com.codecraft.forms.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FormController {

	@Autowired
	private FormService formService;

	@GetMapping
	public ResponseEntity<List<Form>> getAllForms() {
		List<Form> forms = formService.getAllActiveForms();
		return ResponseEntity.ok(forms);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getFormDetails(@PathVariable Long id) {
		Optional<FormDetailsResponse> formDetails = formService.getFormDetails(id);

		if (formDetails.isEmpty()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Formulário não encontrado");
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(formDetails.get());
	}

	@PostMapping("/{id}/submit")
	public ResponseEntity<?> submitForm(
			@PathVariable Long id,
			@RequestBody FormSubmissionRequest request,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Usuário não autenticado");
			return ResponseEntity.status(401).body(error);
		}

		String username = authentication.getName();

		try {
			// Verificar se o usuário já respondeu
			if (formService.hasUserRespondedForm(id, username)) {
				Map<String, String> error = new HashMap<>();
				error.put("error", "Você já respondeu este formulário");
				return ResponseEntity.badRequest().body(error);
			}

			request.setFormId(id);
			formService.submitFormResponse(request, username);

			Map<String, String> response = new HashMap<>();
			response.put("message", "Formulário enviado com sucesso!");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/{id}/analytics")
	public ResponseEntity<?> getFormAnalytics(
			@PathVariable Long id,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Usuário não autenticado");
			return ResponseEntity.status(401).body(error);
		}

		// Verificar se é admin (apenas admin pode ver analytics)
		String username = authentication.getName();
		if (!"admin".equals(username)) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Acesso negado. Apenas administradores podem visualizar analytics.");
			return ResponseEntity.status(403).body(error);
		}

		try {
			FormAnalyticsResponse analytics = formService.getFormAnalytics(id);
			return ResponseEntity.ok(analytics);
		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/{id}/check-response")
	public ResponseEntity<?> checkUserResponse(
			@PathVariable Long id,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Usuário não autenticado");
			return ResponseEntity.status(401).body(error);
		}

		String username = authentication.getName();
		boolean hasResponded = formService.hasUserRespondedForm(id, username);

		Map<String, Boolean> response = new HashMap<>();
		response.put("hasResponded", hasResponded);
		return ResponseEntity.ok(response);
	}
}
