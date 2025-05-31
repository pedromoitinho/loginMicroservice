package com.codecraft.forms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.codecraft.auth.entity.User;
import com.codecraft.forms.entity.Form;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
	List<Form> findByCreatedBy(User user);

	List<Form> findByIsActiveTrueOrderByCreatedAtDesc();

	List<Form> findByCreatedByOrderByCreatedAtDesc(User user);

	List<Form> findAllByOrderByCreatedAtDesc();

	@Modifying
	@Transactional
	@Query("DELETE FROM QuestionOption qo WHERE qo.question.form.id = :formId")
	void deleteOptionsForForm(Long formId);

	@Modifying
	@Transactional
	@Query("DELETE FROM Question q WHERE q.form.id = :formId")
	void deleteQuestionsForForm(Long formId);
}