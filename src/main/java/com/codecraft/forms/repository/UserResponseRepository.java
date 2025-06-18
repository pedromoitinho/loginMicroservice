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

	// New queries for sector-based analytics - Include null userSetor as "Não
	// informado"
	@Query("SELECT DISTINCT COALESCE(ur.userSetor, 'Não informado') FROM UserResponse ur WHERE ur.form.id = :formId ORDER BY COALESCE(ur.userSetor, 'Não informado')")
	List<String> findDistinctSetorsByForm(@Param("formId") Long formId);

	// New queries for empresa-based analytics - Include null userEmpresa as "Não
	// informado"
	@Query("SELECT DISTINCT COALESCE(ur.userEmpresa, 'Não informado') FROM UserResponse ur WHERE ur.form.id = :formId ORDER BY COALESCE(ur.userEmpresa, 'Não informado')")
	List<String> findDistinctEmpresasByForm(@Param("formId") Long formId);

	// Sector-filtered analytics queries
	@Query("SELECT ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL AND (:setor IS NULL OR COALESCE(ur.userSetor, 'Não informado') = :setor) GROUP BY ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByOptionAndGroupFiltered(@Param("formId") Long formId,
			@Param("setor") String setor);

	@Query("SELECT ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL AND (:setor IS NULL OR COALESCE(ur.userSetor, 'Não informado') = :setor) GROUP BY ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByTextAndGroupFiltered(@Param("formId") Long formId, @Param("setor") String setor);

	@Query("SELECT ur.question.id, ur.responseNumber, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseNumber IS NOT NULL AND (:setor IS NULL OR COALESCE(ur.userSetor, 'Não informado') = :setor) GROUP BY ur.question.id, ur.responseNumber, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByNumberAndGroupFiltered(@Param("formId") Long formId,
			@Param("setor") String setor);

	// Count total responses filtered by sector
	@Query("SELECT COUNT(DISTINCT ur.userIdentifier) FROM UserResponse ur WHERE ur.form.id = :formId AND (:setor IS NULL OR COALESCE(ur.userSetor, 'Não informado') = :setor)")
	Long countByFormFiltered(@Param("formId") Long formId, @Param("setor") String setor);

	// Bulk queries for sector filtering (for performance)
	@Query("SELECT ur.question.id, ur.selectedOption.text, COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL AND (:setor IS NULL OR COALESCE(ur.userSetor, 'Não informado') = :setor) GROUP BY ur.question.id, ur.selectedOption.text")
	List<Object[]> bulkCountResponsesByOptionFiltered(@Param("formId") Long formId, @Param("setor") String setor);

	@Query("SELECT ur.question.id, ur.responseText, COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL AND (:setor IS NULL OR COALESCE(ur.userSetor, 'Não informado') = :setor) GROUP BY ur.question.id, ur.responseText")
	List<Object[]> bulkCountResponsesByTextFiltered(@Param("formId") Long formId, @Param("setor") String setor);

	// Empresa-filtered analytics queries
	@Query("SELECT ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByOptionAndGroupFilteredByEmpresa(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	@Query("SELECT ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByTextAndGroupFilteredByEmpresa(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	@Query("SELECT ur.question.id, ur.responseNumber, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseNumber IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.responseNumber, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> countFormResponsesByNumberAndGroupFilteredByEmpresa(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	// Count total responses filtered by empresa
	@Query("SELECT COUNT(DISTINCT ur.userIdentifier) FROM UserResponse ur WHERE ur.form.id = :formId AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa)")
	Long countByFormFilteredByEmpresa(@Param("formId") Long formId, @Param("empresa") String empresa);

	// Bulk queries for empresa filtering (for performance)
	@Query("SELECT ur.question.id, ur.selectedOption.text, COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.selectedOption.text")
	List<Object[]> bulkCountResponsesByOptionFilteredByEmpresa(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	@Query("SELECT ur.question.id, ur.responseText, COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.responseText")
	List<Object[]> bulkCountResponsesByTextFilteredByEmpresa(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	@Query("SELECT ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> bulkCountResponsesByOptionAndGroupFilteredByEmpresa2(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	@Query("SELECT ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL AND (:empresa IS NULL OR COALESCE(ur.userEmpresa, 'Não informado') = :empresa) GROUP BY ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> bulkCountResponsesByTextAndGroupFilteredByEmpresa2(@Param("formId") Long formId,
			@Param("empresa") String empresa);

	// Bulk queries for all responses without filtering (needed by FormService)
	@Query("SELECT ur.question.id, ur.selectedOption.text, COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL GROUP BY ur.question.id, ur.selectedOption.text")
	List<Object[]> bulkCountResponsesByOption(@Param("formId") Long formId);

	@Query("SELECT ur.question.id, ur.responseText, COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL GROUP BY ur.question.id, ur.responseText")
	List<Object[]> bulkCountResponsesByText(@Param("formId") Long formId);

	@Query("SELECT ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.selectedOption IS NOT NULL GROUP BY ur.question.id, ur.selectedOption.text, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> bulkCountResponsesByOptionAndGroup(@Param("formId") Long formId);

	@Query("SELECT ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo'), COUNT(ur) FROM UserResponse ur WHERE ur.form.id = :formId AND ur.responseText IS NOT NULL GROUP BY ur.question.id, ur.responseText, COALESCE(ur.userGroup, 'Sem Grupo')")
	List<Object[]> bulkCountResponsesByTextAndGroup(@Param("formId") Long formId);
}
