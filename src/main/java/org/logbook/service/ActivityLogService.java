package org.logbook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.model.UserId;
import org.logbook.repository.ActivityLogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.logbook.controller.UserController.ZONE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ActivityLogService {

    private final ActivityLogEntryRepository activityLogEntryRepository;

    public RestActivityLogEntry logActivity(
            UserId userId,
            ActivityType type,
            long quantity,
            String unit) {
        log.info("{}: Logging activity: {} {} {}", userId, type, quantity, unit);
        // TODO no need to break up into types but i'd like some business logic to add here maybe validation or some such
        ActivityLogEntry savedEntry =
                activityLogEntryRepository.save(
                        new ActivityLogEntry(
                                userId,
                                type,
                                quantity,
                                unit));
        log.debug("[AUDIT]: {} {} {} {}",
                savedEntry.getId(), savedEntry.getType(), savedEntry.getQuantity(), savedEntry.getUnit());

        return RestActivityLogEntry.fromActivityLogEntry(savedEntry);
    }

    public Optional<List<RestActivityLogEntry>> getActivityLogsForType(
            ActivityType type, Instant start, Instant end, UserId userId) {
        return Optional.of(
                activityLogEntryRepository.findByActivityTypeAndTimeRange(type, start, end, userId.userId())
                        .stream()
                        .map(RestActivityLogEntry::fromActivityLogEntry)
                        .sorted(Comparator.comparingLong(RestActivityLogEntry::getTimestamp).reversed())
                        .toList());
    }

    public Map<ActivityType, Integer> getActivityLogsCountForType(UserId userId) {
        Instant endTS = Instant.now();
        Instant startTS = LocalDate.ofInstant(endTS, ZONE_ID)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
        Map<ActivityType, Integer> actual = new HashMap<>();
        List.of(ActivityType.PUSHUPS, ActivityType.SITUPS, ActivityType.SQUATS)
                .forEach(type ->
                        getActivityLogsForType(type, startTS, endTS, userId)
                                .ifPresent(logsOfType -> {
                                    int activityOfType = logsOfType.stream()
                                            .mapToInt(entry -> (int) entry.getQuantity())
                                            .sum();
                                    log.info("User {} did {} sets of type {} for total {}",
                                            userId,
                                            logsOfType.size(),
                                            type,
                                            activityOfType);
                                    actual.put(type, activityOfType);
                                }));
        return actual;
    }
}
