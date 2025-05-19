package org.logbook.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import static org.logbook.controller.UserController.ZONE_ID;

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
                .unit(entry.getUnit())
                .timestamp(entry.getTimestamp_utc().toEpochMilli())
                .localeDate(LocalDate.ofInstant(entry.getTimestamp_utc(), ZONE_ID))
                .build();
    }
}
