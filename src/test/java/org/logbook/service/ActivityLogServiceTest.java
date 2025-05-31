package org.logbook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.model.UserId;
import org.logbook.repository.ActivityLogEntryRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActivityLogServiceTest {

    private ActivityLogEntryRepository repository;
    private ActivityLogService service;

    @BeforeEach
    void setup() {
        repository = mock(ActivityLogEntryRepository.class);
        service = new ActivityLogService(repository);
    }

    @Test
    void testLogActivity_returnsSavedRestActivityLogEntry() {
        UserId userId = new UserId("user-123");
        ActivityType type = ActivityType.PUSHUPS;
        long quantity = 20;
        String unit = "reps";

        ActivityLogEntry mockSavedEntry =
                new ActivityLogEntry(userId, type, quantity, unit);

        when(repository.save(any(ActivityLogEntry.class)))
                .thenReturn(mockSavedEntry);

        RestActivityLogEntry result =
                service.logActivity(userId, type, quantity, unit);

        assertThat(result).isNotNull();
        //assertThat(result.getUserId()).isEqualTo(userId.getUserId());
        assertThat(result.getType()).isEqualTo(type.getValue());
        assertThat(result.getQuantity()).isEqualTo(quantity);
        assertThat(result.getUnit()).isEqualTo(unit);
    }

    @Test
    void testGetActivityLogsCountForType_returnsCorrectSum() {
        UserId userId = new UserId("user-789");
        ActivityType pushupsType = ActivityType.PUSHUPS;
        ActivityType situpsType = ActivityType.SITUPS;
        Instant now = Instant.now();

        List<ActivityLogEntry> mockPushupEntries = List.of(
                new ActivityLogEntry(userId, pushupsType, 10, "reps"),
                new ActivityLogEntry(userId, pushupsType, 15, "reps")
        );

        when(repository.findByActivityTypeAndTimeRange(
                eq(pushupsType), any(Instant.class), any(Instant.class), eq(userId.userId())))
                .thenReturn(mockPushupEntries);

        List<ActivityLogEntry> mockSitupEntries = List.of(
                new ActivityLogEntry(userId, situpsType, 20, "reps"),
                new ActivityLogEntry(userId, situpsType, 25, "reps")
        );

        when(repository.findByActivityTypeAndTimeRange(
                eq(situpsType), any(Instant.class), any(Instant.class), eq(userId.userId())))
                .thenReturn(mockSitupEntries);

        Map<ActivityType, Integer> result = service.getActivityLogsCountForType(userId);

        assertThat(result).containsKey(pushupsType);
        assertThat(result.get(pushupsType)).isEqualTo(25);
        assertThat(result).containsKey(situpsType);
        assertThat(result.get(situpsType)).isEqualTo(45);
    }
}
