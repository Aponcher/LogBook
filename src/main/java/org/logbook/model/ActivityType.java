package org.logbook.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum ActivityType {
    PUSHUPS("pushups"),
    SITUPS("situps"),
    SQUATS("squats"),
    SLEEP("sleep"),
    SICK("sick"),
    SMOKE("smoke"),
    ATE("ate"),
    HOCKEY("hockey"),
    HEALTH_EVALUATION("health"),
    LOG_ENTRY("log");

    @Getter
    @JsonValue
    final String value;

    ActivityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActivityType fromValue(String value) {
        for (ActivityType type : ActivityType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown activity type: " + value);
    }
}
