package org.logbook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityGoals;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.model.UserId;
import org.logbook.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin(origins = "*") // loosen later for prod
public class UserController {
    public static ZoneId ZONE_ID = ZoneId.of("America/Chicago");

    private final ActivityLogService activityLogService;

    @GetMapping("/goals")
    public ResponseEntity<RestActivityGoals> getGoals() {
        // TODO add 'Growth' rate
        return ResponseEntity.ok(
                RestActivityGoals.initialGoals());
    }

    @GetMapping("/today/summary")
    public ResponseEntity<RestActivityGoals> getTodaySummary(
            @RequestParam(required = false) String userId) {
        Map<ActivityType, Integer> actual =
                activityLogService.getActivityLogsCountForType(UserId.of(userId));
        return ResponseEntity.ok(RestActivityGoals.todaySummary(actual));
    }
}
