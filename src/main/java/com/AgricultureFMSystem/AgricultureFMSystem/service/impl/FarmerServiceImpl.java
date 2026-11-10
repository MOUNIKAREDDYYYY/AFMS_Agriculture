package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmerRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmerResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.DuplicateResourceException;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.FarmerMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmerRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FarmerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Farmer Service Implementation with @Transactional & @Slf4j
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository farmerRepository;
    private final FarmerMapper farmerMapper;

    // Evicts all cached farmer entries when a new farmer registers
    @Override
    @Transactional
    @CacheEvict(value = "farmers", allEntries = true)
    public FarmerResponseDTO createFarmer(FarmerRequestDTO requestDTO) {
        log.info("Creating new farmer with email: {}", requestDTO.getEmail());

        if (farmerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Farmer with email '" + requestDTO.getEmail() + "' already exists.");
        }

        Farmer farmer = farmerMapper.toEntity(requestDTO);
        Farmer savedFarmer = farmerRepository.save(farmer);

        log.info("Successfully registered farmer with ID: {}", savedFarmer.getId());
        return farmerMapper.toResponseDTO(savedFarmer);
    }

    // Caches farmer profile by ID in "farmers" cache region
    @Override
    @Cacheable(value = "farmers", key = "#id")
    public FarmerResponseDTO getFarmerById(Long id) {
        log.debug("Fetching farmer details for ID (Database hit): {}", id);
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", id));
        return farmerMapper.toResponseDTO(farmer);
    }

    @Override
    public PagedResponse<FarmerResponseDTO> getAllFarmers(Pageable pageable) {
        log.debug("Fetching paginated list of all farmers");
        Page<FarmerResponseDTO> page = farmerRepository.findAll(pageable)
                .map(farmerMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FarmerResponseDTO> getFarmersByActiveStatus(Boolean active, Pageable pageable) {
        log.debug("Fetching farmers filtered by active status: {}", active);
        Page<FarmerResponseDTO> page = farmerRepository.findByActive(active, pageable)
                .map(farmerMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FarmerResponseDTO> searchFarmers(String query, Pageable pageable) {
        log.debug("Searching farmers with query: {}", query);
        Page<FarmerResponseDTO> page = farmerRepository.searchFarmers(query, pageable)
                .map(farmerMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    // Evicts stale farmer cache upon update
    @Override
    @Transactional
    @CacheEvict(value = "farmers", allEntries = true)
    public FarmerResponseDTO updateFarmer(Long id, FarmerRequestDTO requestDTO) {
        log.info("Updating farmer with ID: {}", id);
        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", id));

        // If email changed, check for conflict
        if (!existing.getEmail().equalsIgnoreCase(requestDTO.getEmail()) &&
                farmerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email '" + requestDTO.getEmail() + "' is already in use.");
        }

        farmerMapper.updateEntityFromDTO(requestDTO, existing);
        Farmer updated = farmerRepository.save(existing);
        return farmerMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "farmers", allEntries = true)
    public FarmerResponseDTO patchFarmer(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to farmer ID: {}", id);
        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "fullName" -> existing.setFullName(PatchHelper.requireNonBlankString("fullName", value));
                case "phoneNumber" -> existing.setPhoneNumber(PatchHelper.requireNonBlankString("phoneNumber", value));
                case "address" -> existing.setAddress(PatchHelper.optionalString("address", value));
                case "governmentIdNumber" -> existing.setGovernmentIdNumber(PatchHelper.optionalString("governmentIdNumber", value));
                case "active" -> existing.setActive(PatchHelper.requireBoolean("active", value));
                case "email" -> {
                    String newEmail = PatchHelper.requireNonBlankString("email", value);
                    if (!existing.getEmail().equalsIgnoreCase(newEmail) && farmerRepository.existsByEmail(newEmail)) {
                        throw new DuplicateResourceException("Email '" + newEmail + "' is already in use.");
                    }
                    existing.setEmail(newEmail);
                }
            }
        });

        Farmer saved = farmerRepository.save(existing);
        return farmerMapper.toResponseDTO(saved);
    }

    // Evicts cached farmer upon deletion
    @Override
    @Transactional
    @CacheEvict(value = "farmers", allEntries = true)
    public void deleteFarmer(Long id) {
        log.info("Deleting farmer with ID: {}", id);
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", id));
        farmerRepository.delete(farmer);
    }

    // Evicts cached farmer upon status toggle
    @Override
    @Transactional
    @CacheEvict(value = "farmers", allEntries = true)
    public FarmerResponseDTO toggleFarmerStatus(Long id) {
        log.info("Toggling active status for farmer ID: {}", id);
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", id));
        farmer.setActive(!farmer.getActive());
        Farmer saved = farmerRepository.save(farmer);
        return farmerMapper.toResponseDTO(saved);
    }
}