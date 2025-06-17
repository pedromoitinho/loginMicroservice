package com.codecraft.forms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecraft.auth.entity.User;
import com.codecraft.auth.repository.UserRepository;
import com.codecraft.forms.dto.CreateFormDTO;
import com.codecraft.forms.dto.CreateQuestionDTO;
import com.codecraft.forms.dto.FormDTO;
import com.codecraft.forms.dto.FormStatisticsDTO;
import com.codecraft.forms.dto.QuestionDTO;
import com.codecraft.forms.dto.QuestionOptionDTO;
import com.codecraft.forms.dto.UpdateFormDTO;
import com.codecraft.forms.entity.Form;
import com.codecraft.forms.entity.Question;
import com.codecraft.forms.entity.QuestionOption;
import com.codecraft.forms.repository.FormRepository;
import com.codecraft.forms.repository.QuestionRepository;
import com.codecraft.forms.repository.UserResponseRepository;

@Service
public class FormService {
	@Autowired
	private FormRepository formRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserResponseRepository userResponseRepository;

	@Autowired
	private QuestionRepository questionRepository;

	public FormDTO createForm(CreateFormDTO createFormDTO, String username) {
		System.out.println("📝 FormService.createForm called");
		User user = userRepository.findByUsername(username);

		Form form = new Form();
		form.setTitle(createFormDTO.getTitle());
		form.setDescription(createFormDTO.getDescription());
		form.setCreatedBy(user);
		form.setAllowedGroups(createFormDTO.getAllowedGroups());

		for (CreateQuestionDTO questionDTO : createFormDTO.getQuestions()) {
			Question question = new Question();
			question.setQuestionText(questionDTO.getQuestionText());

			// Ensure type is properly set - first set the enum, which will automatically
			// set questionType
			try {
				question.setType(questionDTO.getType());
				System.out.println(String.format("✅ Question type set to '%s'", question.getType()));
			} catch (Exception e) {
				System.out.println(String.format("⚠️ Error setting question type: %s, defaulting to TEXT", e.getMessage()));
				question.setType(com.codecraft.forms.type.QuestionType.TEXT);
			}

			question.setQuestionOrder(questionDTO.getQuestionOrder());
			question.setForm(form);

			if (questionDTO.getOptions() != null) {
				System.out.println(String.format("📋 Processing %d options for question", questionDTO.getOptions().size()));
				for (int i = 0; i < questionDTO.getOptions().size(); i++) {
					QuestionOption option = new QuestionOption();
					option.setText(questionDTO.getOptions().get(i));
					option.setOptionOrder(i);
					option.setQuestion(question);
					question.getOptions().add(option);
					System.out.println(String.format("  🔘 Added option %d: '%s'", i + 1, questionDTO.getOptions().get(i)));
				}
			}
			form.getQuestions().add(question);
			System.out.println(
					String.format("➕ Added question: '%s' (Type: %s)", question.getQuestionText(), question.getType()));
		}

		System.out.println("💾 Saving form with " + form.getQuestions().size() + " questions");
		Form savedForm = formRepository.save(form);
		System.out.println("✅ Form saved successfully");

		return convertToDTO(savedForm);
	}

