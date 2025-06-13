package com.codecraft.forms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecraft.forms.entity.Question;
import com.codecraft.forms.entity.QuestionOption;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
	List<QuestionOption> findByQuestionOrderByOptionOrder(Question question);

	List<QuestionOption> findByQuestionId(Long questionId);

	QuestionOption findByQuestionIdAndText(Long questionId, String text);
}
