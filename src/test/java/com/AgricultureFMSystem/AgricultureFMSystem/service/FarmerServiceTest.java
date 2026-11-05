package com.AgricultureFMSystem.AgricultureFMSystem.service;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmerRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.DuplicateResourceException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.FarmerMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmerRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.impl.FarmerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ============================================================================================
 * Phase 5: Testing with JUnit 5 & Mockito - FarmerServiceTest
 * ============================================================================================
 * Demonstrates:
 * - Mocking dependencies with @Mock (FarmerRepository, FarmerMapper)
 * - Injecting mocks into the service implementation with @InjectMocks
 * - Verifying business rules, duplicate detection, and exception assertions
 */
@ExtendWith(MockitoExtension.class)
class FarmerServiceTest {

    @Mock
    private FarmerRepository farmerRepository;

    @Mock
    private FarmerMapper farmerMapper;

    @InjectMocks
    private FarmerServiceImpl farmerService;

    private FarmerRequestDTO requestDTO;
    private Farmer farmer;
    private FarmerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = FarmerRequestDTO.builder()
                .fullName("Ramesh Patel")
                .email("ramesh.patel@test.com")
                .phoneNumber("+91-9988776655")
                .address("Village Mehsana, Gujarat")
                .active(true)
                .build();

        farmer = Farmer.builder()
                .id(1L)
                .fullName("Ramesh Patel")
                .email("ramesh.patel@test.com")
                .phoneNumber("+91-9988776655")
                .address("Village Mehsana, Gujarat")
                .active(true)
                .build();

        responseDTO = FarmerResponseDTO.builder()
                .id(1L)
                .fullName("Ramesh Patel")
                .email("ramesh.patel@test.com")
                .phoneNumber("+91-9988776655")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new farmer")
    void createFarmer_Success() {
        // Arrange
        when(farmerRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(farmerMapper.toEntity(requestDTO)).thenReturn(farmer);
        when(farmerRepository.save(farmer)).thenReturn(farmer);
        when(farmerMapper.toResponseDTO(farmer)).thenReturn(responseDTO);

        // Act
        FarmerResponseDTO result = farmerService.createFarmer(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ramesh.patel@test.com", result.getEmail());
        verify(farmerRepository, times(1)).save(farmer);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email is already taken")
    void createFarmer_DuplicateEmail_ThrowsException() {
        // Arrange
        when(farmerRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            farmerService.createFarmer(requestDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(farmerRepository, never()).save(any(Farmer.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when querying non-existent farmer ID")
    void getFarmerById_NotFound_ThrowsException() {
        // Arrange
        when(farmerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            farmerService.getFarmerById(99L);
        });

        verify(farmerRepository, times(1)).findById(99L);
    }
}
