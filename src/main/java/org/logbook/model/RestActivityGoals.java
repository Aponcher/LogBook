package org.logbook.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import static org.logbook.controller.UserController.START_DATE;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestActivityGoals {

    public static final Map<ActivityType, Integer> INITIAL_GOALS =
            Map.of(
            ActivityType.PUSHUPS, 25,
            ActivityType.SITUPS, 25,
            ActivityType.SQUATS, 10);

    private Map<ActivityType, Integer> goals;
    private Map<ActivityType, Integer> actual;

    public static RestActivityGoals initialGoals() {
        int multiplier = (int) ChronoUnit.WEEKS.between(START_DATE, LocalDate.now());
        return RestActivityGoals.builder().goals(getCurrentGoals(multiplier)).build();
    }

    public static RestActivityGoals todaySummary(Map<ActivityType, Integer> actual) {
        int multiplier = (int) ChronoUnit.WEEKS.between(START_DATE, LocalDate.now());
        return RestActivityGoals.builder()
                .goals(getCurrentGoals(multiplier))
                .actual(actual)
                .build();
    }

    private static Map<ActivityType, Integer> getCurrentGoals(int weeksSinceStart) {
        return INITIAL_GOALS.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue() * weeksSinceStart));
    }

}
