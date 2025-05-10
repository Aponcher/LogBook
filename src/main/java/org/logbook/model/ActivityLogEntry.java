package org.logbook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Row in the activity log table.
 * can be a number of different types as defined in the compound key:
 * TYPE with range key
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

    // TODO maybe combine these constructors somehow
    public ActivityLogEntry() {
        Instant ts = Instant.now();
        this.timestamp = ts.toString();
        this.timestamp_utc = ts;
        this.type = ActivityType.HEALTH_EVALUATION;
        this.quantity = 5;
        this.unit = "score";
        this.userId = "test-user";
    }

    public ActivityLogEntry(ActivityType type, long quantity) {
        Instant ts = Instant.now();
        this.timestamp = ts.toString();
        this.timestamp_utc = ts;
        this.type = type;
        this.quantity = quantity;
        this.unit = "reps";
        this.userId = "test-user";
    }

    public ActivityLogEntry(ActivityType type, long quantity, String unit) {
        Instant ts = Instant.now();
        this.timestamp = ts.toString();
        this.timestamp_utc = ts;
        this.type = type;
        this.quantity = quantity;
        this.unit = unit;
        this.userId = "test-user";
    }

    public ActivityLogEntry(UserId userId,
                            ActivityType type,
                            long quantity,
                            String unit) {
        Instant ts = Instant.now();
        this.timestamp = ts.toString();
        this.timestamp_utc = ts;
        this.type = type;
        this.quantity = quantity;
        this.unit = unit;
        this.userId = userId.getUserId();
    }

}
