package com.team15.partpicker.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.entity.UserProfile;
import com.team15.partpicker.model.repository.BuildRepository;
import com.team15.partpicker.model.repository.UserPreferenceRepository;
import com.team15.partpicker.model.repository.UserProfileRepository;
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
class RecommendationApiTests { // tests the HTTP endpoints and response contracts for the RecommendationController

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

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
            assertTrue(receivedJson.hasNonNull("cooler"));
            assertTrue(receivedJson.hasNonNull("computerCase"));
            assertTrue(receivedJson.hasNonNull("case"));

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
            assertTrue(receivedJson.hasNonNull("cooler"));
            assertTrue(receivedJson.hasNonNull("computerCase"));
            assertTrue(receivedJson.hasNonNull("case"));

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

    @Test
    void createPreferencePersistsCaseAndCoolerBrandPreferences() throws Exception {
        String email = "brand-preferences@test.com";
        deleteExistingProfile(email);

        UserProfile profile = saveUserProfile(email, "Brand", "Preferences");
        Long preferenceId = null;

        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 1500);
            preferenceJson.put("preferredCaseBrand", "Corsair");
            preferenceJson.put("preferredCoolerBrand", "Thermalright");

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            preferenceId = receivedJson.get("id").longValue();

            assertEquals("Corsair", receivedJson.get("preferredCaseBrand").textValue());
            assertEquals("Thermalright", receivedJson.get("preferredCoolerBrand").textValue());

