package com.evmarket.trade.request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class RegisterRequest {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username length must be 3-50")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;

    @NotBlank(message = "confirmPassword is required")
    private String confirmPassword;

    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotNull(message = "dateOfBirth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "sex is required")
    private String sex;

    @NotBlank(message = "identityCard is required")
    @Pattern(regexp = "^\\d{9,20}$", message = "identityCard must be 9-20 digits")
    private String identityCard;

    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    private String email;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^(\\+?84|0)\\d{9,10}$", message = "phoneNumber is invalid")
    private String phoneNumber;
}


