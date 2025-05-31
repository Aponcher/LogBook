package org.logbook.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.logbook.model.User;
import org.logbook.model.UserId;
import org.logbook.model.dto.AuthResponse;
import org.logbook.model.dto.ChangePasswordRequest;
import org.logbook.model.dto.UserAuthRequest;
import org.logbook.model.dto.UserRegistrationRequest;
import org.logbook.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.logbook.model.UserId.TEST_USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegister_success() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                TEST_USER_ID, "test@example.com", "password123");

        when(userRepository.save(any())).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(UUID.randomUUID()); // simulate persistence
            return user;
        });

        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = userService.register(request);

        assertNotNull(response.token());
        assertNotNull(response.userId());
    }

    @Test
    void testLogin_success() {
        String email = String.format("%s@gmail.com", TEST_USER_ID);
        User user = User.builder()
                .username(TEST_USER_ID)
                .email(email)
                .password("hashed")
                .role("USER")
                .build();

        when(userRepository.findByUsername(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        UserAuthRequest request = new UserAuthRequest(TEST_USER_ID, "password123");
        AuthResponse response = userService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals(TEST_USER_ID, response.userId().userId());
    }

    @Test
    void testChangePassword_success() {
        String email = String.format("%s@gmail.com", TEST_USER_ID);
        User user = User.builder()
                .username(TEST_USER_ID)
                .email(email)
                .password("oldHashed")
                .role("USER")
                .build();

        when(userRepository.findByUsername(TEST_USER_ID)).thenReturn(Optional.of(user));
        
        // oldHashed would be decoded to something like oldPassword and compared to 'oldPassword'
        when(passwordEncoder.matches("oldPassword", "oldHashed"))
                .thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashed");

        userService.changePassword(
                new ChangePasswordRequest("oldPassword", "newPassword"),
                UserId.of(TEST_USER_ID));

        verify(userRepository).save(user);
        assertEquals("newHashed", user.getPassword());
    }
}