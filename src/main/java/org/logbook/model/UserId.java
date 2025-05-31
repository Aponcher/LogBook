package org.logbook.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple UserId wrapper so that easily identifiable as User data rather than any old string
 */
@Slf4j
public record UserId(@JsonValue String userId) {

    public static String TEST_USER_ID = "test-user";
    public static UserId TEST_USER = UserId.of(TEST_USER_ID);

    public static UserId of(String userId) {
        if (userId == null) {
            log.warn("Null userId passed to UserId.of using TEST USER");
            return TEST_USER;
        }
        return new UserId(userId);
    }

    @Override
    public String toString() {
        return userId;
    }
}
