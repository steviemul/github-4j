package io.steviemul.github.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

public class GithubAppJWTToken {

  public static final int ISSUED_AT_SECONDS = 60;
  public static final int EXPIRY_SECONDS = 540;

  private final String appId;
  private final RSAPrivateKey privateKey;

  public GithubAppJWTToken(String appId, RSAPrivateKey privateKey) {
    this.appId = appId;
    this.privateKey = privateKey;
  }

  public String getSignedJWT() {

    Algorithm algorithm = Algorithm.RSA256(privateKey);

    return JWT.create()
        .withIssuedAt(Date.from(Instant.now().minusSeconds(ISSUED_AT_SECONDS)))
        .withExpiresAt(Date.from(Instant.now().plusSeconds(EXPIRY_SECONDS)))
        .withIssuer(appId)
        .sign(algorithm);
  }
 }
