package org.logbook.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RestActivityLogEntry {

    private String type;
    private long quantity;
    private String unit;
    private long timestamp;
    private LocalDate localeDate;

    public static RestActivityLogEntry fromActivityLogEntry(ActivityLogEntry entry) {
        return RestActivityLogEntry.builder()
                .type(entry.getType().getValue())
                .quantity(entry.getQuantity())
                .timestamp(entry.getTimestamp())
                // TODO Date or Time so that we can do by hour or week or something
                .localeDate(LocalDate.ofEpochDay(entry.getTimestamp()))
                .build();
    }
}
