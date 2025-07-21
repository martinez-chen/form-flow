package com.example.formflow.repository;

import com.example.formflow.model.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormSubmissionRepository extends JpaRepository<FormSubmission, Long> {

    List<FormSubmission> findByCurrentReviewerId(Long reviewerId);
}
