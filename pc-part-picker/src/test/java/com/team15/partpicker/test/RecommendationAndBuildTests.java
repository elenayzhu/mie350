package com.team15.partpicker.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.repository.BuildRepository;
import com.team15.partpicker.model.repository.UserPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RecommendationAndBuildTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private BuildRepository buildRepository;

    @Test
    void getRecommendationForSeededPreference() throws Exception {
        Long buildId = null;
        try {
            MockHttpServletResponse response = mockMvc.perform(get("/recommendations/1"))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertEquals(1L, receivedJson.get("preferenceId").longValue());
            assertTrue(receivedJson.hasNonNull("buildId"));
            assertTrue(receivedJson.hasNonNull("cpu"));
            assertTrue(receivedJson.hasNonNull("gpu"));
            assertTrue(receivedJson.hasNonNull("motherboard"));

            BigDecimal totalPrice = receivedJson.get("totalPrice").decimalValue();
            assertTrue(totalPrice.compareTo(BigDecimal.ZERO) > 0);
            assertTrue(totalPrice.compareTo(new BigDecimal("1300.00")) <= 0);

            buildId = receivedJson.get("buildId").longValue();
            assertTrue(buildRepository.findById(buildId).isPresent());
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
        }
    }

    @Test
    void createBuildForPreference() throws Exception {
        assertTrue(userPreferenceRepository.findById(1L).isPresent());

        String buildTitle = "Integration Build";
        ObjectNode buildJson = objectMapper.createObjectNode();
        buildJson.put("buildTitle", buildTitle);

        Long buildId = null;
        try {
            MockHttpServletResponse response = mockMvc.perform(
                            post("/preferences/1/builds")
                                    .contentType("application/json")
                                    .content(buildJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            buildId = receivedJson.get("id").longValue();

            assertEquals(buildTitle, receivedJson.get("buildTitle").textValue());
            assertEquals(1L, receivedJson.get("preferenceId").longValue());
            assertTrue(receivedJson.hasNonNull("createdAt"));
            assertTrue(receivedJson.hasNonNull("totalPrice"));

            Build savedBuild = buildRepository.findById(buildId).orElseThrow();
            assertEquals(buildTitle, savedBuild.getBuildTitle());
            assertNotNull(savedBuild.getCreatedAt());
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
        }
    }

    @Test
    void deleteBuild() throws Exception {
        String buildTitle = "Delete Me";
        ObjectNode buildJson = objectMapper.createObjectNode();
        buildJson.put("buildTitle", buildTitle);

        MockHttpServletResponse createResponse = mockMvc.perform(
                        post("/preferences/1/builds")
                                .contentType("application/json")
                                .content(buildJson.toString()))
                .andReturn()
                .getResponse();

        assertEquals(201, createResponse.getStatus());

        ObjectNode createdBuildJson = objectMapper.readValue(createResponse.getContentAsString(), ObjectNode.class);
        Long buildId = createdBuildJson.get("id").longValue();
        assertTrue(buildRepository.findById(buildId).isPresent());

        MockHttpServletResponse deleteResponse = mockMvc.perform(
                        delete("/builds/" + buildId)
                                .contentType("application/json"))
                .andReturn()
                .getResponse();

        assertEquals(204, deleteResponse.getStatus());
        assertTrue(buildRepository.findById(buildId).isEmpty());
    }
}
