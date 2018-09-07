package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AlphaVantageStockPriceData {

  @JsonAlias("1. open")
  private String open;

  @JsonAlias("2. high")
  private String high;

  @JsonAlias("3. low")
  private String low;

  @JsonAlias("4. close")
  private String close;

  @JsonAlias("5. volume")
  private String volume;


}
