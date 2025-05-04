package org.logbook.repository;

import org.logbook.model.ActivityLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ActivityLogEntryRepository extends JpaRepository<ActivityLogEntry, String> {

    @Query("""
        SELECT l FROM ActivityLogEntry l
        WHERE l.type = :activityType
          AND l.timestamp BETWEEN :start AND :end
        ORDER BY l.timestamp ASC
    """)
    // TODO Pagination ? at what point does it become a concern, write as paginations anyway for scaleability
    List<ActivityLogEntry> findByActivityTypeAndTimeRange(
            @Param("activityType") String activityType,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
