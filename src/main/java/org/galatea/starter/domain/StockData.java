package org.galatea.starter.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import lombok.Data;
import lombok.NonNull;

@Data
public class StockData {
  @Id
  private String id;

  @NonNull
  @Column(name = "stockSymbol")
  private String stockSymbol;

  @NonNull
  @Column(name = "date")
  private String date;

  @NonNull
  @Column(name = "priceData")
  private AlphaVantageStockPriceData priceData;

  protected StockData(){}

  public StockData(String symbol, String date, AlphaVantageStockPriceData priceData) {
    this.stockSymbol = symbol;
    this.date = date;
    this.priceData = priceData;
  }

}
