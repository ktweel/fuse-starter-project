package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import lombok.Data;

@Data
@Embeddable
public class AlphaVantageStockPriceData implements Serializable{
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
