package org.logbook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.User;
import org.logbook.model.UserId;
import org.logbook.model.dto.AuthResponse;
import org.logbook.model.dto.ChangePasswordRequest;
import org.logbook.model.dto.UserAuthRequest;
import org.logbook.model.dto.UserRegistrationRequest;
import org.logbook.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User Service for registering new users, updating and validating existing users.
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Register a new user if they don't already exist.
     *
     * @param request UserRegistrationRequest with username, email and password.
     * @return User registered in DB.
     */
    public User register(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        user.setEnabled(true);

        log.info("Registering user {} with email {}", request.username(), request.email());

        return userRepository.save(user);
    }

    /**
     * Login a user, if they exist and credentials match, and return a JWT token
     *
     * @param request AuthRequest with username or email and password.
     * @return AuthResponse with JWT token.
     */
    public AuthResponse login(UserAuthRequest request) {
        log.info("login request for '{}'", request.usernameOrEmail());
        // TODO i don't really need to wrap it in UserId but i don't like passing around a bunch of strings i like 'typed' strings
        User userByUsernameOrEmail = findUserByUsernameOrEmail(UserId.of(request.usernameOrEmail()));

        if (!passwordEncoder.matches(request.password(), userByUsernameOrEmail.getPassword())) {
            log.warn("Password did not match for user {}", request.usernameOrEmail());
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(userByUsernameOrEmail);
        return new AuthResponse(token);
    }

    /**
     * Check if User exists and that your credentials are correct,
     * and if so, update their password.
     *
     * @param request ChangePasswordRequest with the current and new password.
     * @param userId  'ID' for User, either username or email.
     */
    public void changePassword(ChangePasswordRequest request, UserId userId) {
        // check current password, update hash
        User user = findUserByUsernameOrEmail(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password changed for user {}", userId);
    }

    /**
     * UserId can be either username or email;
     * find or throw exception if not found.
     *
     * @param usernameOrEmail UserId wrapper for either username or email to lookup in DB.
     * @return User found in DB.
     */
    private User findUserByUsernameOrEmail(UserId usernameOrEmail) {
        String usernameOrEmailStr = usernameOrEmail.getUserId();
        if (usernameOrEmailStr.contains("@")) {
            return userRepository.findByEmail(usernameOrEmailStr)
                    .orElseThrow(() -> new IllegalArgumentException("No User with Email in use"));
        } else {
            return userRepository.findByUsername(usernameOrEmailStr)
                    .orElseThrow(() -> new IllegalArgumentException("No User with Username in use"));
        }
    }
}