	private FormDTO convertToDTO(Form form) {
		System.out.println("🔄 Converting Form to DTO - Form ID: " + form.getId());
		FormDTO dto = new FormDTO();
		dto.setId(form.getId());
		dto.setTitle(form.getTitle());
		dto.setDescription(form.getDescription());
		dto.setCreatedBy(form.getCreatedBy() != null ? form.getCreatedBy().getUsername() : "Unknown");
		dto.setCreatedAt(form.getCreatedAt());
		dto.setIsActive(form.getIsActive());
		dto.setAllowedGroups(form.getAllowedGroups());
		// Map questions and their options
		if (form.getQuestions() != null) {
			System.out.println("📋 Converting " + form.getQuestions().size() + " questions to DTO");
			List<QuestionDTO> questionDTOs = form.getQuestions().stream()
					.sorted(Comparator.comparing(Question::getQuestionOrder)).map(question -> {
						System.out.println(String.format(
								"  📝 Converting Question ID=%d, Text='%s', Type=%s, QuestionType='%s', Options=%d",
								question.getId(), question.getQuestionText(), question.getType(), question.getQuestionType(),
								question.getOptions() != null ? question.getOptions().size() : 0));

						// Use questionType string field as primary, fallback to enum if needed
						String questionTypeString = question.getQuestionType();
						if (questionTypeString == null || questionTypeString.isEmpty()) {
							questionTypeString = question.getType() != null ? question.getType().name() : "TEXT";
							System.out.println(
									String.format("    ⚠️ Question %d: questionType was null/empty, using enum fallback: %s",
											question.getId(), questionTypeString));
						}

						QuestionDTO qDto = new QuestionDTO(question.getId(), question.getQuestionText(),
								questionTypeString, question.getQuestionOrder());

						// Explicitly set both type and questionType for clarity
						qDto.setType(questionTypeString);
						qDto.setQuestionType(questionTypeString);

						if (question.getOptions() != null) {
							List<QuestionOptionDTO> optionDTOs = question.getOptions().stream()
									.sorted(Comparator.comparing(QuestionOption::getOptionOrder))
									.map(opt -> {
										System.out.println(String.format("    🔘 Converting Option ID=%d, Text='%s', Order=%d",
												opt.getId(), opt.getText(), opt.getOptionOrder()));
										return new QuestionOptionDTO(opt.getId(), opt.getText(), opt.getOptionOrder());
									})
									.collect(Collectors.toList());
							qDto.setOptions(optionDTOs);
						} else {
							System.out.println("    🚫 No options for this question");
						}
						return qDto;
					}).collect(Collectors.toList());
			dto.setQuestions(questionDTOs);
		} else {
			dto.setQuestions(Collections.emptyList());
		}

		System.out.println("✅ Form conversion completed - DTO questions count: " +
				(dto.getQuestions() != null ? dto.getQuestions().size() : 0));
		return dto;
	}

	public List<FormDTO> getUserForms(String username) {
		User user = userRepository.findByUsername(username);
		String userGroup = user != null ? user.getUserGroup() : null;
		List<Form> forms = formRepository.findByIsActiveTrueOrderByCreatedAtDesc();
		return forms.stream().filter(form -> {
			String allowedGroups = form.getAllowedGroups();
			if (allowedGroups == null || allowedGroups.isBlank())
				return true;
			if (userGroup == null || userGroup.isBlank())
				return false;
			for (String group : allowedGroups.split(",")) {
				if (userGroup.trim().equalsIgnoreCase(group.trim()))
					return true;
			}
			return false;
		}).map(this::convertToDTO).collect(Collectors.toList());
	}

	// Obter formulário público (para responder)
	public FormDTO getPublicForm(Long formId) {
		System.out.println("🔍 getPublicForm called for formId: " + formId);

		Form form = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

		System.out.println("📋 Public form found in database:");
		System.out.println(String.format("  Form ID: %d, Title: '%s', Questions: %d",
				form.getId(), form.getTitle(), form.getQuestions().size()));

		for (int i = 0; i < form.getQuestions().size(); i++) {
			Question q = form.getQuestions().get(i);
			System.out.println(String.format("  Question %d: ID=%d, Text='%s', Type=%s, Options=%d",
					i + 1, q.getId(), q.getQuestionText(), q.getType(),
					q.getOptions() != null ? q.getOptions().size() : 0));
		}

		FormDTO result = convertToDTO(form);
		System.out.println("✅ getPublicForm completed, returning DTO with " +
				(result.getQuestions() != null ? result.getQuestions().size() : 0) + " questions");

		return result;
	}

