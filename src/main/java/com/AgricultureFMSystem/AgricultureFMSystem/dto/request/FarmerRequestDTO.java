package com.AgricultureFMSystem.AgricultureFMSystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================================================
 * Phase 2: Request DTO with Jakarta Bean Validation (@Valid)
 * ============================================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerRequestDTO {

    @NotBlank(message = "Farmer full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+0-9\\-\\s()]{7,20}$", message = "Phone number format is invalid")
    private String phoneNumber;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 50, message = "Government ID cannot exceed 50 characters")
    private String governmentIdNumber;

    @Builder.Default
    private Boolean active = true;
}
