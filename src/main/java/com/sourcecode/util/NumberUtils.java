package com.sourcecode.util;

import java.math.BigDecimal;

public class NumberUtils {
    public Double setDoubleScale(Double d, int scale) {
        BigDecimal decimal = new BigDecimal(d);
        return (decimal.setScale(scale, BigDecimal.ROUND_HALF_DOWN).doubleValue());
    }
}
