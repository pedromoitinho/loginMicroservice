package com.codecraft.forms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codecraft.forms.entity.Form;
import com.codecraft.forms.entity.UserResponse;

@Repository
public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
	List<UserResponse> findByForm(Form form);

	List<UserResponse> findByFormAndUserIdentifier(Form form, String userIdentifier);

	Long countByForm(Form form);

	@Query("SELECT ur.selectedOption.text, COUNT(ur) FROM UserResponse ur WHERE ur.question.id = :questionId AND ur.selectedOption IS NOT NULL GROUP BY ur.selectedOption.text")
	List<Object[]> countResponsesByOption(Long questionId);

	@Query("SELECT ur.responseText, COUNT(ur) FROM UserResponse ur WHERE ur.question.id = :questionId AND ur.responseText IS NOT NULL GROUP BY ur.responseText")
	List<Object[]> countResponsesByText(@Param("questionId") Long questionId);

	// New queries for group-based analytics - Include null userGroup as "Sem Grupo"
	@Query("SELECT ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.question.id = :questionId AND ur.selectedOption IS NOT NULL GROUP BY ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countResponsesByOptionAndGroup(@Param("questionId") Long questionId);

	@Query("SELECT ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.question.id = :questionId AND ur.responseText IS NOT NULL GROUP BY ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countResponsesByTextAndGroup(@Param("questionId") Long questionId);

	@Query("SELECT COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(DISTINCT ur.userIdentifier) FROM UserResponse ur WHERE ur.form.id = :formId GROUP BY COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countRespondentsByGroup(@Param("formId") Long formId);

	// New query to get all responses for a form with user group info - Include null
	// userGroup as "Sem Grupo"
	@Query("SELECT ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL GROUP BY ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByOptionAndGroup(@Param("formId") Long formId);

	// New queries for numeric and text responses by form and group - Include null
	// userGroup as "Sem Grupo"
	@Query("SELECT ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL GROUP BY ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByTextAndGroup(@Param("formId") Long formId);

	@Query("SELECT ur.question.id, ur.responseNumber, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseNumber IS NOT NULL GROUP BY ur.question.id, ur.responseNumber, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByNumberAndGroup(@Param("formId") Long formId);
}
