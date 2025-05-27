package com.codecraft.forms.repository;

import com.codecraft.forms.entity.FormResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormResponseRepository extends JpaRepository<FormResponse, Long> {

	List<FormResponse> findByFormId(Long formId);

	List<FormResponse> findByUsername(String username);

	Optional<FormResponse> findByFormIdAndUsername(Long formId, String username);

	@Query("SELECT fr FROM FormResponse fr LEFT JOIN FETCH fr.answers a LEFT JOIN FETCH a.formQuestion WHERE fr.form.id = :formId")
	List<FormResponse> findByFormIdWithAnswers(@Param("formId") Long formId);

	@Query("SELECT COUNT(fr) FROM FormResponse fr WHERE fr.form.id = :formId")
	Long countByFormId(@Param("formId") Long formId);
}
