package org.logbook.model.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword) {
}
