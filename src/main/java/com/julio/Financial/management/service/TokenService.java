package com.julio.Financial.management.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.julio.Financial.management.domain.user.User;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User user){
        try {
            Algorithm algorithm = getAlgorithm();

            String token = JWT.create()
                    .withIssuer("Financial-management")
                    .withSubject(user.getEmail())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException e){
            throw new RuntimeException("Erro while authenticating");
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = getAlgorithm();

            return
            JWT.require(algorithm)
                    .withIssuer("Financial-management")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return null;
        }
    }
    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }
}
