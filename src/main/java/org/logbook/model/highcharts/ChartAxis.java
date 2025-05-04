package org.logbook.model.highcharts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * ChartAxis is a wrapper for the axis properties of a Highcharts chart,
 * so it can be passed easily to UI.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartAxis {
    private String title;
    private List<String> categories;
    // "datetime", "category"
    private String type;
    private String tickInterval;
    private String format;
}
