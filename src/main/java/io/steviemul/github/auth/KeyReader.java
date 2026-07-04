package io.steviemul.github.auth;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileReader;
import java.security.interfaces.RSAPrivateKey;

public class KeyReader {

  public static RSAPrivateKey readPrivateKey(String file) {

    try (PEMParser parser = new PEMParser(new FileReader(file))) {

      Object obj = parser.readObject();

      JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

      if (obj instanceof PEMKeyPair pair) {
        return (RSAPrivateKey) converter.getKeyPair(pair).getPrivate();
      }

      if (obj instanceof PrivateKeyInfo info) {
        return (RSAPrivateKey) converter.getPrivateKey(info);
      }

      throw new IllegalArgumentException("Unsupported PEM type: " + obj.getClass());
    }
    catch (Exception e) {
      throw new RuntimeException("Unable to load private key", e);
    }
  }
}
