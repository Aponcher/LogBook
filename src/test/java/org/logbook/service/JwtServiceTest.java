package org.logbook.service;

import org.junit.jupiter.api.Test;
import org.logbook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.logbook.model.UserId.TEST_USER_ID;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=GCJtVWnaFl0mqsjzr0ch7PrImIi9MLD304ryGqO3zEY=",
        "jwt.expiration=3600000"
})
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateAndValidateToken_success() {
        String expectedEmail = String.format("%s@gmail.com", TEST_USER_ID);
        User userDetails = User.builder()
                .username(TEST_USER_ID)
                .email(expectedEmail)
                .password("password")
                .role("USER")
                .build();

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        String username = jwtService.extractUserId(token);
        assertEquals(TEST_USER_ID, username);
        assertEquals(expectedEmail, jwtService.extractEmail(token));

        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void extractUsername_invalidToken_throwsException() {
        String invalidToken = "invalid.token.value";
        assertThrows(Exception.class, () -> jwtService.extractUserId(invalidToken));
    }

    @Test
    void isTokenValid_wrongUser_returnsFalse() {
        User user1 = User.builder().username("alice").password("x").role("USER").build();
        User user2 = User.builder().username("bob").password("x").role("USER").build();

        String token = jwtService.generateToken(user1);
        assertFalse(jwtService.isTokenValid(token, user2));
    }
}
