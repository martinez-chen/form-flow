package com.example.formflow;

import com.example.formflow.controller.TaskController;
import com.example.formflow.model.FormSubmission;
import com.example.formflow.model.SubmissionStatus;
import com.example.formflow.repository.FormSubmissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormSubmissionRepository formSubmissionRepository;

    @Test
    public void testGetTasks() throws Exception {
        FormSubmission submission = new FormSubmission();
        submission.setId(1L);
        submission.setFormId(1L);
        submission.setDataJson("{\"name\":\"John Doe\"}");
        submission.setStatus(SubmissionStatus.PENDING_REVIEW);
        submission.setCurrentReviewerId(1L);

        when(formSubmissionRepository.findByCurrentReviewerId(1)) 
                .thenReturn(Collections.singletonList(submission));

        mockMvc.perform(get("/api/tasks?reviewerId=1"))
                .andExpect(status().isOk());
    }
}
