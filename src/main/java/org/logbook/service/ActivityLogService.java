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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.logbook.controller.LogController.ZONE_ID;

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
        log.info("Logging activity: {} {} {} {}", userId, type, quantity, unit);
        // TODO no need to break up into types but i'd like some business logic to add here maybe validation or some such
        ActivityLogEntry savedEntry =
                activityLogEntryRepository.save(
                        new ActivityLogEntry(
                                userId,
                                type,
                                quantity,
                                unit));
        log.debug("[AUDIT]: {} {} {} {}", userId, type, quantity, unit);

        return RestActivityLogEntry.fromActivityLogEntry(savedEntry);
    }

    public Optional<List<RestActivityLogEntry>> getActivityLogsForType(
            ActivityType type, Instant start, Instant end, UserId userId) {
        return Optional.of(
                activityLogEntryRepository.findByActivityTypeAndTimeRange(type, start, end, userId.getUserId())
                .stream()
                .map(RestActivityLogEntry::fromActivityLogEntry)
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


//public RestChartOptions buildTimeSeriesChart(String activityType, Instant start, Instant end) {
//        List<LogEntry> logs = logEntryRepository.findByActivityTypeAndTimeRange(activityType, start, end);
//
//        List<String> categories = logs.stream()
//            .map(entry -> entry.getTimestamp().toString()) // or format as "MM-dd"
//            .toList();
//
//        List<Integer> values = logs.stream()
//            .map(LogEntry::getValue)
//            .toList();
//
//        RestChartOptions chart = new RestChartOptions();
//        chart.setTitle("Activity Over Time");
//        chart.setType("line");
//        chart.setCategories(categories);
//        chart.setSeries(List.of(Map.of(
//            "name", activityType,
//            "data", values
//        )));
//        chart.setChart(Map.of("type", "line"));
//        chart.setXAxis(Map.of("categories", categories));
//        chart.setYAxis(Map.of("title", Map.of("text", "Count")));
//
//        return chart;
//    }