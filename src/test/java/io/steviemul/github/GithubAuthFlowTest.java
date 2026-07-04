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
    String installation = System.getenv("INSTALLATION_ID");

    RSAPrivateKey privateKey = KeyReader.readPrivateKey(keyPath);

    GithubAppJWTToken githubAppJWTToken = new GithubAppJWTToken(appId, privateKey);

    String jwt = githubAppJWTToken.getSignedJWT();

    GithubAppTokenRequest tokenRequest = new GithubAppTokenRequest(installation, jwt);

    TokenResponse tokenResponse = tokenRequest.getToken();

    GithubRepoRequest repoRequest = new GithubRepoRequest("steviemul", "github-app-consumer");

    String dispatchJson = """
      {
        "event_type": "app-dispatch",
        "client_payload": {
          "message": "Hello from Java"
        }
      }
    """;

    String response = repoRequest.sendRepositoryDispatch(
        tokenResponse.token(),
        dispatchJson);

    System.out.println(response);
  }
}
