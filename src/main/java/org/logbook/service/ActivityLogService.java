package org.logbook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.repository.ActivityLogEntryRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ActivityLogService {

    private final ActivityLogEntryRepository activityLogEntryRepository;

    public RestActivityLogEntry logActivity(ActivityType type, long quantity, String unit) {
        log.info("Logging activity: {} {} {}", type, quantity, unit);
        // TODO no need to break up into types but i'd like some business logic to add here maybe validation or some such
        ActivityLogEntry savedEntry = activityLogEntryRepository.save(new ActivityLogEntry(type, quantity, unit));
        log.debug("[AUDIT]: {} {} {}", type, quantity, unit);

        return RestActivityLogEntry.fromActivityLogEntry(savedEntry);
    }

    public Optional<List<RestActivityLogEntry>> getActivityLogsForType(
            String type, Instant start, Instant end) {
        return Optional.of(
                activityLogEntryRepository.findByActivityTypeAndTimeRange(type, start, end)
                .stream()
                .map(RestActivityLogEntry::fromActivityLogEntry)
                .toList());
    }
}
