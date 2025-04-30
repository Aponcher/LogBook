package org.logbook.repository;

import org.logbook.model.ActivityLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogEntryRepository extends JpaRepository<ActivityLogEntry, String> {
}
