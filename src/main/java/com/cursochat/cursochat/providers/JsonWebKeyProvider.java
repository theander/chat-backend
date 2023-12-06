package com.cursochat.cursochat.providers;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
@Component

public class JsonWebKeyProvider implements KeyProvider{
    private final UrlJwkProvider provider;

    public JsonWebKeyProvider(@Value("${app.auth.jwks-url}") final String jwksUrls) throws MalformedURLException {
        try {
            this.provider = new UrlJwkProvider(new URL(jwksUrls));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
@Cacheable("public-key")
    @Override
    public PublicKey getPublicKey(String keyId) {
        Jwk jwk = null;
        try {
            jwk = provider.get(keyId);
            return jwk.getPublicKey();
        } catch (InvalidPublicKeyException e) {
            throw new RuntimeException(e);
        } catch (JwkException e) {
            throw new RuntimeException(e);
        }
    }
}
