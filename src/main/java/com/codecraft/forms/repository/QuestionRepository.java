package com.codecraft.forms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecraft.forms.entity.Form;
import com.codecraft.forms.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
	List<Question> findByFormOrderByQuestionOrder(Form form);

	List<Question> findByFormId(Long formId);
}
