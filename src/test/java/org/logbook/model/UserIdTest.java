package org.logbook.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.logbook.model.UserId.TEST_USER;
import static org.logbook.model.UserId.TEST_USER_ID;

class UserIdTest {

    @Test
    void testOf() {
        String expectedId = "aponcher";
        UserId userId = UserId.of(expectedId);
        assertEquals(expectedId, userId.getUserId());
    }

    @Test
    void testOf_nullFallback() {
        UserId userId = UserId.of(null);
        assertEquals(TEST_USER, userId);
        assertEquals(TEST_USER_ID, userId.getUserId());
    }

}