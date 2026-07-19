package io.steviemul.github.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record DispatchRequest(
    @JsonProperty("event_type")
    String eventType,
    @JsonProperty("client_payload")
    Map<String, String> clientPayload) {
}
