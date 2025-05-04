package org.logbook.repository;

import org.logbook.model.ActivityLogEntry;
import org.logbook.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface ActivityLogEntryRepository extends JpaRepository<ActivityLogEntry, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityLogEntry e WHERE e.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);

    @Query("""
        SELECT l FROM ActivityLogEntry l
        WHERE l.userId = :userId
            AND l.type = :activityType
            AND l.timestamp_utc BETWEEN :start AND :end
        ORDER BY l.timestamp_utc ASC
    """)
    // TODO Pagination ? at what point does it become a concern, write as paginations anyway for scaleability
    List<ActivityLogEntry> findByActivityTypeAndTimeRange(
            @Param("activityType") ActivityType activityType,
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("userId") String userId
    );
}
