package com.peter.save_bulgaria.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.peter.save_bulgaria.model.User;
import com.peter.save_bulgaria.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @Value("${google.oauth2.client-id:688975825427-m6mjn358n71bqeo3cfs77ucjplmt2v03.apps.googleusercontent.com}")
    private String clientId;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");
        log.info("Google login attempt");

        if (idTokenString == null || idTokenString.trim().isEmpty()) {
            log.warn("Missing ID token in request");
            return ResponseEntity.badRequest().body(Map.of("error", "Missing ID token"));
        }

        try {
            var transport = GoogleNetHttpTransport.newTrustedTransport();
            var jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String googleId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                log.info("Valid Google token for user: {}", email);

                // Use UserService to find or create user
                User user = userService.findOrCreateUserByGoogleAuth(googleId, email, name);

                return ResponseEntity.ok(Map.of(
                        "user", Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "name", user.getName(),
                                "googleId", user.getGoogleId()
                        ),
                        "message", "Login successful"
                ));
            } else {
                log.warn("Invalid ID token provided");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid ID token"));
            }
        } catch (Exception e) {
            log.error("Error during Google authentication", e);
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("User logout");
        // Since we're using stateless authentication, logout is mainly client-side
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}