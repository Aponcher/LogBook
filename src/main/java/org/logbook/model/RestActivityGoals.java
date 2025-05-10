package org.logbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

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
        return RestActivityGoals.builder().goals(INITIAL_GOALS).build();
    }

    public static RestActivityGoals todaySummary(Map<ActivityType, Integer> actual) {

        return RestActivityGoals.builder()
                .goals(INITIAL_GOALS)
                .actual(actual)
                .build();
    }

}
