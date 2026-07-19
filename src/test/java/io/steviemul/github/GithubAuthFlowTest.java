package io.steviemul.github;

import io.steviemul.github.auth.GithubAppJWTToken;
import io.steviemul.github.auth.GithubAppTokenRequest;
import io.steviemul.github.auth.GithubRepoRequest;
import io.steviemul.github.auth.KeyReader;
import io.steviemul.github.auth.TokenResponse;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;

public class GithubAuthFlowTest {

  @Test
  void test_basic_auth_flow() {

    String keyPath = System.getenv("KEY_PATH");
    String appId = System.getenv("APP_ID");
    String installationId = System.getenv("INSTALLATION_ID");

    RSAPrivateKey privateKey = KeyReader.readPrivateKey(keyPath);

    GithubAppJWTToken githubAppJWTToken = new GithubAppJWTToken(appId, privateKey);

    String jwt = githubAppJWTToken.getSignedJWT();

    String token = getInstallationToken(installationId, jwt);

    boolean exists = checkFileExists(token);

    System.out.println("File exists : " + exists);

    if (exists) {
      String dispatchResponse = requestRepositoryDispatch(token);

      System.out.println(dispatchResponse);
    }
  }

  private String getInstallationToken(String installationId, String jwt) {
    GithubAppTokenRequest tokenRequest = new GithubAppTokenRequest(installationId, jwt);

    TokenResponse tokenResponse = tokenRequest.getToken();

    return tokenResponse.token();
  }

  private boolean checkFileExists(String token) {

    GithubRepoRequest repoRequest = new GithubRepoRequest("steviemul", "github-app-consumer");

    return repoRequest.fileExists(token, ".github/workflows/consume-dispatch.yml?ref=main");
  }

  private String requestRepositoryDispatch(String token) {

    GithubRepoRequest repoRequest = new GithubRepoRequest("steviemul", "github-app-consumer");

    String dispatchJson = """
      {
        "event_type": "app-dispatch",
        "client_payload": {
          "message": "Hello from Java"
        }
      }
    """;

    return repoRequest.sendRepositoryDispatch(
        token,
        dispatchJson);
  }
}
