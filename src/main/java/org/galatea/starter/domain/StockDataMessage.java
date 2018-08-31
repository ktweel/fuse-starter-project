package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import javax.persistence.ElementCollection;
import lombok.Data;

@Data
public class StockDataMessage {
  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("timeSeriesData")
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
