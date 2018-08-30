package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import javax.persistence.ElementCollection;

public class StockDataMessage {
  private String symbol;
  @ElementCollection
  private HashMap<String, AlphaVantageStockPriceData> timeSeriesData = new HashMap<>();

  @JsonAnySetter
  public void setTimeSeriesData(final String date, final AlphaVantageStockPriceData priceData) {
    timeSeriesData.put(date, priceData);
  }

  public AlphaVantageStockPriceData getTimeSeriesData(String date) {
    return timeSeriesData.get(date);
  }

}
