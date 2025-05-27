package com.codecraft.forms.repository;

import com.codecraft.forms.entity.FormQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormQuestionRepository extends JpaRepository<FormQuestion, Long> {
    
    List<FormQuestion> findByFormIdOrderByOrderAsc(Long formId);
    
    void deleteByFormId(Long formId);
}
