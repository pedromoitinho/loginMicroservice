package com.codecraft.forms.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecraft.auth.entity.User;
import com.codecraft.auth.repository.UserRepository;
import com.codecraft.forms.dto.FormStatisticsDTO;
import com.codecraft.forms.dto.QuestionResponseDTO;
import com.codecraft.forms.dto.SubmitResponseDTO;
import com.codecraft.forms.entity.Form;
import com.codecraft.forms.entity.Question;
import com.codecraft.forms.entity.QuestionOption;
import com.codecraft.forms.entity.UserResponse;
import com.codecraft.forms.repository.FormRepository;
import com.codecraft.forms.repository.QuestionOptionRepository;
import com.codecraft.forms.repository.QuestionRepository;
import com.codecraft.forms.repository.UserResponseRepository;

@Service
public class ResponseService {

	@Autowired
	private FormRepository formRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionOptionRepository optionRepository;

	@Autowired
	private UserResponseRepository userResponseRepository;

	public void submitResponses(SubmitResponseDTO submitDTO) throws Exception {
		Form form = formRepository.findById(submitDTO.getFormId()).orElseThrow(() -> new Exception("Form not found"));

		if (!form.getIsActive()) {
			throw new Exception("This form is no longer active");
		}

		// Get user setor and empresa information if userIdentifier is provided
		String userSetor = "Não informado";
		String userEmpresa = "Não informado";
		if (submitDTO.getUserIdentifier() != null && !submitDTO.getUserIdentifier().trim().isEmpty()) {
			User user = userRepository.findByUsername(submitDTO.getUserIdentifier());
			if (user != null) {
				if (user.getSetor() != null && !user.getSetor().trim().isEmpty()) {
					userSetor = user.getSetor();
				}
				if (user.getUserGroup() != null && !user.getUserGroup().trim().isEmpty()) {
					userEmpresa = user.getUserGroup(); // Using userGroup as empresa for now
				}
			}
		}

		// Use empresa from DTO if provided, otherwise use empresa from user data
		if (submitDTO.getUserEmpresa() != null && !submitDTO.getUserEmpresa().trim().isEmpty()) {
			userEmpresa = submitDTO.getUserEmpresa();
		}

		// Process each response
		for (QuestionResponseDTO responseDTO : submitDTO.getResponses()) {
			Question question = questionRepository.findById(responseDTO.getQuestionId())
					.orElseThrow(() -> new Exception("Question not found"));

			UserResponse response = new UserResponse();
			response.setForm(form);
			response.setQuestion(question);
			response.setUserIdentifier(submitDTO.getUserIdentifier());
			// Ensure userGroup is always set (default to 'Sem Grupo' if null or empty)
			String userGroup = submitDTO.getUserGroup();
			if (userGroup == null || userGroup.trim().isEmpty()) {
				userGroup = "Sem Grupo";
			}
			response.setUserGroup(userGroup);
			response.setUserSetor(userSetor);
			response.setUserEmpresa(userEmpresa);

			// Set the appropriate response based on question type
			if (responseDTO.getSelectedOptionId() != null) {
				QuestionOption option = optionRepository.findById(responseDTO.getSelectedOptionId())
						.orElseThrow(() -> new Exception("Option not found"));
				response.setSelectedOption(option);
			} else if (responseDTO.getResponseText() != null) {
				// For RADIO and MULTIPLE_CHOICE questions, try to convert text back to option
				// ID
				if (question.getType() == com.codecraft.forms.type.QuestionType.RADIO ||
						question.getType() == com.codecraft.forms.type.QuestionType.MULTIPLE_CHOICE ||
						question.getType() == com.codecraft.forms.type.QuestionType.CHECKBOX ||
						"RADIO".equalsIgnoreCase(question.getQuestionType()) ||
						"MULTIPLE_CHOICE".equalsIgnoreCase(question.getQuestionType()) ||
						"CHECKBOX".equalsIgnoreCase(question.getQuestionType())) {

					// Handle multiple choice (comma-separated values)
					String[] optionTexts = responseDTO.getResponseText().split(",");
					for (String optionText : optionTexts) {
						optionText = optionText.trim();
						if (!optionText.isEmpty()) {
							// Find the option by text
							QuestionOption option = optionRepository.findByQuestionIdAndText(question.getId(), optionText);
							if (option != null) {
								// Create a separate response for each selected option (for multiple choice)
								UserResponse optionResponse = new UserResponse();
								optionResponse.setForm(form);
								optionResponse.setQuestion(question);
								optionResponse.setUserIdentifier(submitDTO.getUserIdentifier());
								optionResponse.setUserGroup(userGroup);
								optionResponse.setUserSetor(userSetor);
								optionResponse.setSelectedOption(option);
								userResponseRepository.save(optionResponse);
							}
						}
					}
					// Skip the save at the end for option-based questions since we saved individual
					// responses
					continue;
				} else {
					// For text-based questions, save as responseText
					response.setResponseText(responseDTO.getResponseText());
				}
			} else if (responseDTO.getResponseNumber() != null) {
				response.setResponseNumber(responseDTO.getResponseNumber());
			}

			userResponseRepository.save(response);
		}
	}

