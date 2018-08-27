package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlphaVantageTimeSeriesDaily {
  @JsonProperty
  private Map<String, AlphaVantageStockPriceData> timeSeriesData = new HashMap<>();

  @JsonAnySetter
  public void setTimeSeriesData(final String date, final AlphaVantageStockPriceData priceData) {
    timeSeriesData.put(date, priceData);
    log.info("put data" + date);
  }

  public AlphaVantageStockPriceData getTimeSeriesData(String date) {
    log.info("get data" + date);
    return timeSeriesData.get(date);
  }
}
