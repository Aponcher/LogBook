package org.logbook.model.dto;

public record UserAuthRequest(
        String id,
        String password) {
}
