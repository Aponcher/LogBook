package org.logbook.model.dto;

import org.logbook.model.UserId;

public record AuthResponse(UserId userId, String token) {
}
