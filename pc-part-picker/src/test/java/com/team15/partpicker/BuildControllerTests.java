package com.team15.partpicker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BuildControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createBuildReturnsCreatedBuildWithComputedTotal() throws Exception {
        String requestBody = """
                {
                  "buildTitle": "My First Build",
                  "cpu": { "id": 1 },
                  "gpu": { "id": 1 },
                  "motherboard": { "id": 1 },
                  "ram": { "id": 1 },
                  "storage": { "id": 1 },
                  "psu": { "id": 1 },
                  "cooler": { "id": 1 },
                  "computerCase": { "id": 1 }
                }
                """;

        mockMvc.perform(post("/preferences/1/builds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.preferenceId").value(1))
                .andExpect(jsonPath("$.buildTitle").value("My First Build"))
                .andExpect(jsonPath("$.cpu.id").value(1))
                .andExpect(jsonPath("$.gpu.id").value(1))
                .andExpect(jsonPath("$.motherboard.id").value(1))
                .andExpect(jsonPath("$.ram.id").value(1))
                .andExpect(jsonPath("$.storage.id").value(1))
                .andExpect(jsonPath("$.psu.id").value(1))
                .andExpect(jsonPath("$.cooler.id").value(1))
                .andExpect(jsonPath("$.computerCase.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(1429.92))
                .andExpect(jsonPath("$.createdAt").exists());
    }
}
