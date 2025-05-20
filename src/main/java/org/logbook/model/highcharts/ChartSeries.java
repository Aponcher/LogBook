package org.logbook.model.highcharts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Generic ChartSeries wrapper for Highcharts chart,
 * so it can be passed easily to UI.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartSeries {

    private String name;
    // TODO default || MAP || random
    private String color;
    // TODO nullable or just dont include
    // Allows mixed types in multi-series
    private String type;
    // TODO nullable or just dont include
    // e.g., "Dash", "Solid"
    @JsonProperty("dashStyle")
    private String dashStyle;

    // for column, bar, category
    @JsonIgnore
    private List<Number> dataSimple;

    // for scatter, line, range, datetime
    @JsonIgnore
    private List<XYPoint> dataXY;

    /**
     * Unifies the data property into a single list so UI can reference it regardless of type.
     *
     * @return List of data points
     */
    @JsonProperty("data")
    public List<?> getUnifiedData() {
        if (dataSimple != null && !dataSimple.isEmpty()) return dataSimple;
        if (dataXY != null && !dataXY.isEmpty()) return dataXY;
        return Collections.emptyList();
    }
}
