package org.logbook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple UserId wrapper so that easily identifiable as User data rather than any old string
 */
@Data
@Slf4j
@AllArgsConstructor
public class UserId {

    public static String TEST_USER_ID = "test-user";
    public static UserId TEST_USER = UserId.of(TEST_USER_ID);

    private String userId;

    public static UserId of(String userId) {
        if (userId == null) {
            log.warn("Null userId passed to UserId.of using TEST USER");
            return TEST_USER;
        }
        return new UserId(userId);
    }
}
