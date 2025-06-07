package com.codecraft.forms.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

			// Set the appropriate response based on question type
			if (responseDTO.getSelectedOptionId() != null) {
				QuestionOption option = optionRepository.findById(responseDTO.getSelectedOptionId())
						.orElseThrow(() -> new Exception("Option not found"));
				response.setSelectedOption(option);
			} else if (responseDTO.getResponseText() != null) {
				response.setResponseText(responseDTO.getResponseText());
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
				case MULTIPLE_CHOICE, CHECKBOX, RADIO -> {
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
}