package com.cursochat.cursochat.providers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonWebTokenProvider implements TokenProvider {
    private final KeyProvider provider;

    @Override
    public Map<String, String> decode(String token) {
        DecodedJWT jwt = JWT.decode(token);
        PublicKey publicKey = provider.getPublicKey(jwt.getKeyId());
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
        algorithm.verify(jwt);
        var expired = jwt

                .getExpiresAtAsInstant()
                .atZone(ZoneId.systemDefault())
                .isBefore(ZonedDateTime.now());
        if (expired) {
            throw new RuntimeException("token is expired");
        }
        return Map.of("id", jwt.getSubject(),
                "name", jwt.getClaim("name").asString(),
                "picture", jwt.getClaim("picture").asString()
        );
    }
}
