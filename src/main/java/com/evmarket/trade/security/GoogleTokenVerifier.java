package com.evmarket.trade.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
@Slf4j
public class GoogleTokenVerifier {
    
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public GoogleTokenVerifier() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Verify Google ID Token by calling Google's tokeninfo endpoint
     * This is the simplest and most reliable way to verify Google tokens
     * 
     * @param idToken The Google ID Token to verify
     * @return GoogleUserInfo if valid, empty otherwise
     */
    public Optional<GoogleUserInfo> verifyToken(String idToken) {
        try {
            // Call Google's tokeninfo API to verify the token
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GOOGLE_USER_INFO_URL + "?id_token=" + idToken))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse the response
                JsonNode jsonNode = objectMapper.readTree(response.body());
                
                // Check if token is valid (Google returns error if invalid)
                if (jsonNode.has("error_description")) {
                    log.warn("Google token verification failed: {}", jsonNode.get("error_description").asText());
                    return Optional.empty();
                }
                
                // Extract user information
                String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
                String sub = jsonNode.has("sub") ? jsonNode.get("sub").asText() : null;
                String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
                String picture = jsonNode.has("picture") ? jsonNode.get("picture").asText() : null;
                
                if (email == null || sub == null) {
                    log.warn("Missing required fields in Google token response");
                    return Optional.empty();
                }
                
                return Optional.of(new GoogleUserInfo(sub, email, name, picture));
            } else {
                log.warn("Google token verification failed with status: {}", response.statusCode());
                return Optional.empty();
            }
            
        } catch (Exception e) {
            log.error("Error verifying Google token", e);
            return Optional.empty();
        }
    }
    
    /**
     * Simple verification by decoding JWT (less secure, faster)
     * Only use this in development/testing
     * For production, use verifyToken() method above
     */
    public Optional<GoogleUserInfo> decodeTokenSimple(String idToken) {
        try {
            // JWT format: Header.Payload.Signature
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }
            
            // Decode payload (second part)
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);
            
            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(payloadJson);
            
            String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
            String sub = jsonNode.has("sub") ? jsonNode.get("sub").asText() : null;
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
            String picture = jsonNode.has("picture") ? jsonNode.get("picture").asText() : null;
            
            if (email == null || sub == null) {
                return Optional.empty();
            }
            
            return Optional.of(new GoogleUserInfo(sub, email, name, picture));
            
        } catch (Exception e) {
            log.error("Error decoding Google token", e);
            return Optional.empty();
        }
    }
    
    /**
     * Google User Info data class
     */
    public static class GoogleUserInfo {
        private final String providerId;
        private final String email;
        private final String fullName;
        private final String profilePicture;
        
        public GoogleUserInfo(String providerId, String email, String fullName, String profilePicture) {
            this.providerId = providerId;
            this.email = email;
            this.fullName = fullName;
            this.profilePicture = profilePicture;
        }
        
        public String getProviderId() {
            return providerId;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public String getProfilePicture() {
            return profilePicture;
        }
    }
}



