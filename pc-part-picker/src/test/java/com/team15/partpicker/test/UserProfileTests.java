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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

    @Test
    void getUserProfileById_returnsCorrectProfile() throws Exception {
        String email = "integration-get-profile@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("secret123");
        profile.setFirstName("Get");
        profile.setLastName("Profile");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            MockHttpServletResponse response = mockMvc.perform(
                            get("/profiles/" + saved.getId()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertEquals(saved.getId(), receivedJson.get("id").longValue());
            assertEquals(email, receivedJson.get("email").textValue());
            assertEquals("Get", receivedJson.get("firstName").textValue());
            assertEquals("Profile", receivedJson.get("lastName").textValue());
            assertFalse(receivedJson.has("password"));
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void updateUserProfile_updatesNameAndEmail() throws Exception {
        String originalEmail = "integration-update-before@test.com";
        String updatedEmail = "integration-update-after@test.com";
        deleteExistingProfile(originalEmail);
        deleteExistingProfile(updatedEmail);

        UserProfile profile = new UserProfile();
        profile.setEmail(originalEmail);
        profile.setPassword("secret123");
        profile.setFirstName("Before");
        profile.setLastName("Update");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode updateJson = objectMapper.createObjectNode();
            updateJson.put("firstName", "After");
            updateJson.put("lastName", "Updated");
            updateJson.put("email", updatedEmail);

            MockHttpServletResponse response = mockMvc.perform(
                            put("/profiles/" + saved.getId())
                                    .contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertEquals(saved.getId(), receivedJson.get("id").longValue());
            assertEquals("After", receivedJson.get("firstName").textValue());
            assertEquals("Updated", receivedJson.get("lastName").textValue());
            assertEquals(updatedEmail, receivedJson.get("email").textValue());

            UserProfile updatedProfile = userProfileRepository.findById(saved.getId()).orElseThrow();
            assertEquals("After", updatedProfile.getFirstName());
            assertEquals("Updated", updatedProfile.getLastName());
            assertEquals(updatedEmail, updatedProfile.getEmail());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void updateUserProfile_updatesPassword() throws Exception {
        String email = "integration-update-password@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("oldPassword");
        profile.setFirstName("Pass");
        profile.setLastName("Change");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode updateJson = objectMapper.createObjectNode();
            updateJson.put("password", "newPassword");

            MockHttpServletResponse updateResponse = mockMvc.perform(
                            put("/profiles/" + saved.getId())
                                    .contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, updateResponse.getStatus());

            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", email);
            loginJson.put("password", "newPassword");

            MockHttpServletResponse loginResponse = mockMvc.perform(
                            post("/profiles/login")
                                    .contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, loginResponse.getStatus());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void loginWithWrongPassword_returns401() throws Exception {
        String email = "error-test-wrong-pw@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("correctPassword");
        profile.setFirstName("Error");
        profile.setLastName("Test");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", email);
            loginJson.put("password", "wrongPassword");

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/login")
                                    .contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(401, response.getStatus());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void loginWithNonexistentEmail_returns401() throws Exception {
        ObjectNode loginJson = objectMapper.createObjectNode();
        loginJson.put("email", "definitely-does-not-exist@test.com");
        loginJson.put("password", "anything");

        MockHttpServletResponse response = mockMvc.perform(
                        post("/profiles/login")
                                .contentType("application/json")
                                .content(loginJson.toString()))
                .andReturn()
                .getResponse();

        assertEquals(401, response.getStatus());
    }

    @Test
    void loginWithBlankPassword_returns401() throws Exception {
        ObjectNode loginJson = objectMapper.createObjectNode();
        loginJson.put("email", "someone@test.com");
        loginJson.put("password", "   ");

        MockHttpServletResponse response = mockMvc.perform(
                        post("/profiles/login")
                                .contentType("application/json")
                                .content(loginJson.toString()))
                .andReturn()
                .getResponse();

        assertTrue(response.getStatus() == 400 || response.getStatus() == 401,
                "Blank password should be rejected with 400 or 401");
    }

    @Test
    void registerWithDuplicateEmail_returns409() throws Exception {
        String email = "error-test-duplicate@test.com";
        deleteExistingProfile(email);

        UserProfile existing = new UserProfile();
        existing.setEmail(email);
        existing.setPassword("secret123");
        existing.setFirstName("First");
        existing.setLastName("User");
        UserProfile saved = userProfileRepository.save(existing);

        try {
            ObjectNode profileJson = objectMapper.createObjectNode();
            profileJson.put("email", email);
            profileJson.put("password", "secret456");
            profileJson.put("firstName", "Second");
            profileJson.put("lastName", "User");

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles")
                                    .contentType("application/json")
                                    .content(profileJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(409, response.getStatus());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void getUserProfileNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/profiles/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void updateUserProfile_idMismatchInBody_returns400() throws Exception {
        String email = "error-update-id-mismatch@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("secret123");
        profile.setFirstName("Mismatch");
        profile.setLastName("Test");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode updateJson = objectMapper.createObjectNode();
            updateJson.put("id", saved.getId() + 9999L);
            updateJson.put("firstName", "Updated");

            MockHttpServletResponse response = mockMvc.perform(
                            put("/profiles/" + saved.getId())
                                    .contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(400, response.getStatus());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void loginWithCaseInsensitiveEmail_succeeds() throws Exception {
        String email = "case-insensitive@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("secret123");
        profile.setFirstName("Case");
        profile.setLastName("Insensitive");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", "CASE-INSENSITIVE@TEST.COM");
            loginJson.put("password", "secret123");

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/login")
                                    .contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus(), "Login should succeed with upper-cased email");

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertEquals(saved.getId(), receivedJson.get("id").longValue());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void createUserProfile_withWhitespacePaddedEmail_returns400() throws Exception {
        // The @Email bean validation on UserProfile.email fires at request-binding time,
        // before the service's normalizedOrNull() trimming can run.
        // A whitespace-padded email ("  foo@test.com  ") is therefore correctly
        // rejected by the API with 400 Bad Request.
        ObjectNode profileJson = objectMapper.createObjectNode();
        profileJson.put("email", "  trimmed-email@test.com  ");
        profileJson.put("password", "secret123");
        profileJson.put("firstName", "Trim");
        profileJson.put("lastName", "Test");

        MockHttpServletResponse response = mockMvc.perform(
                        post("/profiles")
                                .contentType("application/json")
                                .content(profileJson.toString()))
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus(),
                "Whitespace-padded email should be rejected by @Email bean validation before reaching the service");
    }

    @Test
    void updateNonexistentProfile_returns404() throws Exception {
        ObjectNode updateJson = objectMapper.createObjectNode();
        updateJson.put("firstName", "Ghost");

        MockHttpServletResponse response = mockMvc.perform(
                        put("/profiles/999999")
                                .contentType("application/json")
                                .content(updateJson.toString()))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void loginWithAdminAccount_returnsAdminTrue() throws Exception {
        String email = "admin-login-test@test.com";
        deleteExistingProfile(email);

        UserProfile adminProfile = new UserProfile();
        adminProfile.setEmail(email);
        adminProfile.setPassword("adminPass");
        adminProfile.setFirstName("Admin");
        adminProfile.setLastName("User");
        adminProfile.setAdmin(true);
        UserProfile saved = userProfileRepository.save(adminProfile);

        try {
            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", email);
            loginJson.put("password", "adminPass");

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/login")
                                    .contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertTrue(receivedJson.get("isAdmin").booleanValue(), "isAdmin should be true for admin account");
            assertTrue(receivedJson.get("admin").booleanValue(), "admin should be true for admin account");
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void createUserProfile_namesAreTrimmed() throws Exception {
        String email = "trim-names@test.com";
        deleteExistingProfile(email);

        ObjectNode profileJson = objectMapper.createObjectNode();
        profileJson.put("email", email);
        profileJson.put("password", "secret123");
        profileJson.put("firstName", "  John  ");
        profileJson.put("lastName", "  Doe  ");

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

            assertEquals("John", receivedJson.get("firstName").textValue(),
                    "firstName should be trimmed");
            assertEquals("Doe", receivedJson.get("lastName").textValue(),
                    "lastName should be trimmed");

            UserProfile stored = userProfileRepository.findById(profileId).orElseThrow();
            assertEquals("John", stored.getFirstName());
            assertEquals("Doe", stored.getLastName());
        } finally {
            if (profileId != null) userProfileRepository.deleteById(profileId);
        }
    }

    @Test
    void updateUserProfile_passwordIsTrimmedBeforeStoring() throws Exception {
        String email = "trim-password@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("oldPassword");
        profile.setFirstName("Trim");
        profile.setLastName("Pw");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode updateJson = objectMapper.createObjectNode();
            updateJson.put("password", "  newPassword  ");

            MockHttpServletResponse updateResponse = mockMvc.perform(
                            put("/profiles/" + saved.getId())
                                    .contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, updateResponse.getStatus());

            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", email);
            loginJson.put("password", "newPassword");

            MockHttpServletResponse loginResponse = mockMvc.perform(
                            post("/profiles/login")
                                    .contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, loginResponse.getStatus(),
                    "Should be able to login with trimmed version of the updated password");
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void getUserProfileById_passwordNotExposedInResponse() throws Exception {
        String email = "no-password-exposure@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("secretPassword");
        profile.setFirstName("No");
        profile.setLastName("Expose");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            MockHttpServletResponse response = mockMvc.perform(
                            get("/profiles/" + saved.getId()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertFalse(receivedJson.has("password"),
                    "Password must never be included in profile responses");
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void updateUserProfile_duplicateEmail_returns409() throws Exception {
        String firstEmail = "error-update-email-first@test.com";
        String secondEmail = "error-update-email-second@test.com";
        deleteExistingProfile(firstEmail);
        deleteExistingProfile(secondEmail);

        UserProfile first = new UserProfile();
        first.setEmail(firstEmail);
        first.setPassword("secret123");
        first.setFirstName("First");
        first.setLastName("User");

        UserProfile second = new UserProfile();
        second.setEmail(secondEmail);
        second.setPassword("secret123");
        second.setFirstName("Second");
        second.setLastName("User");

        UserProfile savedFirst = userProfileRepository.save(first);
        UserProfile savedSecond = userProfileRepository.save(second);

        try {
            ObjectNode updateJson = objectMapper.createObjectNode();
            updateJson.put("email", firstEmail);

            MockHttpServletResponse response = mockMvc.perform(
                            put("/profiles/" + savedSecond.getId())
                                    .contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(409, response.getStatus());
        } finally {
            userProfileRepository.deleteById(savedFirst.getId());
            userProfileRepository.deleteById(savedSecond.getId());
        }
    }
}
