package io.steviemul.github.auth;

import java.time.Instant;

public record TokenResponse(String token, Instant expiresAt) {
}
