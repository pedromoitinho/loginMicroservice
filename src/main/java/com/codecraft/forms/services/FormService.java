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
		User user = userRepository.findByUsername(username);

		Form form = new Form();
		form.setTitle(createFormDTO.getTitle());
		form.setDescription(createFormDTO.getDescription());
		form.setCreatedBy(user);
		form.setAllowedGroups(createFormDTO.getAllowedGroups());

		for (CreateQuestionDTO questionDTO : createFormDTO.getQuestions()) {
			Question question = new Question();
			question.setQuestionText(questionDTO.getQuestionText());
			question.setType(questionDTO.getType());
			question.setQuestionOrder(questionDTO.getQuestionOrder());
			question.setForm(form);

			if (questionDTO.getOptions() != null) {
				for (int i = 0; i < questionDTO.getOptions().size(); i++) {
					QuestionOption option = new QuestionOption();
					option.setText(questionDTO.getOptions().get(i));
					option.setOptionOrder(i);
					option.setQuestion(question);
					question.getOptions().add(option);
				}
			}
			form.getQuestions().add(question);
		}

		Form savedForm = formRepository.save(form);
		return convertToDTO(savedForm);
	}

	private FormDTO convertToDTO(Form form) {
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
			List<QuestionDTO> questionDTOs = form.getQuestions().stream()
					.sorted(Comparator.comparing(Question::getQuestionOrder)).map(question -> {
						QuestionDTO qDto = new QuestionDTO(question.getId(), question.getQuestionText(),
								question.getType().name(), question.getQuestionOrder());
						if (question.getOptions() != null) {
							List<QuestionOptionDTO> optionDTOs = question.getOptions().stream()
									.sorted(Comparator.comparing(QuestionOption::getOptionOrder))
									.map(opt -> new QuestionOptionDTO(opt.getId(), opt.getText(), opt.getOptionOrder()))
									.collect(Collectors.toList());
							qDto.setOptions(optionDTOs);
						}
						return qDto;
					}).collect(Collectors.toList());
			dto.setQuestions(questionDTOs);
		} else {
			dto.setQuestions(Collections.emptyList());
		}

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
		Form form = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));
		return convertToDTO(form);
	}

	// Obter todos os formulários (admin only)
	public List<FormDTO> getAllForms() {
		List<Form> forms = formRepository.findAllByOrderByCreatedAtDesc();
		return forms.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Obter detalhes do formulário (protegido)
	public FormDTO getFormDetails(Long formId, String username) {
		Form form = formRepository.findById(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

		// Verificar se o usuário é o dono do formulário ou admin
		if (!"admin".equals(username)
				&& (form.getCreatedBy() == null || !form.getCreatedBy().getUsername().equals(username))) {
			throw new RuntimeException("Acesso negado");
		}

		return convertToDTO(form);
	}

	// Atualizar formulário
	public FormDTO updateForm(Long formId, UpdateFormDTO updateFormDTO, String username) {
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

		Form savedForm = formRepository.save(form);
		return convertToDTO(savedForm);
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

				// For text questions, just get all unique responses
				List<Object[]> textCounts = userResponseRepository.countResponsesByText(question.getId());
				List<Object[]> textGroupCounts = userResponseRepository.countResponsesByTextAndGroup(question.getId());

				// Add all text responses
				for (Object[] row : textCounts) {
					String responseText = (String) row[0];
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
