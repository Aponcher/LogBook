package org.logbook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.logbook.repository.ActivityLogEntryRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ActivityLogService {

    private final ActivityLogEntryRepository activityLogEntryRepository;

    public ActivityLogEntry logActivity(ActivityType type, long quantity, String unit) {
        log.info("Logging activity: {} {} {}", type, quantity, unit);
        // TODO no need to break up into types but i'd like some business logic to add here maybe validation or some such
        return activityLogEntryRepository.save(new ActivityLogEntry(type, quantity, unit));
    }
}
