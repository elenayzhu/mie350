package com.team15.partpicker.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CatalogTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCpusWithFilters() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/cpus")
                                .param("brand", "AMD")
                                .param("socket", "AM5")
                                .param("minCores", "8")
                                .param("maxPrice", "350"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0);

        for (int i = 0; i < receivedJson.size(); i++) {
            ObjectNode cpuJson = (ObjectNode) receivedJson.get(i);

            assertEquals("AMD", cpuJson.get("brand").textValue());
            assertEquals("AM5", cpuJson.get("socket").textValue());
            assertTrue(cpuJson.get("cores").intValue() >= 8);
            assertTrue(cpuJson.get("price").decimalValue().compareTo(new BigDecimal("350")) <= 0);
        }
    }

    @Test
    void cpuCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("cpu");

        ObjectNode createJson = json(
                "model", "Integration CPU Alpha " + token,
                "brand", "IntegrationBrandCpu",
                "price", new BigDecimal("219.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration CPU Beta " + token,
                "brand", "UpdatedBrandCpu",
                "price", new BigDecimal("249.99")
        );

        exerciseCatalog(
                "/cpus",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandCpu"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandCpu",
                        "maxPrice", "250"
                )
        );
    }

    @Test
    void gpuCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("gpu");

        ObjectNode createJson = json(
                "model", "Integration GPU Alpha " + token,
                "brand", "IntegrationBrandGpu",
                "price", new BigDecimal("499.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration GPU Beta " + token,
                "brand", "UpdatedBrandGpu",
                "price", new BigDecimal("549.99")
        );

        exerciseCatalog(
                "/gpus",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandGpu"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandGpu",
                        "maxPrice", "550"
                )
        );
    }

    @Test
    void motherboardCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("motherboard");

        ObjectNode createJson = json(
                "model", "Integration Motherboard Alpha " + token,
                "brand", "IntegrationBrandMotherboard",
                "price", new BigDecimal("189.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Motherboard Beta " + token,
                "brand", "UpdatedBrandMotherboard",
                "price", new BigDecimal("159.99")
        );

        exerciseCatalog(
                "/motherboards",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandMotherboard"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandMotherboard",
                        "maxPrice", "160"
                )
        );
    }

    @Test
    void ramCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("ram");

        ObjectNode createJson = json(
                "model", "Integration RAM Alpha " + token,
                "brand", "IntegrationBrandRam",
                "price", new BigDecimal("129.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration RAM Beta " + token,
                "brand", "UpdatedBrandRam",
                "price", new BigDecimal("179.99")
        );

        exerciseCatalog(
                "/rams",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandRam"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandRam",
                        "maxPrice", "180"
                )
        );
    }

    @Test
    void storageCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("storage");

        ObjectNode createJson = json(
                "model", "Integration Storage Alpha " + token,
                "brand", "IntegrationBrandStorage",
                "price", new BigDecimal("109.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Storage Beta " + token,
                "brand", "UpdatedBrandStorage",
                "price", new BigDecimal("149.99")
        );

        exerciseCatalog(
                "/storages",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandStorage"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandStorage",
                        "maxPrice", "150"
                )
        );
    }

    @Test
    void psuCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("psu");

        ObjectNode createJson = json(
                "model", "Integration PSU Alpha " + token,
                "brand", "IntegrationBrandPsu",
                "price", new BigDecimal("139.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration PSU Beta " + token,
                "brand", "UpdatedBrandPsu",
                "price", new BigDecimal("169.99")
        );

        exerciseCatalog(
                "/psus",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandPsu"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandPsu",
                        "maxPrice", "170"
                )
        );
    }

    @Test
    void coolerCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("cooler");

        ObjectNode createJson = json(
                "model", "Integration Cooler Alpha " + token,
                "brand", "IntegrationBrandCooler",
                "price", new BigDecimal("59.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Cooler Beta " + token,
                "brand", "UpdatedBrandCooler",
                "price", new BigDecimal("149.99")
        );

        exerciseCatalog(
                "/coolers",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandCooler"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandCooler",
                        "maxPrice", "150"
                )
        );
    }

    @Test
    void caseCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("case");

        ObjectNode createJson = json(
                "model", "Integration Case Alpha " + token,
                "brand", "IntegrationBrandCase",
                "price", new BigDecimal("99.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Case Beta " + token,
                "brand", "UpdatedBrandCase",
                "price", new BigDecimal("149.99")
        );

        exerciseCatalog(
                "/cases",
                createJson,
                updateJson,
                params(
                        "query", "Alpha " + token,
                        "brand", "IntegrationBrandCase"
                ),
                params(
                        "query", "Beta " + token,
                        "brand", "UpdatedBrandCase",
                        "maxPrice", "150"
                )
        );
    }

    @Test
    void getCpuNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/cpus/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getGpuNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/gpus/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getMotherboardNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/motherboards/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getRamNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/rams/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getStorageNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/storages/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getPsuNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/psus/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getCoolerNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/coolers/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getCaseNotFound_returns404() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/cases/999999"))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    @Test
    void getGpusWithFilters() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/gpus")
                                .param("brand", "NVIDIA")
                                .param("minVramGb", "8")
                                .param("maxPrice", "800"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0);

        for (int i = 0; i < receivedJson.size(); i++) {
            ObjectNode gpuJson = (ObjectNode) receivedJson.get(i);
            assertEquals("NVIDIA", gpuJson.get("brand").textValue());
            assertTrue(gpuJson.get("vramGb").intValue() >= 8);
            assertTrue(gpuJson.get("price").decimalValue().compareTo(new BigDecimal("800")) <= 0);
        }
    }

    @Test
    void getMotherboardsWithFilters() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/motherboards")
                                .param("brand", "ASUS")
                                .param("socket", "AM5")
                                .param("maxPrice", "300"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0);

        for (int i = 0; i < receivedJson.size(); i++) {
            ObjectNode moboJson = (ObjectNode) receivedJson.get(i);
            assertEquals("ASUS", moboJson.get("brand").textValue());
            assertEquals("AM5", moboJson.get("socket").textValue());
            assertTrue(moboJson.get("price").decimalValue().compareTo(new BigDecimal("300")) <= 0);
        }
    }

    @Test
    void getRamsWithFilters() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/rams")
                                .param("brand", "Corsair")
                                .param("ddrType", "DDR5")
                                .param("maxPrice", "200"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0);

        for (int i = 0; i < receivedJson.size(); i++) {
            ObjectNode ramJson = (ObjectNode) receivedJson.get(i);
            assertEquals("Corsair", ramJson.get("brand").textValue());
            assertEquals("DDR5", ramJson.get("ddrType").textValue());
            assertTrue(ramJson.get("price").decimalValue().compareTo(new BigDecimal("200")) <= 0);
        }
    }

    @Test
    void getPsusWithFilters() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/psus")
                                .param("brand", "Corsair")
                                .param("minWattage", "750")
                                .param("maxPrice", "200"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0);

        for (int i = 0; i < receivedJson.size(); i++) {
            ObjectNode psuJson = (ObjectNode) receivedJson.get(i);
            assertEquals("Corsair", psuJson.get("brand").textValue());
            assertTrue(psuJson.get("wattage").intValue() >= 750);
            assertTrue(psuJson.get("price").decimalValue().compareTo(new BigDecimal("200")) <= 0);
        }
    }

    @Test
    void getCoolersWithFilters() throws Exception {
        // All coolers in the seeded catalog have socket=NULL (universal mounting),
        // so socket-based filtering returns no results. Use brand + type + maxPrice instead.
        MockHttpServletResponse response = mockMvc.perform(
                        get("/coolers")
                                .param("brand", "Thermalright")
                                .param("type", "Air")
                                .param("maxPrice", "40"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0);

        for (int i = 0; i < receivedJson.size(); i++) {
            ObjectNode coolerJson = (ObjectNode) receivedJson.get(i);
            assertEquals("Thermalright", coolerJson.get("brand").textValue());
            assertEquals("Air", coolerJson.get("type").textValue());
            assertTrue(coolerJson.get("price").decimalValue().compareTo(new BigDecimal("40")) <= 0);
        }
    }

    @Test
    void getCpusWithNoFilters_returnsNonEmptyList() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/cpus"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertTrue(receivedJson.size() > 0, "CPU catalog should not be empty");
    }

    @Test
    void getCpusWithNoResults_returnsEmptyList() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/cpus").param("brand", "NonExistentBrand_XYZ_123"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        ArrayNode receivedJson = objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
        assertEquals(0, receivedJson.size(), "Unknown brand should return empty list");
    }

    @Test
    void updateNonexistentCpu_returns404() throws Exception {
        ObjectNode updateJson = json("model", "Ghost CPU", "brand", "Ghost Brand", "price", new BigDecimal("199.99"));

        MockHttpServletResponse response = mockMvc.perform(
                        put("/cpus/999999")
                                .contentType("application/json")
                                .content(updateJson.toString()))
                .andReturn()
                .getResponse();

        assertEquals(404, response.getStatus());
    }

    private void exerciseCatalog(
            String endpoint,
            ObjectNode createJson,
            ObjectNode updateJson,
            Map<String, String> createSearchParams,
            Map<String, String> updateSearchParams
    ) throws Exception {
        Long partId = null;
        Long deletedId = null;

        try {
            MockHttpServletResponse createResponse = mockMvc.perform(
                            post(endpoint)
                                    .contentType("application/json")
                                    .content(createJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(201, createResponse.getStatus());

            ObjectNode createdPartJson = objectMapper.readValue(createResponse.getContentAsString(), ObjectNode.class);
            partId = createdPartJson.get("id").longValue();
            assertJsonContainsAll(createdPartJson, createJson);

            ArrayNode createSearchResults = performSearch(endpoint, createSearchParams);
            assertTrue(containsId(createSearchResults, partId));

            MockHttpServletResponse updateResponse = mockMvc.perform(
                            put(endpoint + "/" + partId)
                                    .contentType("application/json")
                                    .content(updateJson.toString()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, updateResponse.getStatus());

            ObjectNode updatedPartJson = objectMapper.readValue(updateResponse.getContentAsString(), ObjectNode.class);
            assertEquals(partId.longValue(), updatedPartJson.get("id").longValue());
            assertJsonContainsAll(updatedPartJson, updateJson);
            assertJsonContainsAllExcept(updatedPartJson, createJson, "model", "brand", "price");

            MockHttpServletResponse getResponse = mockMvc.perform(get(endpoint + "/" + partId))
                    .andReturn()
                    .getResponse();

            assertEquals(200, getResponse.getStatus());

            ObjectNode fetchedPartJson = objectMapper.readValue(getResponse.getContentAsString(), ObjectNode.class);
            assertJsonContainsAll(fetchedPartJson, updateJson);
            assertJsonContainsAllExcept(fetchedPartJson, createJson, "model", "brand", "price");

            ArrayNode updateSearchResults = performSearch(endpoint, updateSearchParams);
            assertTrue(containsId(updateSearchResults, partId));

            MockHttpServletResponse deleteResponse = mockMvc.perform(delete(endpoint + "/" + partId))
                    .andReturn()
                    .getResponse();

            assertEquals(204, deleteResponse.getStatus());

            deletedId = partId;
            partId = null;

            MockHttpServletResponse missingResponse = mockMvc.perform(get(endpoint + "/" + deletedId))
                    .andReturn()
                    .getResponse();

            assertEquals(404, missingResponse.getStatus());

            ArrayNode afterDeleteSearchResults = performSearch(endpoint, updateSearchParams);
            assertFalse(containsId(afterDeleteSearchResults, deletedId));
        } finally {
            if (partId != null) {
                mockMvc.perform(delete(endpoint + "/" + partId)).andReturn();
            }
        }
    }

    private ArrayNode performSearch(String endpoint, Map<String, String> searchParams) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(endpoint);
        for (Map.Entry<String, String> entry : searchParams.entrySet()) {
            requestBuilder = requestBuilder.param(entry.getKey(), entry.getValue());
        }

        MockHttpServletResponse response = mockMvc.perform(requestBuilder)
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        return objectMapper.readValue(response.getContentAsString(), ArrayNode.class);
    }

    private boolean containsId(ArrayNode jsonArray, Long id) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonNode node = jsonArray.get(i);
            if (node.hasNonNull("id") && node.get("id").longValue() == id) {
                return true;
            }
        }
        return false;
    }

    private void assertJsonContainsAll(ObjectNode actualJson, ObjectNode expectedJson) {
        expectedJson.fields().forEachRemaining(field -> {
            JsonNode actualValue = actualJson.get(field.getKey());
            assertNotNull(actualValue, "Missing field: " + field.getKey());
            assertJsonValueEquals(field.getKey(), field.getValue(), actualValue);
        });
    }

    private void assertJsonContainsAllExcept(ObjectNode actualJson, ObjectNode expectedJson, String... excludedFields) {
        expectedJson.fields().forEachRemaining(field -> {
            if (isExcluded(field.getKey(), excludedFields)) {
                return;
            }

            JsonNode actualValue = actualJson.get(field.getKey());
            assertNotNull(actualValue, "Missing field: " + field.getKey());
            assertJsonValueEquals(field.getKey(), field.getValue(), actualValue);
        });
    }

    private void assertJsonValueEquals(String fieldName, JsonNode expectedValue, JsonNode actualValue) {
        if (expectedValue.isNumber()) {
            assertEquals(0, expectedValue.decimalValue().compareTo(actualValue.decimalValue()), fieldName);
            return;
        }

        if (expectedValue.isBoolean()) {
            assertEquals(expectedValue.booleanValue(), actualValue.booleanValue(), fieldName);
            return;
        }

        assertEquals(expectedValue.textValue(), actualValue.textValue(), fieldName);
    }

    private boolean isExcluded(String fieldName, String... excludedFields) {
        for (String excludedField : excludedFields) {
            if (excludedField.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    private ObjectNode json(Object... keyValuePairs) {
        ObjectNode json = objectMapper.createObjectNode();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = (String) keyValuePairs[i];
            Object value = keyValuePairs[i + 1];

            if (value instanceof String) {
                json.put(key, (String) value);
            } else if (value instanceof Integer) {
                json.put(key, (Integer) value);
            } else if (value instanceof Long) {
                json.put(key, (Long) value);
            } else if (value instanceof BigDecimal) {
                json.put(key, (BigDecimal) value);
            } else if (value instanceof Boolean) {
                json.put(key, (Boolean) value);
            } else {
                throw new IllegalArgumentException("Unsupported JSON value type for key " + key + ": " + value);
            }
        }
        return json;
    }

    private Map<String, String> params(String... keyValuePairs) {
        Map<String, String> params = new LinkedHashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            params.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return params;
    }

    private String uniqueToken(String prefix) {
        return prefix + "-" + System.nanoTime();
    }
}
