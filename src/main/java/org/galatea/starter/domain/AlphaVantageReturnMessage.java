package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;

/**
 * Class representing the structure of data received from Alpha Vantage, contains
 * meta data and a map from dates to stock price data.
 */
@Slf4j
@Data
@Embeddable
public class AlphaVantageReturnMessage {
  @JsonProperty("Meta Data")
  private AlphaVantageMetaData metaData;

  @JsonProperty("Time Series (Daily)")
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
