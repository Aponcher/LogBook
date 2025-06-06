package org.logbook.model.highcharts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartTitle {
    private String text;

    public static ChartTitle of(String text) {
        return ChartTitle.builder().text(text).build();
    }
}
