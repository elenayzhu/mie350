package com.team15.partpicker.test;

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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
}
