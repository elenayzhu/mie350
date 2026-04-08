package com.team15.partpicker.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.team15.partpicker.model.entity.UserProfile;
import com.team15.partpicker.model.repository.BuildRepository;
import com.team15.partpicker.model.repository.CpuRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SystemTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private UserPreferenceRepository userPreferenceRepository;
    @Autowired private BuildRepository buildRepository;
    @Autowired private CpuRepository cpuRepository;

    // -----------------------------------------------------------------------
    // Test 1: Complete happy-path journey
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_registerLoginPreferenceRecommendBuild() throws Exception {
        String email = "system-journey-1@test.com";
        deleteExistingProfile(email);

        Long profileId = null;
        Long preferenceId = null;
        Long buildId = null;
        try {
            // Step 1: Register
            ObjectNode profileJson = objectMapper.createObjectNode();
            profileJson.put("email", email);
            profileJson.put("password", "secret123");
            profileJson.put("firstName", "Journey");
            profileJson.put("lastName", "One");

            MockHttpServletResponse registerResponse = mockMvc.perform(
                            post("/profiles").contentType("application/json")
                                    .content(profileJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, registerResponse.getStatus(), "Registration should succeed");
            profileId = objectMapper.readValue(registerResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            // Step 2: Login with registered credentials
            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", email);
            loginJson.put("password", "secret123");

            MockHttpServletResponse loginResponse = mockMvc.perform(
                            post("/profiles/login").contentType("application/json")
                                    .content(loginJson.toString()))
                    .andReturn().getResponse();
            assertEquals(200, loginResponse.getStatus(), "Login should succeed after registration");

            ObjectNode loginResult = objectMapper.readValue(loginResponse.getContentAsString(), ObjectNode.class);
            assertEquals(profileId.longValue(), loginResult.get("id").longValue(),
                    "Logged-in profile ID should match registered profile ID");

            // Step 3: Create a GAMING preference with $1500 budget
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 1500);

            MockHttpServletResponse preferenceResponse = mockMvc.perform(
                            post("/profiles/" + profileId + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn().getResponse();
            assertEquals(200, preferenceResponse.getStatus(), "Preference creation should succeed");

            ObjectNode preferenceResult = objectMapper.readValue(preferenceResponse.getContentAsString(), ObjectNode.class);
            preferenceId = preferenceResult.get("id").longValue();
            assertEquals(profileId.longValue(), preferenceResult.get("userProfileId").longValue(),
                    "Preference should be linked to the correct user profile");

            // Step 4: Get recommendation for the preference
            MockHttpServletResponse recResponse = mockMvc.perform(
                            get("/recommendations/" + preferenceId))
                    .andReturn().getResponse();
            assertEquals(200, recResponse.getStatus(), "Recommendation endpoint should return 200");

            ObjectNode recResult = objectMapper.readValue(recResponse.getContentAsString(), ObjectNode.class);
            assertEquals(preferenceId.longValue(), recResult.get("preferenceId").longValue());
            assertTrue(recResult.hasNonNull("cpu"), "Recommendation should include a CPU");
            assertTrue(recResult.hasNonNull("gpu"), "Recommendation should include a GPU");
            assertTrue(recResult.get("totalPrice").decimalValue().compareTo(new BigDecimal("1500")) <= 0,
                    "Total price must be within the $1500 budget");

            Long recBuildId = recResult.get("buildId").longValue();
            buildRepository.deleteById(recBuildId);

            // Step 5: Save a named build
            ObjectNode buildJson = objectMapper.createObjectNode();
            buildJson.put("buildTitle", "My First Build");

            MockHttpServletResponse buildResponse = mockMvc.perform(
                            post("/profiles/" + profileId + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json")
                                    .content(buildJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, buildResponse.getStatus(), "Build creation should succeed");

            ObjectNode buildResult = objectMapper.readValue(buildResponse.getContentAsString(), ObjectNode.class);
            buildId = buildResult.get("id").longValue();
            assertEquals("My First Build", buildResult.get("buildTitle").textValue());
            assertEquals(profileId.longValue(), buildResult.get("userProfileId").longValue());

            // Step 6: Verify build appears in user's build list
            MockHttpServletResponse buildsResponse = mockMvc.perform(
                            get("/profiles/" + profileId + "/builds"))
                    .andReturn().getResponse();
            assertEquals(200, buildsResponse.getStatus());

            ArrayNode builds = (ArrayNode) objectMapper.readTree(buildsResponse.getContentAsString());
            assertEquals(1, builds.size(), "User should have exactly one build");
            assertEquals(buildId.longValue(), builds.get(0).get("id").longValue());
            assertEquals("My First Build", builds.get(0).get("buildTitle").textValue());
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            if (profileId != null) userProfileRepository.deleteById(profileId);
        }
    }

    // -----------------------------------------------------------------------
    // Test 2: Multiple builds from the same preference
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_multipleBuildsSamePreference() throws Exception {
        String email = "system-journey-2@test.com";
        deleteExistingProfile(email);

        UserProfile profile = saveUserProfile(email, "Journey", "Two");
        Long preferenceId = null;
        Long buildId1 = null;
        Long buildId2 = null;
        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 1200);

            MockHttpServletResponse prefResponse = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn().getResponse();
            assertEquals(200, prefResponse.getStatus());
            preferenceId = objectMapper.readValue(prefResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            ObjectNode build1Json = objectMapper.createObjectNode();
            build1Json.put("buildTitle", "Build Iteration One");
            ObjectNode build2Json = objectMapper.createObjectNode();
            build2Json.put("buildTitle", "Build Iteration Two");

            MockHttpServletResponse b1Response = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json").content(build1Json.toString()))
                    .andReturn().getResponse();
            MockHttpServletResponse b2Response = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json").content(build2Json.toString()))
                    .andReturn().getResponse();

            assertEquals(201, b1Response.getStatus());
            assertEquals(201, b2Response.getStatus());
            buildId1 = objectMapper.readValue(b1Response.getContentAsString(), ObjectNode.class).get("id").longValue();
            buildId2 = objectMapper.readValue(b2Response.getContentAsString(), ObjectNode.class).get("id").longValue();

            assertNotEquals(buildId1, buildId2, "Each build should have a unique ID");

            MockHttpServletResponse buildsForPrefResponse = mockMvc.perform(
                            get("/preferences/" + preferenceId + "/builds"))
                    .andReturn().getResponse();
            assertEquals(200, buildsForPrefResponse.getStatus());

            ArrayNode builds = (ArrayNode) objectMapper.readTree(buildsForPrefResponse.getContentAsString());
            assertEquals(2, builds.size(), "Both builds should be returned for the preference");

            for (int i = 0; i < builds.size(); i++) {
                assertEquals(preferenceId.longValue(), builds.get(i).get("preferenceId").longValue(),
                        "All returned builds should belong to the same preference");
            }
        } finally {
            if (buildId1 != null) buildRepository.deleteById(buildId1);
            if (buildId2 != null) buildRepository.deleteById(buildId2);
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            userProfileRepository.deleteById(profile.getId());
        }
    }

    // -----------------------------------------------------------------------
    // Test 3: Multiple preferences with different categories
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_multiplePreferencesDifferentCategories() throws Exception {
        String email = "system-journey-3@test.com";
        deleteExistingProfile(email);

        UserProfile profile = saveUserProfile(email, "Journey", "Three");
        Long gamingPrefId = null;
        Long aiMlPrefId = null;
        Long workstationPrefId = null;
        Long gamingBuildId = null;
        Long aiMlBuildId = null;
        Long workstationBuildId = null;
        try {
            gamingPrefId = createPreferenceForProfile(profile.getId(), "GAMING", 1500);
            aiMlPrefId = createPreferenceForProfile(profile.getId(), "AI_ML", 2000);
            workstationPrefId = createPreferenceForProfile(profile.getId(), "WORKSTATION", 2500);

            // Verify all three preferences appear in the user's preference list
            MockHttpServletResponse prefsResponse = mockMvc.perform(
                            get("/profiles/" + profile.getId() + "/preferences"))
                    .andReturn().getResponse();
            assertEquals(200, prefsResponse.getStatus());
            ArrayNode prefs = (ArrayNode) objectMapper.readTree(prefsResponse.getContentAsString());
            assertEquals(3, prefs.size(), "User should have exactly three preferences");

            // Get recommendations and verify each respects its budget
            MockHttpServletResponse gamingRecResponse = mockMvc.perform(
                    get("/recommendations/" + gamingPrefId)).andReturn().getResponse();
            MockHttpServletResponse aiMlRecResponse = mockMvc.perform(
                    get("/recommendations/" + aiMlPrefId)).andReturn().getResponse();
            MockHttpServletResponse workstationRecResponse = mockMvc.perform(
                    get("/recommendations/" + workstationPrefId)).andReturn().getResponse();

            assertEquals(200, gamingRecResponse.getStatus());
            assertEquals(200, aiMlRecResponse.getStatus());
            assertEquals(200, workstationRecResponse.getStatus());

            ObjectNode gamingRec = objectMapper.readValue(gamingRecResponse.getContentAsString(), ObjectNode.class);
            ObjectNode aiMlRec = objectMapper.readValue(aiMlRecResponse.getContentAsString(), ObjectNode.class);
            ObjectNode workstationRec = objectMapper.readValue(workstationRecResponse.getContentAsString(), ObjectNode.class);

            gamingBuildId = gamingRec.get("buildId").longValue();
            aiMlBuildId = aiMlRec.get("buildId").longValue();
            workstationBuildId = workstationRec.get("buildId").longValue();

            assertTrue(gamingRec.get("totalPrice").decimalValue().compareTo(new BigDecimal("1500")) <= 0,
                    "GAMING recommendation total should be within $1500");
            assertTrue(aiMlRec.get("totalPrice").decimalValue().compareTo(new BigDecimal("2000")) <= 0,
                    "AI_ML recommendation total should be within $2000");
            assertTrue(workstationRec.get("totalPrice").decimalValue().compareTo(new BigDecimal("2500")) <= 0,
                    "WORKSTATION recommendation total should be within $2500");

            // Higher budgets should produce higher or equal total prices
            assertTrue(
                    aiMlRec.get("totalPrice").decimalValue()
                            .compareTo(gamingRec.get("totalPrice").decimalValue()) >= 0,
                    "AI_ML build ($2000 budget) should cost at least as much as GAMING build ($1500 budget)");
        } finally {
            if (gamingBuildId != null) buildRepository.deleteById(gamingBuildId);
            if (aiMlBuildId != null) buildRepository.deleteById(aiMlBuildId);
            if (workstationBuildId != null) buildRepository.deleteById(workstationBuildId);
            if (gamingPrefId != null) userPreferenceRepository.deleteById(gamingPrefId);
            if (aiMlPrefId != null) userPreferenceRepository.deleteById(aiMlPrefId);
            if (workstationPrefId != null) userPreferenceRepository.deleteById(workstationPrefId);
            userProfileRepository.deleteById(profile.getId());
        }
    }

    // -----------------------------------------------------------------------
    // Test 4: Brand preferences flow end-to-end
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_brandPreferencesEndToEnd() throws Exception {
        String email = "system-journey-4@test.com";
        deleteExistingProfile(email);

        UserProfile profile = saveUserProfile(email, "Journey", "Four");
        Long preferenceId = null;
        Long buildId = null;
        try {
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 2000);
            preferenceJson.put("preferredCpuBrand", "AMD");
            preferenceJson.put("preferredGpuBrand", "NVIDIA");
            preferenceJson.put("preferredRamBrand", "Corsair");

            MockHttpServletResponse prefResponse = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences")
                                    .contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn().getResponse();
            assertEquals(200, prefResponse.getStatus());

            ObjectNode prefResult = objectMapper.readValue(prefResponse.getContentAsString(), ObjectNode.class);
            preferenceId = prefResult.get("id").longValue();

            // Verify preferences were stored correctly
            MockHttpServletResponse getPrefResponse = mockMvc.perform(
                    get("/preferences/" + preferenceId)).andReturn().getResponse();
            assertEquals(200, getPrefResponse.getStatus());
            ObjectNode storedPref = objectMapper.readValue(getPrefResponse.getContentAsString(), ObjectNode.class);
            assertEquals("AMD", storedPref.get("preferredCpuBrand").textValue());
            assertEquals("NVIDIA", storedPref.get("preferredGpuBrand").textValue());
            assertEquals("Corsair", storedPref.get("preferredRamBrand").textValue());

            // Get recommendation and verify brands are respected
            MockHttpServletResponse recResponse = mockMvc.perform(
                    get("/recommendations/" + preferenceId)).andReturn().getResponse();
            assertEquals(200, recResponse.getStatus());
            ObjectNode recResult = objectMapper.readValue(recResponse.getContentAsString(), ObjectNode.class);

            Long recBuildId = recResult.get("buildId").longValue();

            if (recResult.hasNonNull("cpu")) {
                assertEquals("AMD", recResult.get("cpu").get("brand").textValue(),
                        "CPU brand should match preference");
            }
            if (recResult.hasNonNull("gpu")) {
                assertEquals("NVIDIA", recResult.get("gpu").get("brand").textValue(),
                        "GPU brand should match preference");
            }
            if (recResult.hasNonNull("ram")) {
                assertEquals("Corsair", recResult.get("ram").get("brand").textValue(),
                        "RAM brand should match preference");
            }

            buildRepository.deleteById(recBuildId);

            // Save build and verify brand details are preserved
            ObjectNode buildJson = objectMapper.createObjectNode();
            buildJson.put("buildTitle", "Brand Specific Build");

            MockHttpServletResponse buildResponse = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json").content(buildJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, buildResponse.getStatus());
            buildId = objectMapper.readValue(buildResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            MockHttpServletResponse getBuildResponse = mockMvc.perform(
                    get("/builds/" + buildId)).andReturn().getResponse();
            assertEquals(200, getBuildResponse.getStatus());
            ObjectNode savedBuild = objectMapper.readValue(getBuildResponse.getContentAsString(), ObjectNode.class);

            if (savedBuild.hasNonNull("cpu")) {
                assertEquals("AMD", savedBuild.get("cpu").get("brand").textValue(),
                        "Persisted build CPU should retain AMD brand");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            userProfileRepository.deleteById(profile.getId());
        }
    }

    // -----------------------------------------------------------------------
    // Test 5: Catalog CRUD immediately visible to the recommendation engine
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_catalogCrudThenRecommendation() throws Exception {
        String uniqueBrand = "UniqueSysTestBrand" + System.nanoTime();
        Long newCpuId = null;
        Long preferenceId = null;
        Long buildId = null;
        try {
            // Add a cheap, uniquely branded CPU to the catalog
            ObjectNode cpuJson = objectMapper.createObjectNode();
            cpuJson.put("model", "SysTest CPU");
            cpuJson.put("brand", uniqueBrand);
            cpuJson.put("socket", "AM5");
            cpuJson.put("cores", 4);
            cpuJson.put("price", new java.math.BigDecimal("10.00"));

            MockHttpServletResponse createCpuResponse = mockMvc.perform(
                            post("/cpus").contentType("application/json")
                                    .content(cpuJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, createCpuResponse.getStatus(), "New CPU should be created successfully");
            newCpuId = objectMapper.readValue(createCpuResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            // Create a preference that targets only that brand with a budget of $50
            ObjectNode preferenceJson = objectMapper.createObjectNode();
            preferenceJson.put("buildCategory", "GAMING");
            preferenceJson.put("maxBudget", 50);
            preferenceJson.put("preferredCpuBrand", uniqueBrand);

            MockHttpServletResponse prefResponse = mockMvc.perform(
                            post("/preferences").contentType("application/json")
                                    .content(preferenceJson.toString()))
                    .andReturn().getResponse();
            assertEquals(200, prefResponse.getStatus());
            preferenceId = objectMapper.readValue(prefResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            // Get recommendation; the new CPU should be selected
            MockHttpServletResponse recResponse = mockMvc.perform(
                    get("/recommendations/" + preferenceId)).andReturn().getResponse();
            assertEquals(200, recResponse.getStatus());

            ObjectNode recResult = objectMapper.readValue(recResponse.getContentAsString(), ObjectNode.class);
            buildId = recResult.get("buildId").longValue();

            assertNotNull(recResult.get("cpu"), "CPU should be selected");
            assertEquals(uniqueBrand, recResult.get("cpu").get("brand").textValue(),
                    "The newly added CPU should be selected when it is the only brand match");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            if (newCpuId != null) {
                mockMvc.perform(delete("/cpus/" + newCpuId)).andReturn();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Test 6: Build deletion and re-creation
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_buildDeletionAndReCreation() throws Exception {
        String email = "system-journey-6@test.com";
        deleteExistingProfile(email);

        UserProfile profile = saveUserProfile(email, "Journey", "Six");
        Long preferenceId = null;
        Long firstBuildId = null;
        Long secondBuildId = null;
        try {
            preferenceId = createPreferenceForProfile(profile.getId(), "GAMING", 1300);

            ObjectNode buildJson = objectMapper.createObjectNode();
            buildJson.put("buildTitle", "To Be Deleted");

            MockHttpServletResponse createResponse = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json").content(buildJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, createResponse.getStatus());
            firstBuildId = objectMapper.readValue(createResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            // Verify it exists
            assertEquals(200, mockMvc.perform(get("/builds/" + firstBuildId))
                    .andReturn().getResponse().getStatus());

            // Delete the build
            MockHttpServletResponse deleteResponse = mockMvc.perform(
                    delete("/builds/" + firstBuildId)).andReturn().getResponse();
            assertEquals(204, deleteResponse.getStatus());
            firstBuildId = null;

            // Verify it no longer exists
            assertEquals(404, mockMvc.perform(get("/builds/" + Long.parseLong(
                    objectMapper.readValue(createResponse.getContentAsString(), ObjectNode.class)
                            .get("id").toString())))
                    .andReturn().getResponse().getStatus());

            // Verify user's build list is now empty
            MockHttpServletResponse listResponse = mockMvc.perform(
                    get("/profiles/" + profile.getId() + "/builds")).andReturn().getResponse();
            assertEquals(200, listResponse.getStatus());
            ArrayNode builds = (ArrayNode) objectMapper.readTree(listResponse.getContentAsString());
            assertEquals(0, builds.size(), "Build list should be empty after deletion");

            // Re-create a build from the same preference
            ObjectNode rebuildJson = objectMapper.createObjectNode();
            rebuildJson.put("buildTitle", "The Replacement Build");

            MockHttpServletResponse recreateResponse = mockMvc.perform(
                            post("/profiles/" + profile.getId() + "/preferences/" + preferenceId + "/builds")
                                    .contentType("application/json").content(rebuildJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, recreateResponse.getStatus(), "New build should be created successfully after deletion");
            secondBuildId = objectMapper.readValue(recreateResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            assertEquals("The Replacement Build",
                    objectMapper.readValue(recreateResponse.getContentAsString(), ObjectNode.class)
                            .get("buildTitle").textValue());
        } finally {
            if (firstBuildId != null) buildRepository.deleteById(firstBuildId);
            if (secondBuildId != null) buildRepository.deleteById(secondBuildId);
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            userProfileRepository.deleteById(profile.getId());
        }
    }

    // -----------------------------------------------------------------------
    // Test 7: Two users have fully isolated workflows
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_twoUsersIsolatedWorkflows() throws Exception {
        String emailA = "system-journey-7a@test.com";
        String emailB = "system-journey-7b@test.com";
        deleteExistingProfile(emailA);
        deleteExistingProfile(emailB);

        UserProfile userA = saveUserProfile(emailA, "User", "Alpha");
        UserProfile userB = saveUserProfile(emailB, "User", "Beta");
        Long prefIdA = null;
        Long prefIdB = null;
        Long buildIdA = null;
        Long buildIdB = null;
        try {
            prefIdA = createPreferenceForProfile(userA.getId(), "GAMING", 1500);
            prefIdB = createPreferenceForProfile(userB.getId(), "GAMING", 1500);

            ObjectNode buildAJson = objectMapper.createObjectNode();
            buildAJson.put("buildTitle", "User A Build");
            ObjectNode buildBJson = objectMapper.createObjectNode();
            buildBJson.put("buildTitle", "User B Build");

            MockHttpServletResponse buildAResponse = mockMvc.perform(
                            post("/profiles/" + userA.getId() + "/preferences/" + prefIdA + "/builds")
                                    .contentType("application/json").content(buildAJson.toString()))
                    .andReturn().getResponse();
            MockHttpServletResponse buildBResponse = mockMvc.perform(
                            post("/profiles/" + userB.getId() + "/preferences/" + prefIdB + "/builds")
                                    .contentType("application/json").content(buildBJson.toString()))
                    .andReturn().getResponse();

            assertEquals(201, buildAResponse.getStatus());
            assertEquals(201, buildBResponse.getStatus());
            buildIdA = objectMapper.readValue(buildAResponse.getContentAsString(), ObjectNode.class).get("id").longValue();
            buildIdB = objectMapper.readValue(buildBResponse.getContentAsString(), ObjectNode.class).get("id").longValue();

            // User A's build list contains only User A's build
            MockHttpServletResponse listAResponse = mockMvc.perform(
                    get("/profiles/" + userA.getId() + "/builds")).andReturn().getResponse();
            assertEquals(200, listAResponse.getStatus());
            ArrayNode buildsA = (ArrayNode) objectMapper.readTree(listAResponse.getContentAsString());
            assertEquals(1, buildsA.size(), "User A should have exactly one build");
            assertEquals(buildIdA.longValue(), buildsA.get(0).get("id").longValue());
            assertEquals(userA.getId(), buildsA.get(0).get("userProfileId").longValue());

            // User B's build list contains only User B's build
            MockHttpServletResponse listBResponse = mockMvc.perform(
                    get("/profiles/" + userB.getId() + "/builds")).andReturn().getResponse();
            assertEquals(200, listBResponse.getStatus());
            ArrayNode buildsB = (ArrayNode) objectMapper.readTree(listBResponse.getContentAsString());
            assertEquals(1, buildsB.size(), "User B should have exactly one build");
            assertEquals(buildIdB.longValue(), buildsB.get(0).get("id").longValue());
            assertEquals(userB.getId(), buildsB.get(0).get("userProfileId").longValue());

            // User A cannot create a build using User B's preference
            ObjectNode crossBuildJson = objectMapper.createObjectNode();
            crossBuildJson.put("buildTitle", "Cross-User Attempt");

            MockHttpServletResponse crossResponse = mockMvc.perform(
                            post("/profiles/" + userA.getId() + "/preferences/" + prefIdB + "/builds")
                                    .contentType("application/json").content(crossBuildJson.toString()))
                    .andReturn().getResponse();
            assertEquals(400, crossResponse.getStatus(),
                    "Creating a build under another user's preference should be rejected");
        } finally {
            if (buildIdA != null) buildRepository.deleteById(buildIdA);
            if (buildIdB != null) buildRepository.deleteById(buildIdB);
            if (prefIdA != null) userPreferenceRepository.deleteById(prefIdA);
            if (prefIdB != null) userPreferenceRepository.deleteById(prefIdB);
            userProfileRepository.deleteById(userA.getId());
            userProfileRepository.deleteById(userB.getId());
        }
    }

    // -----------------------------------------------------------------------
    // Test 8: Profile update does not break the downstream workflow
    // -----------------------------------------------------------------------
    @Test
    void fullUserJourney_updateProfileThenContinueWorkflow() throws Exception {
        String originalEmail = "system-journey-8-before@test.com";
        String updatedEmail = "system-journey-8-after@test.com";
        deleteExistingProfile(originalEmail);
        deleteExistingProfile(updatedEmail);

        Long profileId = null;
        Long preferenceId = null;
        Long buildId = null;
        try {
            // Register with original email
            ObjectNode profileJson = objectMapper.createObjectNode();
            profileJson.put("email", originalEmail);
            profileJson.put("password", "oldPass");
            profileJson.put("firstName", "Before");
            profileJson.put("lastName", "Update");

            MockHttpServletResponse registerResponse = mockMvc.perform(
                            post("/profiles").contentType("application/json")
                                    .content(profileJson.toString()))
                    .andReturn().getResponse();
            assertEquals(201, registerResponse.getStatus());
            profileId = objectMapper.readValue(registerResponse.getContentAsString(), ObjectNode.class)
                    .get("id").longValue();

            // Verify login works with original credentials
            ObjectNode loginJson = objectMapper.createObjectNode();
            loginJson.put("email", originalEmail);
            loginJson.put("password", "oldPass");
            assertEquals(200, mockMvc.perform(post("/profiles/login").contentType("application/json")
                    .content(loginJson.toString())).andReturn().getResponse().getStatus());

            // Update profile: new email, new name, new password
            ObjectNode updateJson = objectMapper.createObjectNode();
            updateJson.put("email", updatedEmail);
            updateJson.put("firstName", "After");
            updateJson.put("password", "newPass");

            MockHttpServletResponse updateResponse = mockMvc.perform(
                            put("/profiles/" + profileId).contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn().getResponse();
            assertEquals(200, updateResponse.getStatus());

            ObjectNode updateResult = objectMapper.readValue(updateResponse.getContentAsString(), ObjectNode.class);
            assertEquals("After", updateResult.get("firstName").textValue());
            assertEquals(updatedEmail, updateResult.get("email").textValue());

            // Old email login should now fail
            ObjectNode oldLoginJson = objectMapper.createObjectNode();
            oldLoginJson.put("email", originalEmail);
            oldLoginJson.put("password", "oldPass");
            assertEquals(401, mockMvc.perform(post("/profiles/login").contentType("application/json")
                    .content(oldLoginJson.toString())).andReturn().getResponse().getStatus(),
                    "Login with old email should fail after email update");

            // New email login should succeed
            ObjectNode newLoginJson = objectMapper.createObjectNode();
            newLoginJson.put("email", updatedEmail);
            newLoginJson.put("password", "newPass");
            assertEquals(200, mockMvc.perform(post("/profiles/login").contentType("application/json")
                    .content(newLoginJson.toString())).andReturn().getResponse().getStatus(),
                    "Login with new email should succeed after email update");

            // Downstream workflow still works: create preference and get recommendation
            preferenceId = createPreferenceForProfile(profileId, "WORKSTATION", 2000);

            MockHttpServletResponse recResponse = mockMvc.perform(
                    get("/recommendations/" + preferenceId)).andReturn().getResponse();
            assertEquals(200, recResponse.getStatus(),
                    "Recommendation should work normally after profile update");

            ObjectNode recResult = objectMapper.readValue(recResponse.getContentAsString(), ObjectNode.class);
            buildId = recResult.get("buildId").longValue();
            assertTrue(recResult.get("totalPrice").decimalValue().compareTo(new BigDecimal("2000")) <= 0);
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            if (preferenceId != null) userPreferenceRepository.deleteById(preferenceId);
            if (profileId != null) userProfileRepository.deleteById(profileId);
            deleteExistingProfile(updatedEmail);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private UserProfile saveUserProfile(String email, String firstName, String lastName) {
        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setPassword("secret123");
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        return userProfileRepository.save(profile);
    }

    private Long createPreferenceForProfile(Long profileId, String category, int budget) throws Exception {
        ObjectNode preferenceJson = objectMapper.createObjectNode();
        preferenceJson.put("buildCategory", category);
        preferenceJson.put("maxBudget", budget);

        MockHttpServletResponse response = mockMvc.perform(
                        post("/profiles/" + profileId + "/preferences")
                                .contentType("application/json")
                                .content(preferenceJson.toString()))
                .andReturn().getResponse();
        assertEquals(200, response.getStatus());
        return objectMapper.readValue(response.getContentAsString(), ObjectNode.class).get("id").longValue();
    }

    private void deleteExistingProfile(String email) {
        userProfileRepository.findByEmailIgnoreCase(email)
                .ifPresent(p -> userProfileRepository.deleteById(p.getId()));
    }
}
