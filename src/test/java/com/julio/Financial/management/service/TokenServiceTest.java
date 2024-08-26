package com.julio.Financial.management.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.julio.Financial.management.domain.user.User;
import com.julio.Financial.management.exceptions.TokenCreation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService.setSecret("your_secret_key_here"); // Configura o segredo diretamente no TokenService

        user = new User();
        user.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Generate Token Success")
    void generateTokenSuccess() {
        // Simule o comportamento do JWT
        Algorithm algorithm = Algorithm.HMAC256("your_secret_key_here"); // Use o mesmo segredo configurado
        String token = tokenService.generateToken(user);

        // Verifique se o token foi gerado
        assertNotNull(token);

        // Verifique se o token contém os detalhes esperados
        try {
            JWT.require(algorithm)
                    .withIssuer("Financial-management")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            fail("Token validation failed");
        }
    }

    @Test
    @DisplayName("Generate Token Failure")
    void generateTokenFailure() {
        TokenService spyTokenService = spy(tokenService);
        doThrow(new JWTCreationException("Error creating token", new Exception())).when(spyTokenService).getAlgorithm();

        // Verifique se a exceção TokenCreation é lançada
        assertThrows(TokenCreation.class, () -> {
            spyTokenService.generateToken(user);
        });
    }


    @Test
    @DisplayName("Validate Token Success")
    void validateTokenSuccess() {
        String token = tokenService.generateToken(user);

        // Verifique a validação do token
        String subject = tokenService.validateToken(token);

        assertNotNull(subject);
        assertEquals(user.getEmail(), subject);
    }

    @Test
    @DisplayName("Validate Token Failure")
    void validateTokenFailure() {
        // Teste com um token inválido
        String invalidToken = "invalidToken";

        // Verifique a validação do token
        String subject = tokenService.validateToken(invalidToken);

        assertNull(subject);
    }
}
