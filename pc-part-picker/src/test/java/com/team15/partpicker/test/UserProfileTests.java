package com.team15.partpicker.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.team15.partpicker.model.entity.UserProfile;
import com.team15.partpicker.model.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserProfileTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void createUserProfile() throws Exception {
        String email = "integration-create@test.com";
        deleteExistingProfile(email);

        ObjectNode profileJson = objectMapper.createObjectNode();
        profileJson.put("email", email);
        profileJson.put("password", "secret123");
        profileJson.put("firstName", "Integration");
        profileJson.put("lastName", "Tester");

        Long profileId = null;
        try {
            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles")
                                    .contentType("application/json")
                                    .content(profileJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            profileId = receivedJson.get("id").longValue();

            assertEquals(email, receivedJson.get("email").textValue());
            assertEquals("Integration", receivedJson.get("firstName").textValue());
            assertEquals("Tester", receivedJson.get("lastName").textValue());
            assertTrue(userProfileRepository.findByEmailIgnoreCase(email).isPresent());
        } finally {
            if (profileId != null) {
                userProfileRepository.deleteById(profileId);
            }
        }
    }

    @Test
    void loginWithValidCredentials() throws Exception {
        String email = "integration-login@test.com";
        deleteExistingProfile(email);

        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(email);
        userProfile.setPassword("secret123");
        userProfile.setFirstName("Login");
        userProfile.setLastName("Tester");
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);

        ObjectNode loginJson = objectMapper.createObjectNode();
        loginJson.put("email", email);
        loginJson.put("password", "secret123");

        try {
            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/login")
                                    .contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertEquals(savedUserProfile.getId(), receivedJson.get("id").longValue());
            assertEquals(email, receivedJson.get("email").textValue());
            assertEquals("Login", receivedJson.get("firstName").textValue());
            assertEquals("Tester", receivedJson.get("lastName").textValue());
        } finally {
            userProfileRepository.deleteById(savedUserProfile.getId());
        }
    }

    private void deleteExistingProfile(String email) {
        userProfileRepository.findByEmailIgnoreCase(email)
                .ifPresent(existingProfile -> userProfileRepository.deleteById(existingProfile.getId()));
    }
}
