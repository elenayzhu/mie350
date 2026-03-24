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
    void cpuCatalogSupportsSearchAndCrud() throws Exception {
        String token = uniqueToken("cpu");

        ObjectNode createJson = json(
                "model", "Integration CPU Alpha " + token,
                "brand", "IntegrationBrandCpu",
                "socket", "CPU-SOCKET-A",
                "cores", 8,
                "tdp", 95,
                "price", new BigDecimal("219.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration CPU Beta " + token,
                "brand", "IntegrationBrandCpu",
                "socket", "CPU-SOCKET-B",
                "cores", 12,
                "tdp", 125,
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
                        "socket", "CPU-SOCKET-B",
                        "minCores", "12",
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
                "manufacturer", "NVIDIA",
                "lengthMm", 300,
                "vramGb", 12,
                "tdp", 220,
                "color", "Black",
                "price", new BigDecimal("499.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration GPU Beta " + token,
                "brand", "IntegrationBrandGpu",
                "manufacturer", "AMD",
                "lengthMm", 320,
                "vramGb", 16,
                "tdp", 250,
                "color", "White",
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
                        "minVramGb", "16",
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
                "ddrType", "DDR5",
                "socket", "MB-SOCKET-A",
                "formFactor", "ATX",
                "color", "Black",
                "memorySlots", 4,
                "price", new BigDecimal("189.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Motherboard Beta " + token,
                "brand", "IntegrationBrandMotherboard",
                "ddrType", "DDR5",
                "socket", "MB-SOCKET-B",
                "formFactor", "Micro-ATX",
                "color", "White",
                "memorySlots", 2,
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
                        "socket", "MB-SOCKET-B",
                        "formFactor", "Micro-ATX",
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
                "ddrType", "DDR5",
                "speedRatio", 180,
                "capacityGb", 32,
                "color", "Black",
                "price", new BigDecimal("129.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration RAM Beta " + token,
                "brand", "IntegrationBrandRam",
                "ddrType", "DDR5",
                "speedRatio", 200,
                "capacityGb", 64,
                "color", "White",
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
                        "ddrType", "DDR5",
                        "minSpeed", "200",
                        "minCapacity", "64",
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
                "type", "NVMe SSD",
                "capacityGb", 1000,
                "price", new BigDecimal("109.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Storage Beta " + token,
                "brand", "IntegrationBrandStorage",
                "type", "SATA SSD",
                "capacityGb", 2000,
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
                        "type", "SATA SSD",
                        "minCapacity", "2000",
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
                "wattage", 750,
                "efficiencyRating", "80+ Gold",
                "modularType", "Fully Modular",
                "color", "Black",
                "price", new BigDecimal("139.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration PSU Beta " + token,
                "brand", "IntegrationBrandPsu",
                "wattage", 850,
                "efficiencyRating", "80+ Platinum",
                "modularType", "Semi-Modular",
                "color", "White",
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
                        "minWattage", "850",
                        "efficiencyRating", "80+ Platinum",
                        "modularType", "Semi-Modular",
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
                "socket", "AM5",
                "maxTdp", 220,
                "type", "Air",
                "color", "Black",
                "price", new BigDecimal("59.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Cooler Beta " + token,
                "brand", "IntegrationBrandCooler",
                "socket", "LGA1700",
                "maxTdp", 280,
                "type", "AIO 360mm",
                "color", "White",
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
                        "socket", "LGA1700",
                        "type", "AIO 360mm",
                        "minMaxTdp", "280",
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
                "formFactor", "ATX",
                "maxGpuLengthMm", 360,
                "type", "Mid Tower",
                "color", "Black",
                "price", new BigDecimal("99.99")
        );

        ObjectNode updateJson = json(
                "model", "Integration Case Beta " + token,
                "brand", "IntegrationBrandCase",
                "formFactor", "Micro-ATX",
                "maxGpuLengthMm", 400,
                "type", "Full Tower",
                "color", "White",
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
                        "formFactor", "Micro-ATX",
                        "minMaxGpuLengthMm", "400",
                        "maxPrice", "150"
                )
        );
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

            MockHttpServletResponse getResponse = mockMvc.perform(get(endpoint + "/" + partId))
                    .andReturn()
                    .getResponse();

            assertEquals(200, getResponse.getStatus());

            ObjectNode fetchedPartJson = objectMapper.readValue(getResponse.getContentAsString(), ObjectNode.class);
            assertJsonContainsAll(fetchedPartJson, updateJson);

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
