package org.galatea.starter.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageStockPriceData;
import org.galatea.starter.domain.StockData;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DatabaseService {

  private final StockDataRepository repository;

  public void save(String symbol, String date, AlphaVantageStockPriceData priceData) {
    repository.save(new StockData(symbol, date, priceData));
  }

  /**
   * returns StockDataMessage for the given symbol containing any price data for that stock that is
   * stored in the database.
   */
  public StockDataMessage dumpDatabase(String symbol) {
    List<StockData> stocks = repository.findByStockSymbol(symbol);
    StockDataMessage message = new StockDataMessage();

    message.setSymbol(symbol);
    for (StockData d : stocks) {
      message.setTimeSeriesData(d.getDate(), d.getPriceData());
    }

    return message;
  }

  /**
   * Checks the database for price data for the given stock symbol and list of dates, returns a
   * StockDataMessage containing any price data in the database.
   */
  public StockDataMessage databaseCheck(String symbol, List<String> dates) {
    List<StockData> data = repository.findByStockSymbolAndDateIn(symbol, dates);
    StockDataMessage message = new StockDataMessage();

    message.setSymbol(symbol);
    for (StockData d : data) {
      message.setTimeSeriesData(d.getDate(), d.getPriceData());
    }

    return message;
  }
}