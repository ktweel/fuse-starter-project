package org.galatea.starter.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.AlphaVantageServer;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.galatea.starter.domain.StockDataMessage;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class AlphaVantageService {

  private final AlphaVantageServer alphaVantageServer;

  /**
   * Makes api call to Alpha Vantage to retrieve data not persisted in database
   * @param symbol stock symbol
   * @param numDays number of days request is for, used to determine full or compact
   * @return AlphaVantageReturnMessage containing the data from the Alpha Vantage request
   */
  public AlphaVantageReturnMessage alphaVantageCall(String symbol, int numDays, StockDataMessage stockDataMessage, List<String> dates) {
    log.info("Calling Alpha Vantage for symbol: {} and numDays: {}", symbol, numDays);
    long startTime = System.currentTimeMillis();
    AlphaVantageReturnMessage alphaVantageReturnMessage = alphaVantageServer
        .alphaVantageApiCall(symbol, (numDays > 100) ? "full" : "compact");
    long endTime = System.currentTimeMillis();
    log.info("Alpha Vantage call completed in: {}ms", endTime-startTime);
    return alphaVantageReturnMessage;
  }

}
