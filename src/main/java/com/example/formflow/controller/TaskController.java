package com.example.formflow.controller;

import com.example.formflow.model.FormSubmission;
import com.example.formflow.repository.FormSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final FormSubmissionRepository formSubmissionRepository;

    @GetMapping
    public List<FormSubmission> getTasks(@RequestParam Long reviewerId) {
        return formSubmissionRepository.findByCurrentReviewerId(reviewerId);
    }
}
