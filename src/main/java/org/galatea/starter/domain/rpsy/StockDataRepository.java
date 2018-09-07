package org.galatea.starter.domain.rpsy;

import org.galatea.starter.domain.StockData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockDataRepository extends MongoRepository<StockData, String> {

  List<StockData> findByStockSymbolAndDateIn(String stockSymbol, List<String> dates);

  List<StockData> findByStockSymbol(String stockSymbol);
}
