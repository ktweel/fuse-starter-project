package org.galatea.starter.domain;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Id;

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

  /**
   * All arguments constructor for StockData.
   * @param symbol stock symbol
   * @param date date of data
   * @param priceData price data for given date
   */
  public StockData(String symbol, String date, AlphaVantageStockPriceData priceData) {
    this.stockSymbol = symbol;
    this.date = date;
    this.priceData = priceData;
  }

}
