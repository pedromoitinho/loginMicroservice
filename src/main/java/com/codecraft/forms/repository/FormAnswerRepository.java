package com.codecraft.forms.repository;

import com.codecraft.forms.entity.FormAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormAnswerRepository extends JpaRepository<FormAnswer, Long> {

	List<FormAnswer> findByFormResponseId(Long formResponseId);

	List<FormAnswer> findByFormQuestionId(Long formQuestionId);

	@Query("SELECT fa FROM FormAnswer fa WHERE fa.formQuestion.id = :questionId AND fa.answerNumber IS NOT NULL")
	List<FormAnswer> findNumericAnswersByQuestionId(@Param("questionId") Long questionId);

	@Query("SELECT fa.answerText, COUNT(fa) FROM FormAnswer fa WHERE fa.formQuestion.id = :questionId AND fa.answerText IS NOT NULL GROUP BY fa.answerText")
	List<Object[]> countAnswersByQuestionId(@Param("questionId") Long questionId);
}
