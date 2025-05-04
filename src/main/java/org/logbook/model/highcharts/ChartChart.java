package org.logbook.model.highcharts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartChart {
    private String type;
    private String backgroundColor;
}
