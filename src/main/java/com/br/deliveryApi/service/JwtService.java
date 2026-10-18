package com.br.deliveryApi.service;

import com.br.deliveryApi.dto.TokenResponse;
import com.br.deliveryApi.model.Cliente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expirationMinutes;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.encoder = encoder;
        this.expirationMinutes = expirationMinutes;
    }

    public TokenResponse gerarToken(Cliente cliente) {
        Instant agora = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant expiraEm = agora.plus(expirationMinutes, ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("deliveryApi")
                .subject(String.valueOf(cliente.getId()))
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .claim("email", cliente.getEmail())
                .claim("roles", List.of(cliente.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", expiraEm);
    }
}