	// Obter todos os formulários (admin only)
	public List<FormDTO> getAllForms() {
		List<Form> forms = formRepository.findAllByOrderByCreatedAtDesc();
		return forms.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Obter detalhes do formulário (protegido)
	public FormDTO getFormDetails(Long formId, String username) {
		System.out.println("🔍 getFormDetails called for formId: " + formId + ", username: " + username);

		Form form = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

		System.out.println("📋 Form found in database:");
		System.out.println(String.format("  Form ID: %d, Title: '%s', Questions: %d",
				form.getId(), form.getTitle(), form.getQuestions().size()));

		// Log detailed question information
		for (int i = 0; i < form.getQuestions().size(); i++) {
			Question q = form.getQuestions().get(i);
			System.out.println(String.format("  Question %d: ID=%d, Text='%s', Type=%s, Options=%d",
					i + 1, q.getId(), q.getQuestionText(), q.getType(),
					q.getOptions() != null ? q.getOptions().size() : 0));

			if (q.getOptions() != null && q.getOptions().size() > 0) {
				for (int j = 0; j < q.getOptions().size(); j++) {
					QuestionOption opt = q.getOptions().get(j);
					System.out.println(String.format("    Option %d: ID=%d, Text='%s'",
							j + 1, opt.getId(), opt.getText()));
				}
			}
		}

		// Verificar se o usuário é o dono do formulário ou admin
		if (!"admin".equals(username)
				&& (form.getCreatedBy() == null || !form.getCreatedBy().getUsername().equals(username))) {
			throw new RuntimeException("Acesso negado");
		}

		FormDTO result = convertToDTO(form);
		System.out.println("✅ getFormDetails completed, returning DTO with " +
				(result.getQuestions() != null ? result.getQuestions().size() : 0) + " questions");

		return result;
	}

	// Atualizar formulário
	@Transactional
	public FormDTO updateForm(Long formId, UpdateFormDTO updateFormDTO, String username) {
		System.out.println("🔄 FormService.updateForm called with formId: " + formId);
		System.out.println("📝 UpdateFormDTO received: " + updateFormDTO);

		if (updateFormDTO.getQuestions() != null) {
			System.out.println("📋 Questions in update request: " + updateFormDTO.getQuestions().size());
			for (int i = 0; i < updateFormDTO.getQuestions().size(); i++) {
				CreateQuestionDTO q = updateFormDTO.getQuestions().get(i);
				System.out.println(String.format("  Question %d: text='%s', type=%s, hasOptions=%s, optionsCount=%d",
						i + 1,
						q.getQuestionText(),
						q.getType(),
						q.getOptions() != null,
						q.getOptions() != null ? q.getOptions().size() : 0));
			}
		}

		Form form = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

		// Verificar se o usuário é o dono do formulário ou admin
		if (!"admin".equals(username)
				&& (form.getCreatedBy() == null || !form.getCreatedBy().getUsername().equals(username))) {
			throw new RuntimeException("Acesso negado");
		}

		// Atualizar campos básicos
		if (updateFormDTO.getTitle() != null) {
			form.setTitle(updateFormDTO.getTitle());
		}
		if (updateFormDTO.getDescription() != null) {
			form.setDescription(updateFormDTO.getDescription());
		}
		if (updateFormDTO.getIsActive() != null) {
			form.setIsActive(updateFormDTO.getIsActive());
		}
		if (updateFormDTO.getAllowedGroups() != null) {
			form.setAllowedGroups(updateFormDTO.getAllowedGroups());
		}

		// Atualizar perguntas se fornecidas
		if (updateFormDTO.getQuestions() != null) {
			System.out.println("🗑️ Clearing existing questions from form");
			// Remover perguntas antigas explicitamente
			form.getQuestions().clear();

			// Força o flush para garantir que as perguntas antigas sejam deletadas
			formRepository.saveAndFlush(form);

			System.out.println("➕ Adding new questions to form");
			// Adicionar novas perguntas
			for (int i = 0; i < updateFormDTO.getQuestions().size(); i++) {
				CreateQuestionDTO questionDTO = updateFormDTO.getQuestions().get(i);
				System.out.println(String.format("  Creating question %d: '%s' (type: %s)",
						i + 1, questionDTO.getQuestionText(), questionDTO.getType()));

				Question question = new Question();
				question.setQuestionText(questionDTO.getQuestionText());

				// Ensure type is properly set - first set the enum which will automatically set
				// questionType
				try {
					question.setType(questionDTO.getType());
					System.out.println(String.format("  ✅ Question type set to '%s'", question.getType()));
				} catch (Exception e) {
					System.out.println(
							String.format("  ⚠️ Error setting question type: %s, defaulting to TEXT", e.getMessage()));
					question.setType(com.codecraft.forms.type.QuestionType.TEXT);
				}

				question.setQuestionOrder(questionDTO.getQuestionOrder());
				question.setForm(form);

				System.out.println(
						String.format("  ✅ Question created - ID: null, Text: '%s', Type: %s, TypeString: %s",
								question.getQuestionText(), question.getType(), question.getQuestionType()));

				if (questionDTO.getOptions() != null && questionDTO.getOptions().size() > 0) {
					System.out.println(String.format("    Adding %d options to question %d",
							questionDTO.getOptions().size(), i + 1));
					for (int j = 0; j < questionDTO.getOptions().size(); j++) {
						QuestionOption option = new QuestionOption();
						option.setText(questionDTO.getOptions().get(j));
						option.setOptionOrder(j);
						option.setQuestion(question);
						question.getOptions().add(option);
						System.out.println(String.format("      Option %d: '%s'", j + 1, questionDTO.getOptions().get(j)));
					}
				} else {
					System.out.println(String.format("    Question %d has no options", i + 1));
				}
				form.getQuestions().add(question);

				System.out.println(String.format("    ➕ Question %d added to form. Form now has %d questions",
						i + 1, form.getQuestions().size()));
			}
		}

		System.out.println("💾 Saving updated form");
		Form savedForm = formRepository.saveAndFlush(form);
		System.out.println("✅ Form saved and flushed to database");

		System.out.println("🔍 Verifying saved form data:");
		System.out.println(String.format("  Form ID: %d, Questions count: %d",
				savedForm.getId(), savedForm.getQuestions().size()));

		for (int i = 0; i < savedForm.getQuestions().size(); i++) {
			Question q = savedForm.getQuestions().get(i);
			System.out.println(
					String.format("  Saved Question %d: ID=%d, Text='%s', Type (enum)=%s, Type (string)='%s', Options=%d",
							i + 1, q.getId(), q.getQuestionText(), q.getType(), q.getQuestionType(),
							q.getOptions() != null ? q.getOptions().size() : 0));

			// Log the options too
			if (q.getOptions() != null && q.getOptions().size() > 0) {
				for (int j = 0; j < q.getOptions().size(); j++) {
					QuestionOption opt = q.getOptions().get(j);
					System.out.println(String.format("    Option %d: ID=%d, Text='%s', Order=%d",
							j + 1, opt.getId(), opt.getText(), opt.getOptionOrder()));
				}
			}
		}

		FormDTO result = convertToDTO(savedForm);
		System.out.println("✅ Form updated successfully. Result questions count: " +
				(result.getQuestions() != null ? result.getQuestions().size() : 0));

		System.out.println("🔍 Final DTO verification:");
		if (result.getQuestions() != null) {
			for (int i = 0; i < result.getQuestions().size(); i++) {
				QuestionDTO q = result.getQuestions().get(i);
				System.out.println(String.format("  DTO Question %d: ID=%d, Text='%s', Type='%s', Options=%d",
						i + 1, q.getId(), q.getQuestionText(), q.getType(),
						q.getOptions() != null ? q.getOptions().size() : 0));
			}
		}

		// 🔍 EXTRA VALIDATION: Re-fetch the form from database to verify persistence
		System.out.println("🔍 EXTRA VALIDATION: Re-fetching form from database to verify persistence...");
		Form reloadedForm = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Form not found after update"));

		System.out.println("📋 Reloaded form verification:");
		System.out.println(String.format("  Form ID: %d, Questions count: %d",
				reloadedForm.getId(), reloadedForm.getQuestions().size()));

		for (int i = 0; i < reloadedForm.getQuestions().size(); i++) {
			Question q = reloadedForm.getQuestions().get(i);
			System.out.println(String.format("  Reloaded Question %d: ID=%d, Text='%s', Type=%s, Options=%d",
					i + 1, q.getId(), q.getQuestionText(), q.getType(),
					q.getOptions() != null ? q.getOptions().size() : 0));
		}

		return result;
	}

	public List<FormDTO> getPublicForms() {
		List<Form> forms = formRepository.findByIsActiveTrueOrderByCreatedAtDesc();
		return forms.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Deletar formulário
	@Transactional
	public void deleteForm(Long formId, String username) {
		Form form = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

		// Verificar se o usuário é o dono do formulário ou admin
		if (!"admin".equals(username)
				&& (form.getCreatedBy() == null || !form.getCreatedBy().getUsername().equals(username))) {
			throw new RuntimeException("Acesso negado");
		}

		try {
			// Primeiro, deletar todas as respostas de usuários associadas a este formulário
			userResponseRepository.deleteAll(userResponseRepository.findByForm(form));

			// Limpar manualmente as relações para evitar problemas de constraint
			form.getQuestions().forEach(question -> {
				// Limpar opções de cada questão
				question.setForm(null); // Dissociate question from form
				question.getOptions().forEach(option -> option.setQuestion(null)); // Dissociate options from question
				question.getOptions().clear();
			});

			// Limpar as questões do formulário
			form.getQuestions().clear();

			// Salvar o formulário com as relações limpas
			formRepository.saveAndFlush(form);

			// Agora podemos deletar o formulário
			formRepository.delete(form);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao excluir formulário: " + e.getMessage(), e);
		}
	}

	public List<Long> getAnsweredFormIds(String username) {
		List<Form> allForms = formRepository.findAll();
		return allForms.stream().filter(form -> {
			List<com.codecraft.forms.entity.UserResponse> responses = userResponseRepository
					.findByFormAndUserIdentifier(form, username);
			return !responses.isEmpty();
		}).map(Form::getId).collect(Collectors.toList());
	}

	public FormStatisticsDTO getFormStatistics(Long formId) {
		Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
		List<Question> questions = questionRepository.findByFormId(formId);

		// Get total responses count for this form
		int totalResponses = userResponseRepository.countByForm(form).intValue();
		List<FormStatisticsDTO.QuestionStatisticsDTO> questionsAnalytics = new ArrayList<>();

		// For each question, collect answer counts, text answers, and group breakdowns
		for (Question question : questions) {
			Map<String, Integer> answerCounts = new LinkedHashMap<>();
			Map<String, Map<String, Integer>> answerCountsByGroup = new LinkedHashMap<>();
			List<String> textAnswers = new ArrayList<>();

			if (question.getQuestionType().equalsIgnoreCase("MULTIPLE_CHOICE")
					|| question.getQuestionType().equalsIgnoreCase("CHECKBOX")
					|| question.getQuestionType().equalsIgnoreCase("RADIO")) {

				// Get option-based counts with group breakdown
				List<Object[]> optionCounts = userResponseRepository.countResponsesByOption(question.getId());
				List<Object[]> optionGroupCounts = userResponseRepository.countResponsesByOptionAndGroup(question.getId());

				// Process overall counts
				for (Object[] row : optionCounts) {
					String optionText = (String) row[0];
					Long count = (Long) row[1];
					answerCounts.put(optionText, count.intValue());
				}

				// Process group-based counts
				for (Object[] row : optionGroupCounts) {
					String optionText = (String) row[0];
					String userGroup = (String) row[1];
					Long count = (Long) row[2];

					answerCountsByGroup.computeIfAbsent(optionText, k -> new LinkedHashMap<>())
							.put(userGroup != null ? userGroup : "Sem grupo", count.intValue());
				}

			} else if (question.getQuestionType().equalsIgnoreCase("NUMBER")
					|| question.getQuestionType().equalsIgnoreCase("RATING")) {

				// Get text-based counts (numbers/ratings stored as text) with group breakdown
				List<Object[]> textCounts = userResponseRepository.countResponsesByText(question.getId());
				List<Object[]> textGroupCounts = userResponseRepository.countResponsesByTextAndGroup(question.getId());

				// Process overall counts
				for (Object[] row : textCounts) {
					String responseText = (String) row[0];
					Long count = (Long) row[1];
					answerCounts.put(responseText, count.intValue());
					textAnswers.add(responseText);
				}

				// Process group-based counts
				for (Object[] row : textGroupCounts) {
					String responseText = (String) row[0];
					String userGroup = (String) row[1];
					Long count = (Long) row[2];

					answerCountsByGroup.computeIfAbsent(responseText, k -> new LinkedHashMap<>())
							.put(userGroup != null ? userGroup : "Sem grupo", count.intValue());
				}

			} else if (question.getQuestionType().equalsIgnoreCase("TEXT")
					|| question.getQuestionType().equalsIgnoreCase("TEXTAREA")) {

				// For text questions, get both unique responses and their counts
				List<Object[]> textCounts = userResponseRepository.countResponsesByText(question.getId());
				List<Object[]> textGroupCounts = userResponseRepository.countResponsesByTextAndGroup(question.getId());

				// Process overall counts and populate both answerCounts and textAnswers
				for (Object[] row : textCounts) {
					String responseText = (String) row[0];
					Long count = (Long) row[1];
					answerCounts.put(responseText, count.intValue());
					textAnswers.add(responseText);
				}

				// Process group-based counts for text answers
				for (Object[] row : textGroupCounts) {
					String responseText = (String) row[0];
					String userGroup = (String) row[1];
					Long count = (Long) row[2];

					answerCountsByGroup.computeIfAbsent(responseText, k -> new LinkedHashMap<>())
							.put(userGroup != null ? userGroup : "Sem grupo", count.intValue());
				}
			}

			// Convert to FormStatisticsDTO.QuestionStatisticsDTO
			FormStatisticsDTO.QuestionStatisticsDTO qStats = new FormStatisticsDTO.QuestionStatisticsDTO();
			qStats.setQuestionId(question.getId());
			qStats.setQuestionText(question.getQuestionText());
			qStats.setQuestionType(question.getQuestionType());
			qStats.setAnswerCounts(answerCounts);
			qStats.setAnswerCountsByGroup(answerCountsByGroup);
			qStats.setTextAnswers(textAnswers);
			questionsAnalytics.add(qStats);
		}

		return new FormStatisticsDTO(form.getId(), form.getTitle(), totalResponses, questionsAnalytics);
	}
}
