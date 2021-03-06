package org.galatea.starter.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.AlphaVantageServer;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class AlphaVantageService {

  private final AlphaVantageServer alphaVantageServer;
  // number of datapoints returned when compact alpha vantage call is made
  private static final int COMPACT_DATA_LIMIT = 100;
  private String apiKey;

  /**
   * Makes api call to Alpha Vantage to retrieve data not persisted in database.
   * @param symbol stock symbol
   * @param numDays number of days request is for, used to determine full or compact
   * @return AlphaVantageReturnMessage containing the data from the Alpha Vantage request
   */
  public AlphaVantageReturnMessage alphaVantageCall(String symbol, int numDays) {
    log.info("Calling Alpha Vantage for symbol: {} and numDays: {}", symbol, numDays);
    long startTime = System.currentTimeMillis();
    AlphaVantageReturnMessage alphaVantageReturnMessage = alphaVantageServer
        .alphaVantageApiCall(symbol, (numDays > COMPACT_DATA_LIMIT) ? "full" : "compact", apiKey);
    long endTime = System.currentTimeMillis();
    log.info("Alpha Vantage call completed in: {}ms", endTime - startTime);
    return alphaVantageReturnMessage;
  }

}
