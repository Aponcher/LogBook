package org.logbook.model.dto;

public record UserRegistrationRequest(
        String username,
        String email,
        String password) {
}
