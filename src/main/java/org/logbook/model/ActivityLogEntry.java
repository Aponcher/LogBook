package org.logbook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Row in the activity log table.
 * Can represent different types as defined by the ActivityType enum.
 */
@Entity
@Table(name = "activity_log")
@Getter
@Setter
public class ActivityLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    private long quantity;
    private String unit;
    private String timestamp;
    private Instant timestamp_utc;

    @Column(name = "user_id", nullable = false)
    private String userId;

    public ActivityLogEntry() {
        this(UserId.TEST_USER, ActivityType.HEALTH_EVALUATION, 5, "score");
    }

    public ActivityLogEntry(ActivityType type, long quantity) {
        this(UserId.TEST_USER, type, quantity, "reps");
    }

    public ActivityLogEntry(ActivityType type, long quantity, String unit) {
        this(UserId.TEST_USER, type, quantity, unit);
    }

    public ActivityLogEntry(UserId userId,
                            ActivityType type,
                            long quantity,
                            String unit) {
        Instant now = Instant.now();
        this.userId = userId.getUserId();
        this.timestamp = now.toString();
        this.timestamp_utc = now;
        this.type = type;
        this.quantity = quantity;
        this.unit = unit;
    }
}
