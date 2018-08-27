package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlphaVantageReturnMessage {
    @JsonProperty("Meta Data")
    private AlphaVantageMetaData metaData;
    @JsonProperty("Time Series (Daily)")
    private AlphaVantageTimeSeriesDaily priceData;


    public String getSymbol() {
      log.debug("in first get symbol");
      return metaData.getSymbol();
    }
}
