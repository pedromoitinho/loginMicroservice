package com.codecraft.forms.repository;

import com.codecraft.forms.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

	List<Form> findByIsActiveTrue();

	Optional<Form> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT f FROM Form f LEFT JOIN FETCH f.questions q WHERE f.id = :id AND f.isActive = true ORDER BY q.order")
	Optional<Form> findByIdWithQuestions(Long id);
}
