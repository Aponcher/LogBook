package org.logbook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
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

    private long timestamp;

    private Instant timestamp_utc;

    public ActivityLogEntry() {
        Instant ts = Instant.now();
        this.timestamp = ts.toEpochMilli();
        this.timestamp_utc = ts;
    }

    public ActivityLogEntry(ActivityType type, long quantity) {
        Instant ts = Instant.now();
        this.timestamp = ts.toEpochMilli();
        this.timestamp_utc = ts;
        this.type = type;
        this.quantity = quantity;
        this.unit = "reps";
    }

    public ActivityLogEntry(ActivityType type, long quantity, String unit) {
        Instant ts = Instant.now();
        this.timestamp = ts.toEpochMilli();
        this.timestamp_utc = ts;
        this.type = type;
        this.quantity = quantity;
        this.unit = unit;
    }

}
