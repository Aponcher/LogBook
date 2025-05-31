package org.logbook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.model.UserId;
import org.logbook.model.highcharts.RestChartOptions;
import org.logbook.service.ActivityLogService;
import org.logbook.service.HighchartsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/log")
@AllArgsConstructor
public class LogController {

    private final ActivityLogService activityLogService;
    private final HighchartsService highchartsService;

    // TODO another one where its the body instead of the request params ? or maybe a more general one for that
    // TODO add User def so we can have username lookup and such
    @PostMapping("/{type}")
    public ResponseEntity<RestActivityLogEntry> logActivity(
            @PathVariable String type,
            Principal principal,
            @RequestParam(required = false, defaultValue = "0") long quantity,
            @RequestParam(required = false, defaultValue = "reps") String unit) {
        String username = principal.getName();
        ActivityType activityType = ActivityType.fromValue(type);
        return ResponseEntity.ok(
                activityLogService.logActivity(
                        UserId.of(username),
                        activityType,
                        quantity,
                        unit));
    }

    @GetMapping("list/{type}")
    public ResponseEntity<List<RestActivityLogEntry>> getActivityLogsForType(
            @PathVariable String type,
            Principal principal,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end
    ) {
        if (start == null || end == null) {
            Instant ts = Instant.now();
            if (start == null) {
                start = ts.minus(Duration.ofDays(7));
            }
            if (end == null) {
                end = ts;
            }
        }
        String username = principal.getName();
        return ResponseEntity.of(
                activityLogService.getActivityLogsForType(
                        ActivityType.fromValue(type),
                        start,
                        end,
                        UserId.of(username)));
    }

    /**
     * Highcharts timeseries data for a given type of activity log and time range
     *
     * @param type  Type of activity log to retrieve
     * @param start Timestamp of the start of the time range
     * @param end   Timestamp of the end of the time range
     * @return timeseries data for rendering in Highcharts
     */
    @GetMapping("/{type}/timeSeriesData")
    public ResponseEntity<RestChartOptions> getTimeSeriesActivityLogsForType(
            @PathVariable String type,
            Principal principal,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end,
            @RequestParam(required = false) Integer interval,
            @RequestParam(required = false, defaultValue = "line") String chartType
    ) {
        if (start == null || end == null) {
            Instant ts = Instant.now();
            if (start == null) {
                start = ts.minus(Duration.ofDays(7));
            }
            if (end == null) {
                end = ts;
            }
        }
        String username = principal.getName();
        RestChartOptions restChartOptions = highchartsService.buildTimeSeriesChart(
                UserId.of(username),
                ActivityType.fromValue(type),
                start,
                end,
                chartType);
        return ResponseEntity.ok(restChartOptions);
    }

    // TODO log free form log (OpenSearch so we can fuzy search and such)

    // TODO Angular Gauges (speedometer) pushups/situps daily goal ?
}
