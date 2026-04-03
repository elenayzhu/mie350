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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
            assertTrue(receivedJson.has("isAdmin"));
            assertTrue(receivedJson.has("admin"));
            assertFalse(receivedJson.get("isAdmin").booleanValue());
            assertFalse(receivedJson.get("admin").booleanValue());
            assertFalse(receivedJson.has("password"));
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
            assertTrue(receivedJson.has("isAdmin"));
            assertTrue(receivedJson.has("admin"));
            assertFalse(receivedJson.get("isAdmin").booleanValue());
            assertFalse(receivedJson.get("admin").booleanValue());
            assertFalse(receivedJson.has("password"));
        } finally {
            userProfileRepository.deleteById(savedUserProfile.getId());
        }
    }

    @Test
    void loginDoesNotInheritAdminStatusFromAnotherAccount() throws Exception {
        String adminEmail = "integration-admin@test.com";
        String regularEmail = "integration-regular@test.com";
        deleteExistingProfile(adminEmail);
        deleteExistingProfile(regularEmail);

        UserProfile adminProfile = new UserProfile();
        adminProfile.setEmail(adminEmail);
        adminProfile.setPassword("secret123");
        adminProfile.setFirstName("Admin");
        adminProfile.setLastName("Tester");
        adminProfile.setAdmin(true);

        UserProfile regularProfile = new UserProfile();
        regularProfile.setEmail(regularEmail);
        regularProfile.setPassword("secret123");
        regularProfile.setFirstName("Regular");
        regularProfile.setLastName("Tester");
        regularProfile.setAdmin(false);

        UserProfile savedAdminProfile = userProfileRepository.save(adminProfile);
        UserProfile savedRegularProfile = userProfileRepository.save(regularProfile);

        ObjectNode loginJson = objectMapper.createObjectNode();
        loginJson.put("email", regularEmail);
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
            assertEquals(savedRegularProfile.getId(), receivedJson.get("id").longValue());
            assertEquals(regularEmail, receivedJson.get("email").textValue());
            assertTrue(receivedJson.has("isAdmin"));
            assertTrue(receivedJson.has("admin"));
            assertFalse(receivedJson.get("isAdmin").booleanValue());
            assertFalse(receivedJson.get("admin").booleanValue());
        } finally {
            userProfileRepository.deleteById(savedAdminProfile.getId());
            userProfileRepository.deleteById(savedRegularProfile.getId());
        }
    }

    private void deleteExistingProfile(String email) {
        userProfileRepository.findByEmailIgnoreCase(email)
                .ifPresent(existingProfile -> userProfileRepository.deleteById(existingProfile.getId()));
    }
}
