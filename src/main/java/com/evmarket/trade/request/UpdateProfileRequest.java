package com.evmarket.trade.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile update request")
public class UpdateProfileRequest {
    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^(\\+?84|0)\\d{9,10}$", message = "phone is invalid")
    @Schema(description = "Phone number", example = "0123456789")
    private String phone;
    
    @NotBlank(message = "address is required")
    @Schema(description = "Address", example = "123 Main Street, City")
    private String address;
    
    @NotNull(message = "dateOfBirth is required")
    @Schema(description = "Date of birth", example = "2000-01-01")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "gender is required")
    @Schema(description = "Gender", example = "male", allowableValues = {"male", "female", "other"})
    private String gender;
    
    @NotBlank(message = "identityCard is required")
    @Pattern(regexp = "^\\d{9,20}$", message = "identityCard must be 9-20 digits")
    @Schema(description = "Identity card number", example = "123456789012")
    private String identityCard;
}

