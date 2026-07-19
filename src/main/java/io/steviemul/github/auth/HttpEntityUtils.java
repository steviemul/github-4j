package io.steviemul.github.auth;

import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpEntityUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static HttpRequest.BodyPublisher jsonBody(Object value) {

    return HttpRequest.BodyPublishers.ofString(
        MAPPER.writeValueAsString(value));
  }

  public static <T> T readBody(HttpResponse<String> response, Class<T> type) {
    return MAPPER.readValue(response.body(), type);
  }
}
