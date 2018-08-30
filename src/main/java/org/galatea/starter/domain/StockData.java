package org.galatea.starter.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NonNull;

@Entity
@Data
@Table(name="stock")
public class StockData {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NonNull
  @Column(name="stockSymbol")
  private String stockSymbol;

  @NonNull
  @Column(name="data")
  @Access(AccessType.PROPERTY)
  private AlphaVantageReturnMessage data;

  protected StockData(){}

  public StockData(String symbol, AlphaVantageReturnMessage data) {
    this.stockSymbol = symbol;
    this.data = data;
  }

}
