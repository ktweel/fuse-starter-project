package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AlphaVantageStockPriceData implements Serializable {

    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;

    @JsonProperty("1. open")
    public void setOpen(String open) {
      this.open = open;
    }
    @JsonProperty("open")
    public void setStockDataOpen(String open) { this.open = open;}
    @JsonProperty("open")
    public String getOpen() {
      return open;
    }


    @JsonProperty("2. high")
    public void setHigh(String high) {
      this.high = high;
    }
    @JsonProperty("high")
    public void setStockDataHigh(String high) {
    this.high = high;
  }
    @JsonProperty("high")
    public String getHigh() {
      return high;
    }


    @JsonProperty("3. low")
     public void setLow(String low) {
      this.low = low;
    }
    @JsonProperty("low")
    public void setStockDataLow(String low) {
    this.low = low;
  }
    @JsonProperty("low")
    public String getLow() {
      return low;
    }


    @JsonProperty("4. close")
    public void setClose(String close) {
      this.close = close;
    }
    @JsonProperty("close")
    public void setStockDataClose(String close) {
    this.close = close;
  }
    @JsonProperty("close")
    public String getClose() {
      return close;
    }

    @JsonProperty("5. volume")
    public void setVolume(String volume) {
      this.volume = volume;
    }
    @JsonProperty("volume")
    public void setStockDataVolume(String volume) {
    this.volume = volume;
  }
    @JsonProperty("volume")
    public String getVolume() {
      return volume;
    }

}
