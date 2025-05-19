package org.logbook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.model.UserId;
import org.logbook.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/log")
@AllArgsConstructor
@CrossOrigin(origins = "*") // loosen later for prod
public class LogController {

    private final ActivityLogService activityLogService;

    // TODO another one where its the body instead of the request params ? or maybe a more general one for that
    // TODO add User def so we can have username lookup and such
    @PostMapping("/{type}")
    public ResponseEntity<RestActivityLogEntry> logActivity(
            @PathVariable String type,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "0") long quantity,
            @RequestParam(required = false, defaultValue = "reps") String unit) {
        ActivityType activityType = ActivityType.fromValue(type);
        return ResponseEntity.ok(
                activityLogService.logActivity(
                        UserId.of(userId),
                        activityType,
                        quantity,
                        unit));
    }

    @GetMapping("list/{type}")
    public ResponseEntity<List<RestActivityLogEntry>> getActivityLogsForType(
            @PathVariable String type,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end
    ) {
        // TODO if start/end is null then use some default values like last week
        if (start == null || end == null) {
            Instant ts = Instant.now();
            if (start == null) {
                start = ts.minus(Duration.ofDays(7));
            }
            if (end == null) {
                end = ts;
            }
        }
        return ResponseEntity.of(
                activityLogService.getActivityLogsForType(
                        ActivityType.fromValue(type),
                        start,
                        end,
                        UserId.of(userId)));
    }

    /**
     * | Endpoint                    | Purpose                               | Chart Type Example   |
     * | --------------------------- | ------------------------------------- | -------------------- |
     * | `/api/chart/timeseries`     | Line/area/time-series by date         | `line`, `area`       |
     * | `/api/chart/categoryseries` | Category-based bar/column data        | `bar`, `column`      |
     * | `/api/chart/rangeseries`    | Ranges with high/low values           | `range`, `arearange` |
     * | `/api/chart/scatterseries`  | XY data like speed vs heart rate      | `scatter`, `bubble`  |
     * | `/api/chart/timeline`       | Event timeline, e.g., workouts logged | `timeline`           |
     *
     * @param type  Type of activity log to retrieve
     * @param start Timestamp of the start of the time range
     * @param end   Timestamp of the end of the time range
     * @return timeseries data for rendering in Highcharts
     */
    @GetMapping("/{type}/timeSeriesData")
    public ResponseEntity<List<RestActivityLogEntry>> getTimeSeriesActivityLogsForType(
            @PathVariable String type,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end,
            @RequestParam(required = false) int interval
    ) {
        // TODO if start/end is null then use some default values like last week
        if (start == null || end == null) {
            Instant ts = Instant.now();
            if (start == null) {
                start = ts.minus(Duration.ofDays(7));
            }
            if (end == null) {
                end = ts;
            }
        }
        // TODO change this to HighchartsTimeSeriesDataResponse

        return ResponseEntity.of(
                activityLogService.getActivityLogsForType(
                        ActivityType.fromValue(type),
                        start,
                        end,
                        UserId.of(userId)));
    }

    // TODO highchartsTimeSeriesData('type', 'start', 'end', 'interval')

    // TODO log free form log (OpenSearch so we can fuzy search and such)

    // TODO Angular Gauges (speedometer) pushups/situps daily goal ?
}
