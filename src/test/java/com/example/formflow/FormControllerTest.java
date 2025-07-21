package com.example.formflow;

import com.example.formflow.controller.FormController;
import com.example.formflow.model.Form;
import com.example.formflow.repository.FormRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FormController.class)
public class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormRepository formRepository;

    @Test
    public void testGetForm() throws Exception {
        Form form = new Form();
        form.setId(1L);
        form.setName("Test Form");
        form.setFieldsJson("[]");

        when(formRepository.findById(1L)).thenReturn(Optional.of(form));

        mockMvc.perform(get("/api/forms/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateForm() throws Exception {
        Form form = new Form();
        form.setId(1L);
        form.setName("Test Form");
        form.setFieldsJson("[]");

        when(formRepository.save(any(Form.class))).thenReturn(form);

        mockMvc.perform(post("/api/forms")
                .contentType("application/json")
                .content("{\"name\":\"Test Form\",\"fieldsJson\":\"[]\"}"))
                .andExpect(status().isOk());
    }
}
