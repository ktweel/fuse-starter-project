package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class AlphaVantageTimeSeriesDaily {
    @JsonProperty
    private Map<String, AlphaVantageStockPriceData> timeSeriesData = new HashMap<>();

    @JsonAnySetter
    public void setTimeSeriesData(final String date, final AlphaVantageStockPriceData priceData) {
      timeSeriesData.put(date, priceData);
    }
}
