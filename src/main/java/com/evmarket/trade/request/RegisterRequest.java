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
@Schema(description = "User registration request")
public class RegisterRequest {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username length must be 3-50")
    @Schema(description = "Username", example = "")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    @Schema(description = "Password", example = "")
    private String password;

    @NotBlank(message = "confirmPassword is required")
    @Schema(description = "Confirm password", example = "")
    private String confirmPassword;

    @NotBlank(message = "fullName is required")
    @Schema(description = "Full name", example = "")
    private String fullName;

    @NotNull(message = "dateOfBirth is required")
    @Schema(description = "Date of birth", example = "")
    private LocalDate dateOfBirth;

    @NotBlank(message = "sex is required")
    @Schema(description = "Gender", example = "")
    private String sex;

    @NotBlank(message = "identityCard is required")
    @Pattern(regexp = "^\\d{9,20}$", message = "identityCard must be 9-20 digits")
    @Schema(description = "Identity card number", example = "")
    private String identityCard;

    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    @Schema(description = "Email address", example = "")
    private String email;

    @NotBlank(message = "address is required")
    @Schema(description = "Address", example = "")
    private String address;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^(\\+?84|0)\\d{9,10}$", message = "phoneNumber is invalid")
    @Schema(description = "Phone number", example = "")
    private String phoneNumber;
}


