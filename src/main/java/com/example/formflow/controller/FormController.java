package com.example.formflow.controller;

import com.example.formflow.model.Form;
import com.example.formflow.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormRepository formRepository;

    @GetMapping("/{formId}")
    public Form getForm(@PathVariable Long formId) {
        return formRepository.findById(formId).orElse(null);
    }

    @PostMapping
    public Form createForm(@RequestBody Form form) {
        return formRepository.save(form);
    }

    @PutMapping("/{formId}")
    public Form updateForm(@PathVariable Long formId, @RequestBody Form formDetails) {
        Form form = formRepository.findById(formId).orElse(null);
        if (form != null) {
            form.setName(formDetails.getName());
            form.setFieldsJson(formDetails.getFieldsJson());
            return formRepository.save(form);
        }
        return null;
    }
}
