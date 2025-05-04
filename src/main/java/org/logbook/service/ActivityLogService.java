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
            ActivityType type, Instant start, Instant end, String userId) {
        return Optional.of(
                activityLogEntryRepository.findByActivityTypeAndTimeRange(type, start, end, userId)
                .stream()
                .map(RestActivityLogEntry::fromActivityLogEntry)
                .toList());
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