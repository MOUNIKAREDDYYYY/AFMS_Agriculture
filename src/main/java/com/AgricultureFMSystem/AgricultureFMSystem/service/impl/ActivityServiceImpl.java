package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.dto.request.ActivityRequestDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.ActivityResponseDTO;
import com.AgricultureFMSystem.AgricultureFMSystem.dto.response.PagedResponse;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Activity;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Crop;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.Field;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ActivityStatus;
import com.AgricultureFMSystem.AgricultureFMSystem.entity.enums.ActivityType;
import com.AgricultureFMSystem.AgricultureFMSystem.exception.ResourceNotFoundException;
import com.AgricultureFMSystem.AgricultureFMSystem.mapper.ActivityMapper;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.ActivityRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.CropRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.repository.FieldRepository;
import com.AgricultureFMSystem.AgricultureFMSystem.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================================
 * Phase 2 & 3: Activity Service Implementation (Activity Tracking)
 * ============================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final FieldRepository fieldRepository;
    private final CropRepository cropRepository;
    private final ActivityMapper activityMapper;

    @Override
    @Transactional
    public ActivityResponseDTO createActivity(ActivityRequestDTO requestDTO) {
        log.info("Scheduling activity '{}' for field ID {}", requestDTO.getTitle(), requestDTO.getFieldId());

        Field field = fieldRepository.findById(requestDTO.getFieldId())
                .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));

        Crop crop = null;
        if (requestDTO.getCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));
        } else if (field.getCurrentCrop() != null) {
            crop = field.getCurrentCrop();
        }

        Activity activity = activityMapper.toEntity(requestDTO, field, crop);
        Activity saved = activityRepository.save(activity);
        return activityMapper.toResponseDTO(saved);
    }

    @Override
    public ActivityResponseDTO getActivityById(Long id) {
        log.debug("Fetching activity ID: {}", id);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        return activityMapper.toResponseDTO(activity);
    }

    @Override
    public PagedResponse<ActivityResponseDTO> getAllActivities(Pageable pageable) {
        log.debug("Fetching all activities with pagination");
        Page<ActivityResponseDTO> page = activityRepository.findAll(pageable)
                .map(activityMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ActivityResponseDTO> getActivitiesByFieldId(Long fieldId, Pageable pageable) {
        log.debug("Fetching activities for field ID: {}", fieldId);
        if (!fieldRepository.existsById(fieldId)) {
            throw new ResourceNotFoundException("Field", "id", fieldId);
        }
        Page<ActivityResponseDTO> page = activityRepository.findByFieldId(fieldId, pageable)
                .map(activityMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ActivityResponseDTO> getActivitiesByStatus(ActivityStatus status, Pageable pageable) {
        log.debug("Fetching activities by status: {}", status);
        Page<ActivityResponseDTO> page = activityRepository.findByStatus(status, pageable)
                .map(activityMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ActivityResponseDTO> getActivitiesByType(ActivityType activityType, Pageable pageable) {
        log.debug("Fetching activities by type: {}", activityType);
        Page<ActivityResponseDTO> page = activityRepository.findByActivityType(activityType, pageable)
                .map(activityMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ActivityResponseDTO> getActivitiesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Fetching activities scheduled between {} and {}", startDate, endDate);
        Page<ActivityResponseDTO> page = activityRepository.findByScheduledDateBetween(startDate, endDate, pageable)
                .map(activityMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public PagedResponse<ActivityResponseDTO> searchActivities(String query, Pageable pageable) {
        log.debug("Searching activities with query: {}", query);
        Page<ActivityResponseDTO> page = activityRepository.searchActivities(query, pageable)
                .map(activityMapper::toResponseDTO);
        return PagedResponse.of(page);
    }

    @Override
    public List<ActivityResponseDTO> getPendingActivities() {
        log.debug("Retrieving pending farming activities");
        return activityRepository.findPendingActivities().stream()
                .map(activityMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ActivityResponseDTO updateActivity(Long id, ActivityRequestDTO requestDTO) {
        log.info("Updating activity ID: {}", id);
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));

        Field field = null;
        if (requestDTO.getFieldId() != null) {
            field = fieldRepository.findById(requestDTO.getFieldId())
                    .orElseThrow(() -> new ResourceNotFoundException("Field", "id", requestDTO.getFieldId()));
        }

        Crop crop = null;
        if (requestDTO.getCropId() != null) {
            crop = cropRepository.findById(requestDTO.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop", "id", requestDTO.getCropId()));
        }

        activityMapper.updateEntityFromDTO(requestDTO, existing, field, crop);
        Activity saved = activityRepository.save(existing);
        return activityMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ActivityResponseDTO patchActivity(Long id, Map<String, Object> updates) {
        log.info("Applying partial updates to activity ID: {}", id);
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> existing.setTitle((String) value);
                case "activityType" -> existing.setActivityType(ActivityType.valueOf(value.toString()));
                case "status" -> existing.setStatus(ActivityStatus.valueOf(value.toString()));
                case "scheduledDate" -> existing.setScheduledDate(LocalDate.parse(value.toString()));
                case "completionDate" -> existing.setCompletionDate(value != null ? LocalDate.parse(value.toString()) : null);
                case "assignedTo" -> existing.setAssignedTo((String) value);
                case "notes" -> existing.setNotes((String) value);
            }
        });

        Activity saved = activityRepository.save(existing);
        return activityMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        log.info("Deleting activity ID: {}", id);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        activityRepository.delete(activity);
    }

    @Override
    @Transactional
    public ActivityResponseDTO completeActivity(Long id, LocalDate completionDate, String notes) {
        log.info("Marking activity ID {} as completed on {}", id, completionDate);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));

        activity.setStatus(ActivityStatus.COMPLETED);
        activity.setCompletionDate(completionDate != null ? completionDate : LocalDate.now());
        if (notes != null && !notes.isBlank()) {
            activity.setNotes(activity.getNotes() != null ? activity.getNotes() + " | " + notes : notes);
        }

        Activity saved = activityRepository.save(activity);
        return activityMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ActivityResponseDTO updateActivityStatus(Long id, ActivityStatus status) {
        log.info("Setting status of activity {} to {}", id, status);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        activity.setStatus(status);
        if (status == ActivityStatus.COMPLETED && activity.getCompletionDate() == null) {
            activity.setCompletionDate(LocalDate.now());
        }
        Activity saved = activityRepository.save(activity);
        return activityMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ActivityResponseDTO cancelActivity(Long id) {
        log.info("Cancelling activity ID: {}", id);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
        activity.setStatus(ActivityStatus.CANCELLED);
        Activity saved = activityRepository.save(activity);
        return activityMapper.toResponseDTO(saved);
    }
}