            var savedPreference = userPreferenceRepository.findById(preferenceId).orElseThrow();
            assertEquals("Corsair", savedPreference.getPreferredCaseBrand());
            assertEquals("Thermalright", savedPreference.getPreferredCoolerBrand());
        } finally {
            if (preferenceId != null) {
                userPreferenceRepository.deleteById(preferenceId);
            }
            userProfileRepository.deleteById(profile.getId());
        }
    }

    @Test
    void userScopedBuildsAreIsolatedByProfile() throws Exception {
        String firstEmail = "build-owner-one@test.com";
        String secondEmail = "build-owner-two@test.com";
        deleteExistingProfile(firstEmail);
        deleteExistingProfile(secondEmail);

        UserProfile firstProfile = saveUserProfile(firstEmail, "Build", "OwnerOne");
        UserProfile secondProfile = saveUserProfile(secondEmail, "Build", "OwnerTwo");

        Long firstPreferenceId = null;
        Long secondPreferenceId = null;
        Long firstBuildId = null;
        Long secondBuildId = null;

        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 1300);

            MockHttpServletResponse firstPreferenceResponse = mockMvc.perform(
                            post("/profiles/" + firstProfile.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, firstPreferenceResponse.getStatus());
            ObjectNode firstPreferenceJson = objectMapper.readValue(firstPreferenceResponse.getContentAsString(), ObjectNode.class);
            firstPreferenceId = firstPreferenceJson.get("id").longValue();
            assertEquals(firstProfile.getId(), firstPreferenceJson.get("userProfileId").longValue());

            MockHttpServletResponse secondPreferenceResponse = mockMvc.perform(
                            post("/profiles/" + secondProfile.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, secondPreferenceResponse.getStatus());
            ObjectNode secondPreferenceJson = objectMapper.readValue(secondPreferenceResponse.getContentAsString(), ObjectNode.class);
            secondPreferenceId = secondPreferenceJson.get("id").longValue();
            assertEquals(secondProfile.getId(), secondPreferenceJson.get("userProfileId").longValue());

            ObjectNode firstBuildJson = objectMapper.createObjectNode();
            firstBuildJson.put("buildTitle", "First Profile Build");

            MockHttpServletResponse firstBuildResponse = mockMvc.perform(
                            post("/profiles/" + firstProfile.getId() + "/preferences/" + firstPreferenceId + "/builds")
                                    .contentType("application/json")
                                    .content(firstBuildJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, firstBuildResponse.getStatus());
            ObjectNode createdFirstBuildJson = objectMapper.readValue(firstBuildResponse.getContentAsString(), ObjectNode.class);
            firstBuildId = createdFirstBuildJson.get("id").longValue();
            assertEquals(firstProfile.getId(), createdFirstBuildJson.get("userProfileId").longValue());

            ObjectNode secondBuildJson = objectMapper.createObjectNode();
            secondBuildJson.put("buildTitle", "Second Profile Build");

            MockHttpServletResponse secondBuildResponse = mockMvc.perform(
                            post("/profiles/" + secondProfile.getId() + "/preferences/" + secondPreferenceId + "/builds")
                                    .contentType("application/json")
                                    .content(secondBuildJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, secondBuildResponse.getStatus());
            ObjectNode createdSecondBuildJson = objectMapper.readValue(secondBuildResponse.getContentAsString(), ObjectNode.class);
            secondBuildId = createdSecondBuildJson.get("id").longValue();
            assertEquals(secondProfile.getId(), createdSecondBuildJson.get("userProfileId").longValue());

            Build firstSavedBuild = buildRepository.findById(firstBuildId).orElseThrow();
            assertEquals(firstProfile.getId(), firstSavedBuild.getUserProfileId());

            MockHttpServletResponse firstScopedResponse = mockMvc.perform(
                            get("/profiles/" + firstProfile.getId() + "/builds"))
                    .andReturn()
                    .getResponse();

            assertEquals(200, firstScopedResponse.getStatus());
            ArrayNode firstScopedBuilds = (ArrayNode) objectMapper.readTree(firstScopedResponse.getContentAsString());
            assertEquals(1, firstScopedBuilds.size());
            assertEquals(firstBuildId.longValue(), firstScopedBuilds.get(0).get("id").longValue());
            assertEquals(firstProfile.getId(), firstScopedBuilds.get(0).get("userProfileId").longValue());

            MockHttpServletResponse secondScopedResponse = mockMvc.perform(
                            get("/profiles/" + secondProfile.getId() + "/builds"))
                    .andReturn()
                    .getResponse();

            assertEquals(200, secondScopedResponse.getStatus());
            ArrayNode secondScopedBuilds = (ArrayNode) objectMapper.readTree(secondScopedResponse.getContentAsString());
            assertEquals(1, secondScopedBuilds.size());
            assertEquals(secondBuildId.longValue(), secondScopedBuilds.get(0).get("id").longValue());
            assertEquals(secondProfile.getId(), secondScopedBuilds.get(0).get("userProfileId").longValue());
        } finally {
            if (firstBuildId != null) {
                buildRepository.deleteById(firstBuildId);
            }
            if (secondBuildId != null) {
                buildRepository.deleteById(secondBuildId);
            }
            if (firstPreferenceId != null) {
                userPreferenceRepository.deleteById(firstPreferenceId);
            }
            if (secondPreferenceId != null) {
                userPreferenceRepository.deleteById(secondPreferenceId);
            }
            userProfileRepository.deleteById(firstProfile.getId());
            userProfileRepository.deleteById(secondProfile.getId());
        }
    }

    @Test
    void getRecommendationForNonexistentPreference_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/recommendations/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void deleteBuildNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        delete("/builds/999999")
                                .contentType("application/json"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void createBuildForNonexistentPreference_returns404() throws Exception {
        ObjectNode buildJson = objectMapper.createObjectNode();
        buildJson.put("buildTitle", "Should Fail");

        MockHttpServletResponse response = mockMvc.perform(
                        post("/preferences/999999/builds")
                                .contentType("application/json")
                                .content(buildJson.toString()))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getBuildsForNonexistentProfile_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/profiles/999999/builds"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getPreferencesForNonexistentProfile_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/profiles/999999/preferences"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getPreferenceNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/preferences/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void createPreferenceForProfile_mismatchedBodyProfileId_returns400() throws Exception {
        String email = "error-pref-mismatch@test.com";
        deleteExistingProfile(email);

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("secret123");
        profile.setFirstName("Pref");
        profile.setLastName("Mismatch");
        UserProfile saved = userProfileRepository.save(profile);

        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 1000);
            preferenceJson.put("userProfileId", saved.getId() + 9999L);

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/" + saved.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(400, response.getStatus());
        } finally {
            userProfileRepository.deleteById(saved.getId());
        }
    }

    @Test
    void getBuildCategories_returnsAllCategories() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/build-categories"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() >= 3, "Should return at least three build categories");

        boolean hasGaming = false;
        boolean hasAiMl = false;
        boolean hasWorkstation = false;
        for (int i = 0; i < receivedJson.size(); i++) {
            String category = receivedJson.get(i).textValue();
            if ("GAMING".equals(category)) hasGaming = true;
            if ("AI_ML".equals(category)) hasAiMl = true;
            if ("WORKSTATION".equals(category)) hasWorkstation = true;
        }
        assertTrue(hasGaming, "GAMING category should be present");
        assertTrue(hasAiMl, "AI_ML category should be present");
        assertTrue(hasWorkstation, "WORKSTATION category should be present");
    }

    @Test
    void getAllBuilds_returnsListIncludingCreatedBuild() throws Exception {
        ObjectNode buildJson = objectMapper.createObjectNode();
        buildJson.put("buildTitle", "List Me Build");

        Long buildId = null;
        try {
            MockHttpServletResponse createResponse = mockMvc.perform(
                            post("/preferences/1/builds")
                                    .contentType("application/json")
                                    .content(buildJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, createResponse.getStatus());
            buildId = objectMapper.readValue(createResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            MockHttpServletResponse listResponse = mockMvc.perform(get("/builds"))
                    .andReturn()
                    .getResponse();

            assertEquals(200, listResponse.getStatus());

            ArrayNode builds = (ArrayNode) objectMapper.readTree(listResponse.getContentAsString());
            assertTrue(builds.size() > 0, "Build list should not be empty");

            final Long finalBuildId = buildId;
            boolean found = false;
            for (int i = 0; i < builds.size(); i++) {
                if (builds.get(i).get("id").longValue() == finalBuildId) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Created build should appear in the full build list");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
        }
    }

    @Test
    void getBuildById_returnsCorrectBuild() throws Exception {
        ObjectNode buildJson = objectMapper.createObjectNode();
        buildJson.put("buildTitle", "Get By ID Build");

        Long buildId = null;
        try {
            MockHttpServletResponse createResponse = mockMvc.perform(
                            post("/preferences/1/builds")
                                    .contentType("application/json")
                                    .content(buildJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, createResponse.getStatus());
            ObjectNode createdJson = objectMapper.readValue(createResponse.getContentAsString(), ObjectNode.class);
            buildId = createdJson.get("id").longValue();

            MockHttpServletResponse getResponse = mockMvc.perform(get("/builds/" + buildId))
                    .andReturn()
                    .getResponse();

            assertEquals(200, getResponse.getStatus());

            ObjectNode fetchedJson = objectMapper.readValue(getResponse.getContentAsString(), ObjectNode.class);
            assertEquals(buildId.longValue(), fetchedJson.get("id").longValue());
            assertEquals("Get By ID Build", fetchedJson.get("buildTitle").textValue());
            assertEquals(1L, fetchedJson.get("preferenceId").longValue());
            assertTrue(fetchedJson.hasNonNull("totalPrice"));
            assertTrue(fetchedJson.hasNonNull("createdAt"));
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
        }
    }

    @Test
    void getBuildNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/builds/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getBuildsForPreference_returnsOnlyMatchingBuilds() throws Exception {
        ObjectNode build1Json = objectMapper.createObjectNode();
        build1Json.put("buildTitle", "Preference Build One");
        ObjectNode build2Json = objectMapper.createObjectNode();
        build2Json.put("buildTitle", "Preference Build Two");

        Long buildId1 = null;
        Long buildId2 = null;
        try {
            MockHttpServletResponse r1 = mockMvc.perform(
                            post("/preferences/1/builds")
                                    .contentType("application/json")
                                    .content(build1Json.toString()))
                    .andReturn().getResponse();
            MockHttpServletResponse r2 = mockMvc.perform(
                            post("/preferences/1/builds")
                                    .contentType("application/json")
                                    .content(build2Json.toString()))
                    .andReturn().getResponse();

            assertEquals(201, r1.getStatus());
            assertEquals(201, r2.getStatus());
            buildId1 = objectMapper.readValue(r1.getContentAsString(), ObjectNode.class).get("id").longValue();
            buildId2 = objectMapper.readValue(r2.getContentAsString(), ObjectNode.class).get("id").longValue();

            MockHttpServletResponse listResponse = mockMvc.perform(get("/preferences/1/builds"))
                    .andReturn().getResponse();

            assertEquals(200, listResponse.getStatus());
            ArrayNode builds = (ArrayNode) objectMapper.readTree(listResponse.getContentAsString());

            for (int i = 0; i < builds.size(); i++) {
                assertEquals(1L, builds.get(i).get("preferenceId").longValue(),
                        "All returned builds should belong to preference 1");
            }
        } finally {
            if (buildId1 != null) buildRepository.deleteById(buildId1);
            if (buildId2 != null) buildRepository.deleteById(buildId2);
        }
    }

    @Test
    void createPreferenceWithoutProfile_persists() throws Exception {
        ObjectNode preferenceJson = objectMapper.createObjectNode();
        preferenceJson.put("buildCategory", "GAMING");
        preferenceJson.put("maxBudget", 1000);

        Long preferenceId = null;
        try {
            MockHttpServletResponse response = mockMvc.perform(
                            post("/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            preferenceId = receivedJson.get("id").longValue();

            assertTrue(userPreferenceRepository.findById(preferenceId).isPresent(),
                    "Preference created without a user profile should still be persisted");
            assertTrue(receivedJson.get("userProfileId").isNull(),
                    "Preference created without a user profile should have null userProfileId");
        } finally {
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
        }
    }

    @Test
    void createPreferenceWithAllBrandPreferences_persisted() throws Exception {
        String email = "all-brand-prefs@test.com";
        deleteExistingProfile(email);

        UserProfile profile = saveUserProfile(email, "Brand", "All");
        Long preferenceId = null;

        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "WORKSTATION");
            preferenceJson.put("maxBudget", 3000);
            preferenceJson.put("preferredCpuBrand", "Intel");
            preferenceJson.put("preferredGpuBrand", "NVIDIA");
            preferenceJson.put("preferredMotherboardBrand", "ASUS");
            preferenceJson.put("preferredRamBrand", "Kingston");
            preferenceJson.put("preferredStorageBrand", "Samsung");
            preferenceJson.put("preferredPsuBrand", "Seasonic");
            preferenceJson.put("preferredCoolerBrand", "Noctua");
            preferenceJson.put("preferredCaseBrand", "Fractal");

            MockHttpServletResponse response = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            preferenceId = receivedJson.get("id").longValue();

            assertEquals("Intel", receivedJson.get("preferredCpuBrand").textValue());
            assertEquals("NVIDIA", receivedJson.get("preferredGpuBrand").textValue());
            assertEquals("ASUS", receivedJson.get("preferredMotherboardBrand").textValue());
            assertEquals("Kingston", receivedJson.get("preferredRamBrand").textValue());
            assertEquals("Samsung", receivedJson.get("preferredStorageBrand").textValue());
            assertEquals("Seasonic", receivedJson.get("preferredPsuBrand").textValue());
            assertEquals("Noctua", receivedJson.get("preferredCoolerBrand").textValue());
            assertEquals("Fractal", receivedJson.get("preferredCaseBrand").textValue());

            var stored = userPreferenceRepository.findById(preferenceId).orElseThrow();
            assertEquals("Intel", stored.getPreferredCpuBrand());
            assertEquals("Samsung", stored.getPreferredStorageBrand());
        } finally {
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            userProfileRepository.deleteById(profile.getId());
        }
    }

    @Test
    void recommendationResponseIncludesCreatedAtTimestamp() throws Exception {
        Long buildId = null;
        try {
            MockHttpServletResponse response = mockMvc.perform(get("/recommendations/1"))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());

            ObjectNode receivedJson = objectMapper.readValue(response.getContentAsString(), ObjectNode.class);
            assertTrue(receivedJson.hasNonNull("createdAt"),
                    "Recommendation response should include a createdAt timestamp");

            buildId = receivedJson.get("buildId").longValue();
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
        }
    }

    @Test
    void createBuildForPreferenceNotOwnedByProfile_returns400() throws Exception {
        String firstEmail = "error-build-ownership-first@test.com";
        String secondEmail = "error-build-ownership-second@test.com";
        deleteExistingProfile(firstEmail);
        deleteExistingProfile(secondEmail);

        UserProfile firstProfile = new UserProfile();
        firstProfile.setEmail(firstEmail);
        firstProfile.setPassword("secret123");
        firstProfile.setFirstName("Owner");
        firstProfile.setLastName("One");

        UserProfile secondProfile = new UserProfile();
        secondProfile.setEmail(secondEmail);
        secondProfile.setPassword("secret123");
        secondProfile.setFirstName("Owner");
        secondProfile.setLastName("Two");

        UserProfile savedFirst = userProfileRepository.save(firstProfile);
        UserProfile savedSecond = userProfileRepository.save(secondProfile);

        Long preferenceId = null;
        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 1000);

            MockHttpServletResponse prefResponse = mockMvc.perform(
                            post("/profiles/" + savedFirst.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, prefResponse.getStatus());
            ObjectNode prefJson = objectMapper.readValue(prefResponse.getContentAsString(), ObjectNode.class);
            preferenceId = prefJson.get("id").longValue();

            ObjectNode buildJson = objectMapper.createObjectNode();
            buildJson.put("buildTitle", "Should Fail");

            MockHttpServletResponse buildResponse = mockMvc.perform(
                            post("/profiles/" + savedSecond.getId() + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json")
                                    .content(buildJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(400, buildResponse.getStatus());
        } finally {
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            userProfileRepository.deleteById(savedFirst.getId());
            userProfileRepository.deleteById(savedSecond.getId());
        }
    }

    private UserProfile saveUserProfile(String email, String firstName, String lastName) {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(email);
        userProfile.setPassword("secret123");
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        return userProfileRepository.save(userProfile);
    }

    private void deleteExistingProfile(String email) {
        userProfileRepository.findByEmailIgnoreCase(email)
                .ifPresent(existingProfile -> userProfileRepository.deleteById(existingProfile.getId()));
    }
}
