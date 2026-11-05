package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.FarmRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.FarmResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farm;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Farmer;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.FarmMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FarmerRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.FarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Farm Service Implementation
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final FarmerRepository farmerRepository;
    private final FarmMapper farmMapper;

    @Override
    @Transactional
    public FarmResponseDTO createFarm(FarmRequestDTO requestDTO) {
        log.info("Creating new farm '{}' for farmer ID {}", requestDTO.getName(), requestDTO.getFarmerId());

        Farmer farmer = farmerRepository.findById(requestDTO.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", requestDTO.getFarmerId()));

        Farm farm = farmMapper.toEntity(requestDTO, farmer);
        Farm saved = farmRepository.save(farm);

        log.info("Successfully created farm with ID: {}", saved.getId());
        return farmMapper.toResponseDTO(saved);
    }

    @Override
    public FarmResponseDTO getFarmById(Long id) {
        log.debug("Fetching farm by ID: {}", id);
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", id));
        return farmMapper.toResponseDTO(farm);
    }

    @Override
    public PagedResponse<FarmResponseDTO> getAllFarms(Pageable pageable) {
        log.debug("Fetching paginated list of all farms");
        Page<FarmResponseDTO> page = farmRepository.findAll(pageable)
                .map(farmMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FarmResponseDTO> getFarmsByFarmerId(Long farmerId, Pageable pageable) {
        log.debug("Fetching farms for farmer ID: {}", farmerId);
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer", "id", farmerId);
        }
        Page<FarmResponseDTO> page = farmRepository.findByFarmerId(farmerId, pageable)
                .map(farmMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<FarmResponseDTO> searchFarms(String query, Pageable pageable) {
        log.debug("Searching farms with keyword: {}", query);
        Page<FarmResponseDTO> page = farmRepository.searchFarms(query, pageable)
                .map(farmMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional
    public FarmResponseDTO updateFarm(Long id, FarmRequestDTO requestDTO) {
        log.info("Updating farm with ID: {}", id);
        Farm existing = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", id));

        Farmer farmer = null;
        if (requestDTO.getFarmerId() != null) {
            farmer = farmerRepository.findById(requestDTO.getFarmerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", requestDTO.getFarmerId()));
        }

        farmMapper.updateEntityFromDTO(requestDTO, existing, farmer);
        Farm updated = farmRepository.save(existing);
        return farmMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public FarmResponseDTO patchFarm(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to farm ID: {}", id);
        Farm existing = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existing.setName((String) value);
                case "location" -> existing.setLocation((String) value);
                case "totalAreaInAcres" -> existing.setTotalAreaInAcres(Double.parseDouble(value.toString()));
                case "farmerId" -> {
                    Long farmerId = Long.parseLong(value.toString());
                    Farmer farmer = farmerRepository.findById(farmerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", farmerId));
                    existing.setFarmer(farmer);
                }
            }
        });

        Farm saved = farmRepository.save(existing);
        return farmMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteFarm(Long id) {
        log.info("Deleting farm with ID: {}", id);
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm", "id", id));
        farmRepository.delete(farm);
    }
}
