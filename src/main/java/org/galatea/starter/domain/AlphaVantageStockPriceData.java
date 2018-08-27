package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlphaVantageStockPriceData {
    @JsonProperty("1. open")
    private String open;
    @JsonProperty("2. high")
    private String high;
    @JsonProperty("3. low")
    private String low;
    @JsonProperty("4. close")
    private String close;
    @JsonProperty("5. volume")
    private String volume;

}
