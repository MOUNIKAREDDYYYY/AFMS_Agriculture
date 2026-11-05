package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating and updating a Farm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmRequestDTO {

    @NotBlank(message = "Farm name is required")
    @Size(min = 2, max = 150, message = "Farm name must be between 2 and 150 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @NotNull(message = "Total area in acres is required")
    @Positive(message = "Total area must be a positive number greater than 0")
    private Double totalAreaInAcres;

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;
}
