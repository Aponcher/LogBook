package org.logbook.model.highcharts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestChartOptions {

    private String title;

    private String type;

    private List<String> categories;

    private List<ChartSeries> series;

    @JsonProperty("xAxis")
    private ChartAxis xAxis;

    @JsonProperty("yAxis")
    private ChartAxis yAxis;

    private ChartChart chart;
}
