package org.logbook.controller;

import lombok.AllArgsConstructor;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
@AllArgsConstructor
@CrossOrigin(origins = "*") // loosen later for prod
public class LogController {

    private final ActivityLogService activityLogService;

    // TODO another one where its the body instead of the request params ? or maybe a more general one for that
    @PostMapping("/{type}")
    public ResponseEntity<ActivityLogEntry> logActivity(
            @PathVariable String type,
            @RequestParam(required = false, defaultValue = "0") long quantity,
            @RequestParam(required = false, defaultValue = "reps") String unit) {
        ActivityType activityType = ActivityType.fromValue(type);
        return ResponseEntity.ok(activityLogService.logActivity(activityType, quantity, unit));
    }
}
