package org.logbook.model.highcharts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Some chart types require a point with x and y values,
 * rather than x values mapped to a category list.
 */
@Data
@Builder
@AllArgsConstructor
public class XYPoint {
    /**
     * Timestamp in ms
     */
    private long x;

    /**
     * Y value in 'unit'
     */
    private Number y;
}
