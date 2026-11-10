package com.AgricultureFMSystem.AgricultureFMSystem.controller;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmerRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.security.AuthSecurityInterceptor;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FarmerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring Boot WebMvc slice test for {@link FarmerController} using MockMvc and @MockitoBean.
 */
@WebMvcTest(FarmerController.class)
class FarmerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FarmerService farmerService;

    @MockitoBean
    private AuthSecurityInterceptor authSecurityInterceptor;

    private FarmerResponseDTO farmerResponse;

    @BeforeEach
    void setUp() throws Exception {
        when(authSecurityInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        farmerResponse = FarmerResponseDTO.builder()
                .id(1L)
                .fullName("Ramesh Patel")
                .email("ramesh.patel@test.com")
                .phoneNumber("+91-9988776655")
                .address("Village Mehsana, Gujarat")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("GET /api/farmers/{id} returns farmer details when found")
    void getFarmerById_Success() throws Exception {
        when(farmerService.getFarmerById(1L)).thenReturn(farmerResponse);

        mockMvc.perform(get("/api/farmers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Ramesh Patel"))
                .andExpect(jsonPath("$.email").value("ramesh.patel@test.com"));
    }

    @Test
    @DisplayName("GET /api/farmers/{id} returns 404 when farmer does not exist")
    void getFarmerById_NotFound() throws Exception {
        when(farmerService.getFarmerById(99L))
                .thenThrow(new ResourceNotFoundException("Farmer", "id", 99L));

        mockMvc.perform(get("/api/farmers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/farmers creates farmer and returns 201 Created")
    void createFarmer_Success() throws Exception {
        when(farmerService.createFarmer(any(FarmerRequestDTO.class))).thenReturn(farmerResponse);

        String requestBody = """
                {
                  "fullName": "Ramesh Patel",
                  "email": "ramesh.patel@test.com",
                  "phoneNumber": "+91-9988776655",
                  "address": "Village Mehsana, Gujarat",
                  "active": true
                }
                """;

        mockMvc.perform(post("/api/farmers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Ramesh Patel"));
    }
}

