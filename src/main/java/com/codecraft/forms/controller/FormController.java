package com.codecraft.forms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecraft.auth.dto.ErrorResponse;
import com.codecraft.forms.dto.CreateFormDTO;
import com.codecraft.forms.dto.CreateQuestionDTO;
import com.codecraft.forms.dto.FormDTO;
import com.codecraft.forms.dto.FormStatisticsDTO;
import com.codecraft.forms.dto.SubmitResponseDTO;
import com.codecraft.forms.dto.SuccessResponse;
import com.codecraft.forms.dto.UpdateFormDTO;
import com.codecraft.forms.services.FormService;
import com.codecraft.forms.services.ResponseService;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*")
public class FormController {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private FormService formService;

	@Autowired
	private ResponseService responseService;

	// Criar formulário (protegido)
	@PostMapping("/create")
	public ResponseEntity<?> createForm(@RequestBody CreateFormDTO createFormDTO, Authentication auth) {
		try {
			FormDTO form = formService.createForm(createFormDTO, auth.getName());
			return ResponseEntity.ok(form);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("CREATE_ERROR", e.getMessage()));
		}
	}

	// Listar formulários do usuário (protegido)
	@GetMapping("/my-forms")
	public ResponseEntity<?> getMyForms(Authentication auth) {
		List<FormDTO> forms = formService.getUserForms(auth.getName());
		return ResponseEntity.ok(forms);
	}

	// Obter formulário público para responder
	@GetMapping("/public/{formId}")
	public ResponseEntity<?> getPublicForm(@PathVariable Long formId) {
		try {
			FormDTO form = formService.getPublicForm(formId);
			return ResponseEntity.ok(form);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/public-forms")
	public ResponseEntity<?> getAllPublicForms() {
		List<FormDTO> forms = formService.getAllForms();
		return ResponseEntity.ok(forms);
	}

	// Submeter respostas (público)
	@PostMapping("/submit")
	public ResponseEntity<?> submitResponse(@RequestBody SubmitResponseDTO submitDTO) {
		try {
			responseService.submitResponses(submitDTO);
			return ResponseEntity.ok(new SuccessResponse("Respostas enviadas com sucesso"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("SUBMIT_ERROR", e.getMessage()));
		}
	}

	// Obter estatísticas (protegido)
	@GetMapping("/{formId}/statistics")
	@SuppressWarnings("CallToPrintStackTrace")
	public ResponseEntity<?> getFormStatistics(@PathVariable Long formId, Authentication auth) {
		try {
			// Use ResponseService which has proper permission checking for analytics
			FormStatisticsDTO stats = responseService.getFormStatistics(formId, auth.getName());

			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace(); // Add stack trace for better debugging
			return ResponseEntity.badRequest().body(new ErrorResponse("STATS_ERROR", e.getMessage()));
		}
	}

	// Obter setores disponíveis para um formulário (protegido)
	@GetMapping("/{formId}/sectors")
	public ResponseEntity<?> getFormSectors(@PathVariable Long formId, Authentication auth) {
		try {
			List<String> sectors = responseService.getFormSectors(formId, auth.getName());
			return ResponseEntity.ok(sectors);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("SECTORS_ERROR", e.getMessage()));
		}
	}

	// Obter estatísticas filtradas por setor (protegido)
	@GetMapping("/{formId}/statistics/sector/{setor}")
	@SuppressWarnings("CallToPrintStackTrace")
	public ResponseEntity<?> getFormStatisticsBySetor(@PathVariable Long formId, @PathVariable String setor,
			Authentication auth) {
		try {
			// Decode the sector parameter in case it contains special characters
			String decodedSetor = java.net.URLDecoder.decode(setor, "UTF-8");

			FormStatisticsDTO stats = responseService.getFormStatisticsFiltered(formId, auth.getName(), decodedSetor);
			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace(); // Add stack trace for better debugging
			return ResponseEntity.badRequest().body(new ErrorResponse("STATS_SETOR_ERROR", e.getMessage()));
		}
	}

	// Obter empresas disponíveis para um formulário (protegido)
	@GetMapping("/{formId}/empresas")
	public ResponseEntity<?> getFormEmpresas(@PathVariable Long formId, Authentication auth) {
		try {
			List<String> empresas = responseService.getFormEmpresas(formId, auth.getName());
			return ResponseEntity.ok(empresas);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("EMPRESAS_ERROR", e.getMessage()));
		}
	}

	// Obter estatísticas filtradas por empresa (protegido)
	@GetMapping("/{formId}/statistics/empresa/{empresa}")
	@SuppressWarnings("CallToPrintStackTrace")
	public ResponseEntity<?> getFormStatisticsByEmpresa(@PathVariable Long formId, @PathVariable String empresa,
			Authentication auth) {
		try {
			// Decode the empresa parameter in case it contains special characters
			String decodedEmpresa = java.net.URLDecoder.decode(empresa, "UTF-8");

			FormStatisticsDTO stats = responseService.getFormStatisticsFilteredByEmpresa(formId, auth.getName(),
					decodedEmpresa);
			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace(); // Add stack trace for better debugging
			return ResponseEntity.badRequest().body(new ErrorResponse("STATS_EMPRESA_ERROR", e.getMessage()));
		}
	}

	// Listar todos os formulários (admin only)
	@GetMapping("/all")
	public ResponseEntity<?> getAllForms(Authentication auth) {
		try {
			// Check if user is admin
			if (!"admin".equals(auth.getName())) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("ACCESS_DENIED", "Only admin can access all forms"));
			}
			List<FormDTO> forms = formService.getAllForms();
			return ResponseEntity.ok(forms);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("FETCH_ERROR", e.getMessage()));
		}
	}

	// Obter detalhes do formulário (protegido)
	@GetMapping("/details/{formId}")
	public ResponseEntity<?> getFormDetails(@PathVariable Long formId, Authentication auth) {
		try {
			FormDTO form = formService.getFormDetails(formId, auth.getName());
			return ResponseEntity.ok(form);
		} catch (Exception e) {
			// Check if it's an access denied error
			if (e.getMessage() != null && e.getMessage().contains("Acesso negado")) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("ACCESS_DENIED", "Access denied to view this form"));
			}

			// Check if it's a not found error
			if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
				return ResponseEntity.notFound().build();
			}

			return ResponseEntity.badRequest().body(new ErrorResponse("FETCH_ERROR", e.getMessage()));
		}
	}

	// Atualizar formulário (protegido)
	@PutMapping("/update/{formId}")
	public ResponseEntity<?> updateForm(@PathVariable Long formId, @RequestBody UpdateFormDTO updateFormDTO,
			Authentication auth) {
		try {
			System.out.println("🔄 FormController.updateForm called");
			System.out.println("📥 Received UpdateFormDTO:");
			System.out.println(String.format("  Title: '%s'", updateFormDTO.getTitle()));
			System.out.println(String.format("  Questions count: %d",
					updateFormDTO.getQuestions() != null ? updateFormDTO.getQuestions().size() : 0));

			if (updateFormDTO.getQuestions() != null) {
				for (int i = 0; i < updateFormDTO.getQuestions().size(); i++) {
					CreateQuestionDTO q = updateFormDTO.getQuestions().get(i);
					System.out.println(String.format("    Question %d: text='%s', type=%s",
							i + 1, q.getQuestionText(), q.getType()));
				}
			}

			FormDTO form = formService.updateForm(formId, updateFormDTO, auth.getName());
			return ResponseEntity.ok(form);
		} catch (Exception e) {
			System.err.println("❌ Error updating form: " + e.getMessage());
			e.printStackTrace();

			// Check if it's an access denied error
			if (e.getMessage() != null && e.getMessage().contains("Acesso negado")) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("ACCESS_DENIED", "Access denied to update this form"));
			}

			return ResponseEntity.badRequest().body(new ErrorResponse("UPDATE_ERROR", e.getMessage()));
		}
	}

	// Deletar formulário (protegido)
	@DeleteMapping("/delete/{formId}")
	public ResponseEntity<?> deleteForm(@PathVariable Long formId, Authentication auth) {
		try {
			formService.deleteForm(formId, auth.getName());
			return ResponseEntity.ok(new SuccessResponse("Formulário deletado com sucesso"));
		} catch (Exception e) {
			// Log the error instead of printing stack trace
			logger.error("Error deleting form with ID {}: {}", formId, e.getMessage(), e);

			// Check if it's an access denied error
			if (e.getMessage() != null && e.getMessage().contains("Acesso negado")) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("ACCESS_DENIED", "Access denied to delete this form"));
			}

			return ResponseEntity.badRequest().body(new ErrorResponse("DELETE_ERROR", e.getMessage()));
		}
	}

	// Obter formulários já respondidos pelo usuário
	@GetMapping("/answered")
	public ResponseEntity<?> getAnsweredForms(Authentication auth) {
		try {
			List<Long> answeredFormIds = formService.getAnsweredFormIds(auth.getName());
			return ResponseEntity.ok(answeredFormIds);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ErrorResponse("FETCH_ERROR", e.getMessage()));
		}
	}
}