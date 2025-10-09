package com.evmarket.trade.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username length must be 3-50")
    @Schema(example = "")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    @Schema(example = "")
    private String password;

    @NotBlank(message = "confirmPassword is required")
    @Schema(example = "")
    private String confirmPassword;

    @NotBlank(message = "fullName is required")
    @Schema(example = "")
    private String fullName;

    @NotNull(message = "dateOfBirth is required")
    @Schema(example = "")
    private LocalDate dateOfBirth;

    @NotBlank(message = "sex is required")
    @Schema(example = "")
    private String sex;

    @NotBlank(message = "identityCard is required")
    @Pattern(regexp = "^\\d{9,20}$", message = "identityCard must be 9-20 digits")
    @Schema(example = "")
    private String identityCard;

    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    @Schema(example = "")
    private String email;

    @NotBlank(message = "address is required")
    @Schema(example = "")
    private String address;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^(\\+?84|0)\\d{9,10}$", message = "phoneNumber is invalid")
    @Schema(example = "")
    private String phoneNumber;
}


