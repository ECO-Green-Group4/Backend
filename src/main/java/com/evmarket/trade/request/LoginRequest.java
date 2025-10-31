package com.evmarket.trade.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Login request")
public class LoginRequest {
    @Schema(description = "Email", example = "user@example.com")
    private String email;
    
    @Schema(description = "Password", example = "")
    private String password;
}


