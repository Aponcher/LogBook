package org.logbook.model.dto;

public record UserAuthRequest(
        String usernameOrEmail,
        String password) {
}
