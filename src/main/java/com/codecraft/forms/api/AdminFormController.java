package com.codecraft.forms.api;

import com.codecraft.forms.entity.Form;
import com.codecraft.forms.entity.FormQuestion;
import com.codecraft.forms.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/forms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminFormController {

	@Autowired
	private FormRepository formRepository;

	@GetMapping
	public ResponseEntity<?> getAllForms(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Usuário não autenticado");
			return ResponseEntity.status(401).body(error);
		}

		// Verificar se é admin
		String username = authentication.getName();
		if (!"admin".equals(username)) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Acesso negado. Apenas administradores podem visualizar todos os formulários.");
			return ResponseEntity.status(403).body(error);
		}

		List<Form> forms = formRepository.findAll();
		return ResponseEntity.ok(forms);
	}

	public static class CreateFormRequest {
		private String title;
		private String description;
		private List<CreateQuestionRequest> questions;

		public static class CreateQuestionRequest {
			private String questionText;
			private String type;
			private Integer order;
			private Boolean isRequired;
			private String options;
			private String[] optionsArray;

			// Getters and Setters
			public String getQuestionText() {
				return questionText;
			}

			public void setQuestionText(String questionText) {
				this.questionText = questionText;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public Integer getOrder() {
				return order;
			}

			public void setOrder(Integer order) {
				this.order = order;
			}

			public Integer getQuestionOrder() {
				return order;
			}

			public void setQuestionOrder(Integer questionOrder) {
				this.order = questionOrder;
			}

			public Boolean getIsRequired() {
				return isRequired;
			}

			public void setIsRequired(Boolean isRequired) {
				this.isRequired = isRequired;
			}

			public String getOptions() {
				if (options != null) {
					return options;
				}
				if (optionsArray != null && optionsArray.length > 0) {
					return String.join(",", optionsArray);
				}
				return null;
			}

			public void setOptions(String options) {
				this.options = options;
			}

			public String[] getOptionsArray() {
				return optionsArray;
			}

			public void setOptionsArray(String[] optionsArray) {
				this.optionsArray = optionsArray;
			}
		}

		// Getters and Setters
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List<CreateQuestionRequest> getQuestions() {
			return questions;
		}

		public void setQuestions(List<CreateQuestionRequest> questions) {
			this.questions = questions;
		}
	}

	@PostMapping
	public ResponseEntity<?> createForm(
			@RequestBody CreateFormRequest request,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Usuário não autenticado");
			return ResponseEntity.status(401).body(error);
		}

		// Verificar se é admin
		String username = authentication.getName();
		if (!"admin".equals(username)) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Acesso negado. Apenas administradores podem criar formulários.");
			return ResponseEntity.status(403).body(error);
		}

		try {
			// Criar formulário
			Form form = new Form(request.getTitle(), request.getDescription());

			// Criar perguntas
			List<FormQuestion> questions = new ArrayList<>();
			if (request.getQuestions() != null) {
				for (CreateFormRequest.CreateQuestionRequest questionReq : request.getQuestions()) {
					FormQuestion question = new FormQuestion();
					question.setForm(form);
					question.setQuestionText(questionReq.getQuestionText());
					question.setType(FormQuestion.QuestionType.valueOf(questionReq.getType()));
					question.setOrder(questionReq.getOrder());
					question.setIsRequired(questionReq.getIsRequired() != null ? questionReq.getIsRequired() : false);
					question.setOptions(questionReq.getOptions());
					questions.add(question);
				}
			}
			form.setQuestions(questions);

			Form savedForm = formRepository.save(form);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "Formulário criado com sucesso!");
			response.put("formId", savedForm.getId());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Erro ao criar formulário: " + e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteForm(
			@PathVariable Long id,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Usuário não autenticado");
			return ResponseEntity.status(401).body(error);
		}

		// Verificar se é admin
		String username = authentication.getName();
		if (!"admin".equals(username)) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Acesso negado. Apenas administradores podem deletar formulários.");
			return ResponseEntity.status(403).body(error);
		}

		try {
			Form form = formRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

			form.setIsActive(false); // Soft delete
			formRepository.save(form);

			Map<String, String> response = new HashMap<>();
			response.put("message", "Formulário desativado com sucesso!");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Erro ao desativar formulário: " + e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}
}