	public FormStatisticsDTO getFormStatistics(Long formId, String username) throws Exception {
		Form form = formRepository.findById(formId).orElseThrow(() -> new Exception("Form not found"));

		// Check if user has permission to view stats (form creator or admin)
		// Fix: compare username with form.getCreatedBy().getUsername() instead of
		// .equals(User)
		if (!form.getCreatedBy().getUsername().equals(username) && !"admin".equals(username)) {
			throw new Exception("You don't have permission to view this form's statistics");
		}

		FormStatisticsDTO statistics = new FormStatisticsDTO();
		statistics.setFormId(formId);
		statistics.setFormTitle(form.getTitle()); // Set the form title

		// Get total unique respondents count
		Integer totalResponses = userResponseRepository.countByForm(form).intValue();
		statistics.setTotalResponses(totalResponses);

		// Removed unused respondentsByGroup variable and related code

		// Use correct DTO type
		List<FormStatisticsDTO.QuestionStatisticsDTO> questionStatsList = new ArrayList<>();
		List<Question> questions = questionRepository.findByFormId(formId);

		// Get all response counts with group data for the entire form
		List<Object[]> formOptionGroupCounts = userResponseRepository.countFormResponsesByOptionAndGroup(formId);
		List<Object[]> formTextGroupCounts = userResponseRepository.countFormResponsesByTextAndGroup(formId);
		List<Object[]> formNumberGroupCounts = userResponseRepository.countFormResponsesByNumberAndGroup(formId);
		Map<Long, Map<String, Map<String, Integer>>> questionOptionGroupCountsMap = new HashMap<>();

		// Process option-based results (MULTIPLE_CHOICE, CHECKBOX, RADIO)
		for (Object[] count : formOptionGroupCounts) {
			Long questionId = (Long) count[0];
			String optionText = count[1] != null ? count[1].toString() : "Sem Resposta";
			String group = count[2] != null ? count[2].toString() : "Sem Grupo";
			Integer countValue = ((Number) count[3]).intValue();

			// Initialize nested maps if needed
			questionOptionGroupCountsMap.putIfAbsent(questionId, new HashMap<>());
			Map<String, Map<String, Integer>> optionGroupMap = questionOptionGroupCountsMap.get(questionId);
			optionGroupMap.putIfAbsent(optionText, new HashMap<>());
			optionGroupMap.get(optionText).put(group, countValue);
		}

		// Process text-based results (TEXT, TEXTAREA)
		for (Object[] count : formTextGroupCounts) {
			Long questionId = (Long) count[0];
			String responseText = count[1] != null ? count[1].toString() : "Sem Resposta";
			String group = count[2] != null ? count[2].toString() : "Sem Grupo";
			Integer countValue = ((Number) count[3]).intValue();

			// Initialize nested maps if needed
			questionOptionGroupCountsMap.putIfAbsent(questionId, new HashMap<>());
			Map<String, Map<String, Integer>> optionGroupMap = questionOptionGroupCountsMap.get(questionId);
			optionGroupMap.putIfAbsent(responseText, new HashMap<>());
			optionGroupMap.get(responseText).put(group, countValue);
		}

		// Process numeric-based results (NUMBER, RATING)
		for (Object[] count : formNumberGroupCounts) {
			Long questionId = (Long) count[0];
			String responseNumber = count[1] != null ? count[1].toString() : "Sem Resposta";
			String group = count[2] != null ? count[2].toString() : "Sem Grupo";
			Integer countValue = ((Number) count[3]).intValue();

			// Initialize nested maps if needed
			questionOptionGroupCountsMap.putIfAbsent(questionId, new HashMap<>());
			Map<String, Map<String, Integer>> optionGroupMap = questionOptionGroupCountsMap.get(questionId);
			optionGroupMap.putIfAbsent(responseNumber, new HashMap<>());
			optionGroupMap.get(responseNumber).put(group, countValue);
		}

		// Process each question
		for (Question question : questions) {
			FormStatisticsDTO.QuestionStatisticsDTO qStats = new FormStatisticsDTO.QuestionStatisticsDTO();
			qStats.setQuestionId(question.getId());
			qStats.setQuestionText(question.getQuestionText());
			qStats.setQuestionType(question.getType().toString());

			// Process based on question type
			switch (question.getType()) {
				case MULTIPLE_CHOICE, CHECKBOX, RADIO, EMAIL, DATE -> {
					// For EMAIL, DATE - handle same as RADIO if they're one of the choice types
					if (question.getType() == com.codecraft.forms.type.QuestionType.EMAIL ||
							question.getType() == com.codecraft.forms.type.QuestionType.DATE) {
						// Just to avoid errors, check the questionType string to see if we should treat
						// it as multiple choice
						if (!"MULTIPLE_CHOICE".equalsIgnoreCase(question.getQuestionType()) &&
								!"CHECKBOX".equalsIgnoreCase(question.getQuestionType()) &&
								!"RADIO".equalsIgnoreCase(question.getQuestionType())) {
							// Handle as text-based question later
							break;
						}
					}
					// Get all options for this question to preserve order
					@SuppressWarnings("unused")
					List<QuestionOption> allOptions = optionRepository.findByQuestionId(question.getId());

					// Get option counts aggregated by response (not grouped by userGroup)
					List<Object[]> optionCounts = userResponseRepository.countResponsesByOption(question.getId());
					Map<String, Integer> countMap = new HashMap<>();

					// Populate count map
					for (Object[] count : optionCounts) {
						String option = count[0] != null ? count[0].toString() : "Sem Resposta";
						Integer countValue = ((Number) count[1]).intValue();
						countMap.put(option, countValue);
					}

					// Set answerCounts as a map of option labels to counts
					qStats.setAnswerCounts(countMap);

					// Set answerCountsByGroup for this question (for potential future group
					// analysis)
					if (questionOptionGroupCountsMap.containsKey(question.getId())) {
						qStats.setAnswerCountsByGroup(questionOptionGroupCountsMap.get(question.getId()));
					}
				}
				case TEXT, TEXTAREA -> {
					// For text questions, get aggregated counts by response text
					List<Object[]> textCounts = userResponseRepository.countResponsesByText(question.getId());
					Map<String, Integer> countMap = new HashMap<>();

					// Populate count map for text responses
					for (Object[] count : textCounts) {
						String responseText = count[0] != null ? count[0].toString() : "Sem Resposta";
						Integer countValue = ((Number) count[1]).intValue();
						countMap.put(responseText, countValue);
					}

					// Set answerCounts for text questions to show aggregated responses
					qStats.setAnswerCounts(countMap);

					// Also collect individual text answers for fallback display
					List<UserResponse> textResponses = userResponseRepository.findByForm(form).stream()
							.filter(r -> r.getQuestion().getId().equals(question.getId()))
							.filter(r -> r.getResponseText() != null && !r.getResponseText().isEmpty())
							.collect(Collectors.toList());

					qStats.setTextAnswers(
							textResponses.stream().map(UserResponse::getResponseText).collect(Collectors.toList()));

					// For text questions, also set group breakdown by creating answerCountsByGroup
					if (questionOptionGroupCountsMap.containsKey(question.getId())) {
						qStats.setAnswerCountsByGroup(questionOptionGroupCountsMap.get(question.getId()));
					}
				}
				case NUMBER, RATING -> {
					// Calculate average for numeric responses
					List<UserResponse> numericResponses = userResponseRepository.findByForm(form).stream()
							.filter(r -> r.getQuestion().getId().equals(question.getId()))
							.filter(r -> r.getResponseNumber() != null).collect(Collectors.toList());

					if (!numericResponses.isEmpty()) {
						// Calculate average
						Double average = numericResponses.stream().mapToDouble(r -> r.getResponseNumber()).average()
								.orElse(0.0);
						qStats.setAverageRating(average);

						// Create answerCounts for each numeric value aggregated
						Map<String, Integer> countMap = new HashMap<>();
						for (UserResponse response : numericResponses) {
							String valueStr = String.valueOf(response.getResponseNumber());
							countMap.put(valueStr, countMap.getOrDefault(valueStr, 0) + 1);
						}
						qStats.setAnswerCounts(countMap);

						// Add individual responses as textAnswers for display
						List<String> answers = numericResponses.stream().map(r -> String.valueOf(r.getResponseNumber()))
								.collect(Collectors.toList());
						qStats.setTextAnswers(answers);

						// Set answerCountsByGroup for numeric questions
						if (questionOptionGroupCountsMap.containsKey(question.getId())) {
							qStats.setAnswerCountsByGroup(questionOptionGroupCountsMap.get(question.getId()));
						}
					}
				}
			}

			questionStatsList.add(qStats);
		}

		statistics.setQuestionsAnalytics(questionStatsList);
		return statistics;
	}

