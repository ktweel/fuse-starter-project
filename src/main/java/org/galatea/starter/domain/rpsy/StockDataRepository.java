package org.galatea.starter.domain.rpsy;

import java.util.List;
import org.galatea.starter.domain.StockData;
import org.springframework.data.repository.CrudRepository;

public interface StockDataRepository extends CrudRepository<StockData, Long> {

  List<StockData> findByStockSymbol(String stockSymbol);
}
