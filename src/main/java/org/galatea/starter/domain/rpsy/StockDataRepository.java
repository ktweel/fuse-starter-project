package org.galatea.starter.domain.rpsy;

import java.util.List;
import org.galatea.starter.domain.StockData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockDataRepository extends MongoRepository<StockData, String> {

  List<StockData> findByStockSymbolAndDateIn(String stockSymbol, List<String> Dates);

  List<StockData> findByStockSymbol(String stockSymbol);
}
