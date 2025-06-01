package org.logbook.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityGoals;
import org.logbook.model.UserId;
import org.logbook.model.dto.AuthResponse;
import org.logbook.model.dto.UserAuthRequest;
import org.logbook.model.dto.UserRegistrationRequest;
import org.logbook.service.ActivityLogService;
import org.logbook.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    public static ZoneId ZONE_ID = ZoneId.of("America/Chicago");
    public static LocalDate START_DATE = LocalDate.of(2025, 5, 19);

    private final ActivityLogService activityLogService;
    private final UserService userService;

    @GetMapping("/whoAmI")
    public ResponseEntity<String> whoAmI(Principal principal) {
        log.info("Received WhoAmI request for user {}", principal.getName());
        log.info("User: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(principal.getName());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        log.info("Received Register request for user {}", registrationRequest.username());
        return ResponseEntity.ok(userService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserAuthRequest authRequest) {
        log.info("Received Auth request for user {}", authRequest.id());
        return ResponseEntity.ok(userService.login(authRequest));
    }

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
