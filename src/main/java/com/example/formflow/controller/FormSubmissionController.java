package com.example.formflow.controller;

import com.example.formflow.model.FormSubmission;
import com.example.formflow.repository.FormSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms/{formId}/submit")
@RequiredArgsConstructor
public class FormSubmissionController {

    private final FormSubmissionRepository formSubmissionRepository;

    @PostMapping
    public FormSubmission submitForm(@PathVariable Long formId, @RequestBody FormSubmission submission) {
        submission.setFormId(formId);
        return formSubmissionRepository.save(submission);
    }
}
