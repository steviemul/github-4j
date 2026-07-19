package io.steviemul.github.auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GithubRepoRequest {

  private static final String API_ROOT = "https://api.github.com";
  private static final String GITHUB_REPOS = "repos";
  private static final String DISPATCH_PATH  = "dispatches";
  private static final String CONTENTS_PATH = "contents";

  private static final String GITHUB_JSON_CONTENT_TYPE = "application/vnd.github+json";
  private static final String GITHUB_API_VERSION_HEADER = "X-GitHub-Api-Version";
  private static final String GITHUB_API_VERSION = "2022-11-28";

  private static final String ACCEPT_HEADER = "Accept";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER = "Bearer";

  private final String owner;
  private final String repo;

  public GithubRepoRequest(String owner, String repo) {
    this.owner = owner;
    this.repo = repo;
  }

  public boolean fileExists(String token, String path) {

    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest contentsRequest = createContentsHttpRequest(token, path);

      HttpResponse<String> response = client
          .send(contentsRequest, HttpResponse.BodyHandlers.ofString());

      int statusCode = response.statusCode();

      return (statusCode == 200);
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to check repository contents", e);
    }
  }

  public String sendRepositoryDispatch(String token, String body) {

    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest dispatchRequest = createDispatchesHttpRequest(token, body);

      HttpResponse<String> response = client
          .send(dispatchRequest, HttpResponse.BodyHandlers.ofString());

      int statusCode = response.statusCode();

      if (statusCode != 204) {
        throw new RuntimeException("Error sending repository dispatch, received " + statusCode);
      }

      return response.body();
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to send repository dispatch", e);
    }

  }

  private HttpRequest createContentsHttpRequest(String token, String path) {

    return HttpRequest.newBuilder()
        .uri(URI.create(getRepositoryContentsUrl(path)))
        .header(AUTHORIZATION_HEADER, BEARER + " " + token)
        .header(ACCEPT_HEADER, GITHUB_JSON_CONTENT_TYPE)
        .header(GITHUB_API_VERSION_HEADER, GITHUB_API_VERSION)
        .GET()
        .build();
  }

  private HttpRequest createDispatchesHttpRequest(String token, String body) {

    return HttpRequest.newBuilder()
        .uri(URI.create(getRepositoryDispatchUrl()))
        .header(AUTHORIZATION_HEADER, BEARER + " " + token)
        .header(ACCEPT_HEADER, GITHUB_JSON_CONTENT_TYPE)
        .header(GITHUB_API_VERSION_HEADER, GITHUB_API_VERSION)
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
  }

  private String getRepositoryContentsUrl(String path) {
    return API_ROOT
        + "/" + GITHUB_REPOS
        + "/" + owner
        + "/" + repo
        + "/" + CONTENTS_PATH
        + "/" + path;
  }

  private String getRepositoryDispatchUrl() {
    return API_ROOT
        + "/" + GITHUB_REPOS
        + "/" + owner
        + "/" + repo
        + "/" + DISPATCH_PATH;
  }
}
