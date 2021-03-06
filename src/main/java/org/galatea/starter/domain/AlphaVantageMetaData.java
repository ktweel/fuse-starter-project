package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;



@Data
@Embeddable
public class AlphaVantageMetaData {

  @JsonProperty("1. Information")
  private String information;

  @JsonProperty("2. Symbol")
  private String symbol;

  @JsonProperty("3. Last Refreshed")
  private String lastRefreshed;

  @JsonProperty("4. Output Size")
  private String outputSize;

  @JsonProperty("5. Time Zone")
  private String timeZone;

}
