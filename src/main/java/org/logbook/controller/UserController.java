package org.logbook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityGoals;
import org.logbook.model.UserId;
import org.logbook.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin(origins = "*") // loosen later for prod
public class UserController {
    public static ZoneId ZONE_ID = ZoneId.of("America/Chicago");
    public static LocalDate START_DATE = LocalDate.of(2025, 5, 19);

    private final ActivityLogService activityLogService;

    @GetMapping("/goals")
    public ResponseEntity<RestActivityGoals> getGoals(@RequestParam(required = false) String userId) {
        log.info("Getting goals for user {}", userId);
        return ResponseEntity.ok(RestActivityGoals.initialGoals());
    }

    @GetMapping("/today/summary")
    public ResponseEntity<RestActivityGoals> getTodaySummary(
            @RequestParam(required = false) String userId) {
        log.info("Getting today summary for user {}", userId);
        Map<ActivityType, Integer> actual =
                activityLogService.getActivityLogsCountForType(UserId.of(userId));
        return ResponseEntity.ok(RestActivityGoals.todaySummary(actual));
    }
}
