package org.logbook.model.highcharts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * ChartAxis is a wrapper for the axis properties of a Highcharts chart,
 * so it can be passed easily to UI.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartAxis {
    private ChartTitle title;
    private List<String> categories;
    // "datetime", "category"
    private String type;
    @JsonProperty("tickInterval")
    private String tickInterval;
    private String format;
}
