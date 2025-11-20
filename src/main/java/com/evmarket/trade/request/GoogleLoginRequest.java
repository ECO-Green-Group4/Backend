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
@Schema(description = "Google OAuth2 login request")
public class GoogleLoginRequest {
    @Schema(description = "Google ID Token (JWT) from OAuth2 flow", 
            example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMjAzNDU2Nzg5MDEyMzQ1NiJ9...",
            required = true)
    private String idToken;
    
    // Deprecated - kept for backward compatibility but will be ignored
    @Deprecated
    @Schema(description = "DEPRECATED: Use idToken instead", example = "123456789012345678901")
    private String providerId;
    
    @Deprecated
    @Schema(description = "DEPRECATED: Use idToken instead", example = "user@gmail.com")
    private String email;
    
    @Deprecated
    @Schema(description = "DEPRECATED: Use idToken instead", example = "Nguyen Van A")
    private String fullName;
    
    @Deprecated
    @Schema(description = "DEPRECATED: Use idToken instead", example = "https://lh3.googleusercontent.com/a/default")
    private String profilePicture;
}

