package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlphaVantageReturnMessage {
    @JsonProperty("Meta Data")
    private AlphaVantageMetaData metaData;
    @JsonProperty("Time Series (Daily)")
    private AlphaVantageTimeSeriesDaily priceData;


    public AlphaVantageMetaData getMetaData() {
      return metaData;
    }
    public void setMetaData(AlphaVantageMetaData newMetaData) {
      metaData = newMetaData;
    }

  public AlphaVantageTimeSeriesDaily getPriceData() {
    return priceData;
  }

  public void setPriceData(AlphaVantageTimeSeriesDaily priceData) {
    this.priceData = priceData;
  }
}
