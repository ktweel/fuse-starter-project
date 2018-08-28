package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlphaVantageReturnMessage {
  @JsonProperty("Meta Data")
  private AlphaVantageMetaData metaData;
//    @JsonProperty("Time Series (Daily)")
//    private AlphaVantageTimeSeriesDaily priceData;

  @JsonProperty("Time Series (Daily)")
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

  public AlphaVantageMetaData getMetaData() {
    return metaData;
  }
  public void setMetaData(AlphaVantageMetaData newMetaData) {
    metaData = newMetaData;
  }

//  public AlphaVantageTimeSeriesDaily getPriceData() {
//    return priceData;
//  }
//
//  public void setPriceData(AlphaVantageTimeSeriesDaily priceData) {
//    this.priceData = priceData;
//  }
}