	// Get available sectors for a form
	public List<String> getFormSectors(Long formId, String username) throws Exception {
		Form form = formRepository.findById(formId).orElseThrow(() -> new Exception("Form not found"));

		// Check if user has permission to view stats (form creator or admin)
		if (!form.getCreatedBy().getUsername().equals(username) && !"admin".equals(username)) {
			throw new Exception("You don't have permission to view this form's statistics");
		}

		return userResponseRepository.findDistinctSetorsByForm(formId);
	}

	// Get available empresas for a form
	public List<String> getFormEmpresas(Long formId, String username) throws Exception {
		Form form = formRepository.findById(formId).orElseThrow(() -> new Exception("Form not found"));

		// Check if user has permission to view stats (form creator or admin)
		if (!form.getCreatedBy().getUsername().equals(username) && !"admin".equals(username)) {
			throw new Exception("You don't have permission to view this form's statistics");
		}

		return userResponseRepository.findDistinctEmpresasByForm(formId);
	}

	// Get form statistics filtered by sector
	public FormStatisticsDTO getFormStatisticsFiltered(Long formId, String username, String setor) throws Exception {
		Form form = formRepository.findById(formId).orElseThrow(() -> new Exception("Form not found"));

		// Check if user has permission to view stats (form creator or admin)
		if (!form.getCreatedBy().getUsername().equals(username) && !"admin".equals(username)) {
			throw new Exception("You don't have permission to view this form's statistics");
		}

		List<Question> questions = questionRepository.findByFormId(formId);

		// Get total unique respondents count filtered by sector
		Long totalResponses = userResponseRepository.countByFormFiltered(formId, setor);

		// Use optimized bulk queries to get all data at once
		List<Object[]> bulkOptionCounts = userResponseRepository.bulkCountResponsesByOptionFiltered(formId, setor);
		List<Object[]> bulkTextCounts = userResponseRepository.bulkCountResponsesByTextFiltered(formId, setor);
		List<Object[]> bulkOptionGroupCounts = userResponseRepository.countFormResponsesByOptionAndGroupFiltered(formId,
				setor);
		List<Object[]> bulkTextGroupCounts = userResponseRepository.countFormResponsesByTextAndGroupFiltered(formId,
				setor);

		// Create maps for quick lookup
		Map<Long, Map<String, Integer>> optionCountsByQuestion = new LinkedHashMap<>();
		Map<Long, Map<String, Integer>> textCountsByQuestion = new LinkedHashMap<>();
		Map<Long, Map<String, Map<String, Integer>>> optionGroupCountsByQuestion = new LinkedHashMap<>();
		Map<Long, Map<String, Map<String, Integer>>> textGroupCountsByQuestion = new LinkedHashMap<>();
		Map<Long, List<String>> textAnswersByQuestion = new LinkedHashMap<>();

		// Process bulk option counts
		for (Object[] row : bulkOptionCounts) {
			Long questionId = (Long) row[0];
			String optionText = (String) row[1];
			Long count = (Long) row[2];

			optionCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.put(optionText, count.intValue());
		}

		// Process bulk text counts
		for (Object[] row : bulkTextCounts) {
			Long questionId = (Long) row[0];
			String responseText = (String) row[1];
			Long count = (Long) row[2];

			textCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.put(responseText, count.intValue());
			textAnswersByQuestion.computeIfAbsent(questionId, k -> new ArrayList<>())
					.add(responseText);
		}

		// Process bulk option group counts
		for (Object[] row : bulkOptionGroupCounts) {
			Long questionId = (Long) row[0];
			String optionText = (String) row[1];
			String userGroup = (String) row[2];
			Long count = (Long) row[3];

			optionGroupCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.computeIfAbsent(optionText, k -> new LinkedHashMap<>())
					.put(userGroup, count.intValue());
		}

		// Process bulk text group counts
		for (Object[] row : bulkTextGroupCounts) {
			Long questionId = (Long) row[0];
			String responseText = (String) row[1];
			String userGroup = (String) row[2];
			Long count = (Long) row[3];

			textGroupCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.computeIfAbsent(responseText, k -> new LinkedHashMap<>())
					.put(userGroup, count.intValue());
		}

		List<FormStatisticsDTO.QuestionStatisticsDTO> questionStatsList = new ArrayList<>();

		// Process each question using the pre-loaded data
		for (Question question : questions) {
			FormStatisticsDTO.QuestionStatisticsDTO qStats = new FormStatisticsDTO.QuestionStatisticsDTO();
			qStats.setQuestionId(question.getId());
			qStats.setQuestionText(question.getQuestionText());
			qStats.setQuestionType(question.getType().toString());

			Map<String, Integer> answerCounts = new LinkedHashMap<>();
			Map<String, Map<String, Integer>> answerCountsByGroup = new LinkedHashMap<>();
			List<String> textAnswers = new ArrayList<>();

			// Process based on question type using pre-loaded data
			switch (question.getType()) {
				case MULTIPLE_CHOICE, CHECKBOX, RADIO, EMAIL, DATE -> {
					if (question.getType() == com.codecraft.forms.type.QuestionType.EMAIL ||
							question.getType() == com.codecraft.forms.type.QuestionType.DATE) {
						if (!"MULTIPLE_CHOICE".equalsIgnoreCase(question.getQuestionType()) &&
								!"CHECKBOX".equalsIgnoreCase(question.getQuestionType()) &&
								!"RADIO".equalsIgnoreCase(question.getQuestionType())) {
							break;
						}
					}

					// Use pre-loaded data
					answerCounts = optionCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					answerCountsByGroup = optionGroupCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
				}
				case TEXT, TEXTAREA -> {
					// Use pre-loaded data
					answerCounts = textCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					answerCountsByGroup = textGroupCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					textAnswers = textAnswersByQuestion.getOrDefault(question.getId(), new ArrayList<>());
				}
				case NUMBER, RATING -> {
					// Use pre-loaded data
					answerCounts = textCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					answerCountsByGroup = textGroupCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					textAnswers = textAnswersByQuestion.getOrDefault(question.getId(), new ArrayList<>());

					// Calculate average for numeric questions
					if (!answerCounts.isEmpty()) {
						List<Double> numericValues = new ArrayList<>();
						for (Map.Entry<String, Integer> entry : answerCounts.entrySet()) {
							try {
								double value = Double.parseDouble(entry.getKey());
								for (int i = 0; i < entry.getValue(); i++) {
									numericValues.add(value);
								}
							} catch (NumberFormatException e) {
								// Skip non-numeric values
							}
						}

						if (!numericValues.isEmpty()) {
							double average = numericValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
							qStats.setAverageRating(average);
						}
					}
				}
			}

			qStats.setAnswerCounts(answerCounts);
			qStats.setAnswerCountsByGroup(answerCountsByGroup);
			qStats.setTextAnswers(textAnswers);
			questionStatsList.add(qStats);
		}

		return new FormStatisticsDTO(form.getId(), form.getTitle(), totalResponses.intValue(), questionStatsList);
	}

