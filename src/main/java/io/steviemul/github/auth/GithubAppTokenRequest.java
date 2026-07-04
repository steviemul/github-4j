package io.steviemul.github.auth;

import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GithubAppTokenRequest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String GITHUB_INSTALLATIONS_URL = "https://api.github.com/app/installations/";
  private static final String ACCESS_TOKENS_PATH = "/access_tokens";
  private static final String GITHUB_JSON_CONTENT_TYPE = "application/vnd.github+json";
  private static final String GITHUB_API_VERSION_HEADER = "X-GitHub-Api-Version";
  private static final String GITHUB_API_VERSION = "2022-11-28";

  private static final String ACCEPT_HEADER = "Accept";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER = "Bearer";

  private final String installationId;
  private final String jwt;

  public GithubAppTokenRequest(String installationId, String jwt) {
    this.installationId = installationId;
    this.jwt = jwt;
  }

  public TokenResponse getToken() {

    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest request = createHttpRequest();

      HttpResponse<String> response = client
          .send(request, HttpResponse.BodyHandlers.ofString());

      int statusCode = response.statusCode();

      if (statusCode != 201) {
        throw new RuntimeException("Error getting token, received " + statusCode);
      }

      String responseBody = response.body();

      return objectMapper.readValue(responseBody, TokenResponse.class);
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to retrieve token", e);
    }
  }

  private HttpRequest createHttpRequest() {
    return  HttpRequest.newBuilder()
        .uri(URI.create(getInstallationUrl()))
        .header(AUTHORIZATION_HEADER, BEARER + " " + jwt)
        .header(ACCEPT_HEADER, GITHUB_JSON_CONTENT_TYPE)
        .header(GITHUB_API_VERSION_HEADER, GITHUB_API_VERSION)
        .POST(HttpRequest.BodyPublishers.noBody())
        .build();
  }
  private String getInstallationUrl() {
    return GITHUB_INSTALLATIONS_URL + installationId + ACCESS_TOKENS_PATH;
  }
}
