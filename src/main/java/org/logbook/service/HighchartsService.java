package org.logbook.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logbook.model.ActivityType;
import org.logbook.model.RestActivityLogEntry;
import org.logbook.model.UserId;
import org.logbook.model.highcharts.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * | Endpoint                    | Purpose                               | Chart Type Example   |
 * | --------------------------- | ------------------------------------- | -------------------- |
 * | `/log/{type}/timeseries`     | Line/area/time-series by date         | `line`, `area`       |
 * | `/log/{type}/categoryseries` | Category-based bar/column data        | `bar`, `column`      |
 * | `/log/{type}/rangeseries`    | Ranges with high/low values           | `range`, `arearange` |
 * | `/log/{type}/scatterseries`  | XY data like speed vs heart rate      | `scatter`, `bubble`  |
 * | `/log/{type}/timeline`       | Event timeline, e.g., workouts logged | `timeline`           |
 */
@Slf4j
@Service
@AllArgsConstructor
public class HighchartsService {

    private final ActivityLogService logService;

    /**
     * For an Activity Type provide the Highcharts object needed to display a line chart of the 'ActivityType' over time
     *
     * @param userId       User For filtering
     * @param activityType Type of Activity to filter on
     * @param start        Timestamp for start of query
     * @param end          Timestamp for end of query
     * @return HighchartsOptions UI will use to visualzie data
     */
    public RestChartOptions buildTimeSeriesChart(
            UserId userId,
            ActivityType activityType,
            Instant start,
            Instant end) {
        List<RestActivityLogEntry> activityLogsForType = logService.getActivityLogsForType(
                        activityType,
                        start,
                        end,
                        userId)
                .orElse(List.of());

        if (activityLogsForType.isEmpty()) {
            log.warn("No activity logs found for user {} and type {} in TR {} - {}",
                    userId,
                    activityType,
                    start,
                    end);
            return new RestChartOptions();
        }

        List<XYPoint> values = activityLogsForType.stream()
                .map(activity -> XYPoint.builder()
                        .x(activity.getTimestamp())
                        .y(activity.getQuantity())
                        .build())
                .toList();

        RestChartOptions chart = new RestChartOptions();
        chart.setTitle(String.format("%s over time", activityType));
        chart.setType("line");
        chart.setSeries(List.of(ChartSeries.builder()
                .name(activityType.toString())
                .dataXY(values)
                .build()));
        chart.setChart(ChartChart.builder().type("line").build());
        chart.setXAxis(ChartAxis.builder().type("datetime").build());
        chart.setYAxis(ChartAxis.builder().title("count").build());

        return chart;
    }

}
