package org.logbook.repository;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
@Tag("integration")
@Rollback
class ActivityLogEntryRepositoryIntegrationTest {

    @Autowired
    private ActivityLogEntryRepository logbookRepository;

    @Test
    public void testInsertAndFetch() {

        logbookRepository.saveAll(
                Stream.of(10, 10, 15)
                .map(quant -> new ActivityLogEntry(ActivityType.PUSHUPS, 20))
                .toList());
        logbookRepository.saveAll(
                Stream.of(10, 5)
                .map(quant -> new ActivityLogEntry(ActivityType.SITUPS, 20))
                .toList());
        Instant end = Instant.now();
        Instant start = end.minus(Duration.ofHours(2));
        List<ActivityLogEntry> savedPushups =
                logbookRepository.findByActivityTypeAndTimeRange(ActivityType.PUSHUPS, start, end, "test-user");

        List<ActivityLogEntry> savedSitups =
                logbookRepository.findByActivityTypeAndTimeRange(ActivityType.SITUPS, start, end, "test-user");
        assertEquals(3, savedPushups.size());
        assertEquals(2, savedSitups.size());
    }

}