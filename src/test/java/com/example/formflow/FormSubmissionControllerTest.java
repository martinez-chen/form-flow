package com.example.formflow;

import com.example.formflow.controller.FormSubmissionController;
import com.example.formflow.model.FormSubmission;
import com.example.formflow.repository.FormSubmissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FormSubmissionController.class)
public class FormSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormSubmissionRepository formSubmissionRepository;

    @Test
    public void testSubmitForm() throws Exception {
        FormSubmission submission = new FormSubmission();
        submission.setId(1L);
        submission.setFormId(1L);
        submission.setDataJson("{\"name\":\"John Doe\"}");

        when(formSubmissionRepository.save(any(FormSubmission.class))).thenReturn(submission);

        mockMvc.perform(post("/api/forms/1/submit")
                .contentType("application/json")
                .content("{\"dataJson\":\"{\\\"name\\\":\\\"John Doe\\\"}\"}"))
                .andExpect(status().isOk());
    }
}
