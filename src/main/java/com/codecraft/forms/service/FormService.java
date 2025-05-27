package com.codecraft.forms.service;

import com.codecraft.forms.dto.FormAnalyticsResponse;
import com.codecraft.forms.dto.FormDetailsResponse;
import com.codecraft.forms.dto.FormSubmissionRequest;
import com.codecraft.forms.entity.*;
import com.codecraft.forms.repository.FormAnswerRepository;
import com.codecraft.forms.repository.FormRepository;
import com.codecraft.forms.repository.FormResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormService {

	@Autowired
	private FormRepository formRepository;

	@Autowired
	private FormResponseRepository formResponseRepository;

	@Autowired
	private FormAnswerRepository formAnswerRepository;

	public List<Form> getAllActiveForms() {
		return formRepository.findByIsActiveTrue();
	}

	public Optional<FormDetailsResponse> getFormDetails(Long formId) {
		Optional<Form> formOpt = formRepository.findByIdWithQuestions(formId);

		if (formOpt.isEmpty()) {
			return Optional.empty();
		}

		Form form = formOpt.get();
		List<FormDetailsResponse.QuestionResponse> questions = form.getQuestions().stream()
				.map(q -> new FormDetailsResponse.QuestionResponse(
						q.getId(),
						q.getQuestionText(),
						q.getType().toString(),
						q.getOrder(),
						q.getIsRequired(),
						q.getOptions()))
				.collect(Collectors.toList());

		return Optional.of(new FormDetailsResponse(
				form.getId(),
				form.getTitle(),
				form.getDescription(),
				form.getCreatedAt(),
				form.getIsActive(),
				questions));
	}

	@Transactional
	public void submitFormResponse(FormSubmissionRequest request, String username) {
		// Verificar se o formulário existe e está ativo
		Form form = formRepository.findByIdAndIsActiveTrue(request.getFormId())
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado ou inativo"));

		// Verificar se o usuário já respondeu este formulário
		Optional<FormResponse> existingResponse = formResponseRepository
				.findByFormIdAndUsername(request.getFormId(), username);

		if (existingResponse.isPresent()) {
			throw new RuntimeException("Usuário já respondeu este formulário");
		}

		// Criar nova resposta
		FormResponse formResponse = new FormResponse(form, username);
		formResponse = formResponseRepository.save(formResponse);

		// Criar as respostas individuais
		List<FormAnswer> answers = new ArrayList<>();
		for (FormSubmissionRequest.AnswerRequest answerReq : request.getAnswers()) {
			FormQuestion question = form.getQuestions().stream()
					.filter(q -> q.getId().equals(answerReq.getQuestionId()))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Pergunta não encontrada: " + answerReq.getQuestionId()));

			FormAnswer answer = new FormAnswer();
			answer.setFormResponse(formResponse);
			answer.setFormQuestion(question);

			if (answerReq.getAnswerText() != null) {
				answer.setAnswerText(answerReq.getAnswerText());
			}
			if (answerReq.getAnswerNumber() != null) {
				answer.setAnswerNumber(answerReq.getAnswerNumber());
			}

			answers.add(answer);
		}

		formAnswerRepository.saveAll(answers);
	}

	public boolean hasUserRespondedForm(Long formId, String username) {
		return formResponseRepository.findByFormIdAndUsername(formId, username).isPresent();
	}

	public FormAnalyticsResponse getFormAnalytics(Long formId) {
		Form form = formRepository.findByIdWithQuestions(formId)
				.orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

		List<FormResponse> responses = formResponseRepository.findByFormIdWithAnswers(formId);

		FormAnalyticsResponse analytics = new FormAnalyticsResponse();
		analytics.setFormId(form.getId());
		analytics.setFormTitle(form.getTitle());
		analytics.setTotalResponses(responses.size());

		if (!responses.isEmpty()) {
			analytics.setFirstResponse(responses.stream()
					.map(FormResponse::getSubmittedAt)
					.min(LocalDateTime::compareTo)
					.orElse(null));

			analytics.setLastResponse(responses.stream()
					.map(FormResponse::getSubmittedAt)
					.max(LocalDateTime::compareTo)
					.orElse(null));
		}

		// Análise por pergunta
		List<FormAnalyticsResponse.QuestionAnalytics> questionAnalytics = form.getQuestions().stream()
				.map(question -> analyzeQuestion(question, responses))
				.collect(Collectors.toList());

		analytics.setQuestionAnalytics(questionAnalytics);
		return analytics;
	}

	private FormAnalyticsResponse.QuestionAnalytics analyzeQuestion(FormQuestion question,
			List<FormResponse> responses) {
		FormAnalyticsResponse.QuestionAnalytics questionAnalytics = new FormAnalyticsResponse.QuestionAnalytics();
		questionAnalytics.setQuestionId(question.getId());
		questionAnalytics.setQuestionText(question.getQuestionText());
		questionAnalytics.setQuestionType(question.getType().toString());

		List<FormAnswer> answers = responses.stream()
				.flatMap(response -> response.getAnswers().stream())
				.filter(answer -> answer.getFormQuestion().getId().equals(question.getId()))
				.collect(Collectors.toList());

		questionAnalytics.setTotalAnswers(answers.size());

		switch (question.getType()) {
			case RATING:
			case NUMBER:
				List<Integer> numericAnswers = answers.stream()
						.map(FormAnswer::getAnswerNumber)
						.filter(Objects::nonNull)
						.collect(Collectors.toList());

				if (!numericAnswers.isEmpty()) {
					double average = numericAnswers.stream()
							.mapToInt(Integer::intValue)
							.average()
							.orElse(0.0);
					questionAnalytics.setAverageRating(average);
				}
				break;

			case SINGLE_CHOICE:
			case MULTIPLE_CHOICE:
				Map<String, Integer> answerCounts = answers.stream()
						.map(FormAnswer::getAnswerText)
						.filter(Objects::nonNull)
						.collect(Collectors.groupingBy(
								text -> text,
								Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)));
				questionAnalytics.setAnswerCounts(answerCounts);
				break;

			case TEXT:
			case TEXTAREA:
			case EMAIL:
			case PHONE:
			case DATE:
				List<String> textAnswers = answers.stream()
						.map(FormAnswer::getAnswerText)
						.filter(Objects::nonNull)
						.limit(50) // Limitar para não sobrecarregar a resposta
						.collect(Collectors.toList());
				questionAnalytics.setTextAnswers(textAnswers);
				break;
		}

		return questionAnalytics;
	}
}