	// Get form statistics filtered by empresa
	public FormStatisticsDTO getFormStatisticsFilteredByEmpresa(Long formId, String username, String empresa)
			throws Exception {
		Form form = formRepository.findById(formId).orElseThrow(() -> new Exception("Form not found"));

		// Check if user has permission to view stats (form creator or admin)
		if (!form.getCreatedBy().getUsername().equals(username) && !"admin".equals(username)) {
			throw new Exception("You don't have permission to view this form's statistics");
		}

		List<Question> questions = questionRepository.findByFormId(formId);

		// Get total unique respondents count filtered by empresa
		Long totalResponses = userResponseRepository.countByFormFilteredByEmpresa(formId, empresa);

		// Use optimized bulk queries to get all data at once
		List<Object[]> bulkOptionCounts = userResponseRepository.bulkCountResponsesByOptionFilteredByEmpresa(formId,
				empresa);
		List<Object[]> bulkTextCounts = userResponseRepository.bulkCountResponsesByTextFilteredByEmpresa(formId, empresa);
		List<Object[]> bulkOptionGroupCounts = userResponseRepository
				.bulkCountResponsesByOptionAndGroupFilteredByEmpresa2(formId,
						empresa);
		List<Object[]> bulkTextGroupCounts = userResponseRepository.bulkCountResponsesByTextAndGroupFilteredByEmpresa2(
				formId,
				empresa);

		// Create maps for quick lookup
		Map<Long, Map<String, Integer>> optionCountsByQuestion = new LinkedHashMap<>();
		Map<Long, Map<String, Integer>> textCountsByQuestion = new LinkedHashMap<>();
		Map<Long, Map<String, Map<String, Integer>>> optionGroupCountsByQuestion = new LinkedHashMap<>();
		Map<Long, Map<String, Map<String, Integer>>> textGroupCountsByQuestion = new LinkedHashMap<>();
		Map<Long, List<String>> textAnswersByQuestion = new LinkedHashMap<>();

		// Process bulk option counts
		for (Object[] row : bulkOptionCounts) {
			Long questionId = (Long) row[0];
			String optionText = (String) row[1];
			Long count = (Long) row[2];

			optionCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.put(optionText, count.intValue());
		}

		// Process bulk text counts
		for (Object[] row : bulkTextCounts) {
			Long questionId = (Long) row[0];
			String responseText = (String) row[1];
			Long count = (Long) row[2];

			textCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.put(responseText, count.intValue());
			textAnswersByQuestion.computeIfAbsent(questionId, k -> new ArrayList<>())
					.add(responseText);
		}

		// Process bulk option group counts
		for (Object[] row : bulkOptionGroupCounts) {
			Long questionId = (Long) row[0];
			String optionText = (String) row[1];
			String userGroup = (String) row[2];
			Long count = (Long) row[3];

			optionGroupCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.computeIfAbsent(optionText, k -> new LinkedHashMap<>())
					.put(userGroup, count.intValue());
		}

		// Process bulk text group counts
		for (Object[] row : bulkTextGroupCounts) {
			Long questionId = (Long) row[0];
			String responseText = (String) row[1];
			String userGroup = (String) row[2];
			Long count = (Long) row[3];

			textGroupCountsByQuestion.computeIfAbsent(questionId, k -> new LinkedHashMap<>())
					.computeIfAbsent(responseText, k -> new LinkedHashMap<>())
					.put(userGroup, count.intValue());
		}

		List<FormStatisticsDTO.QuestionStatisticsDTO> questionStatsList = new ArrayList<>();

		// Process each question using the pre-loaded data
		for (Question question : questions) {
			FormStatisticsDTO.QuestionStatisticsDTO qStats = new FormStatisticsDTO.QuestionStatisticsDTO();
			qStats.setQuestionId(question.getId());
			qStats.setQuestionText(question.getQuestionText());
			qStats.setQuestionType(question.getType().toString());

			Map<String, Integer> answerCounts = new LinkedHashMap<>();
			Map<String, Map<String, Integer>> answerCountsByGroup = new LinkedHashMap<>();
			List<String> textAnswers = new ArrayList<>();

			// Process based on question type using pre-loaded data
			switch (question.getType()) {
				case MULTIPLE_CHOICE, CHECKBOX, RADIO, EMAIL, DATE -> {
					if (question.getType() == com.codecraft.forms.type.QuestionType.EMAIL ||
							question.getType() == com.codecraft.forms.type.QuestionType.DATE) {
						if (!"MULTIPLE_CHOICE".equalsIgnoreCase(question.getQuestionType()) &&
								!"CHECKBOX".equalsIgnoreCase(question.getQuestionType()) &&
								!"RADIO".equalsIgnoreCase(question.getQuestionType())) {
							break;
						}
					}

					// Use pre-loaded data
					answerCounts = optionCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					answerCountsByGroup = optionGroupCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
				}
				case TEXT, TEXTAREA -> {
					// Use pre-loaded data
					answerCounts = textCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					answerCountsByGroup = textGroupCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					textAnswers = textAnswersByQuestion.getOrDefault(question.getId(), new ArrayList<>());
				}
				case NUMBER, RATING -> {
					// Use pre-loaded data
					answerCounts = textCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					answerCountsByGroup = textGroupCountsByQuestion.getOrDefault(question.getId(), new LinkedHashMap<>());
					textAnswers = textAnswersByQuestion.getOrDefault(question.getId(), new ArrayList<>());

					// Calculate average for numeric questions
					if (!answerCounts.isEmpty()) {
						List<Double> numericValues = new ArrayList<>();
						for (Map.Entry<String, Integer> entry : answerCounts.entrySet()) {
							try {
								double value = Double.parseDouble(entry.getKey());
								for (int i = 0; i < entry.getValue(); i++) {
									numericValues.add(value);
								}
							} catch (NumberFormatException e) {
								// Skip non-numeric values
							}
						}

						if (!numericValues.isEmpty()) {
							double average = numericValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
							qStats.setAverageRating(average);
						}
					}
				}
			}

			qStats.setAnswerCounts(answerCounts);
			qStats.setAnswerCountsByGroup(answerCountsByGroup);
			qStats.setTextAnswers(textAnswers);
			questionStatsList.add(qStats);
		}

		return new FormStatisticsDTO(form.getId(), form.getTitle(), totalResponses.intValue(), questionStatsList);
	}
}