package org.galatea.starter.service;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.galatea.starter.domain.StockDataMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AlphaVantageService {

  @Value("${alpha-vantage.uri}")
  private String uri;

  @Value("${alpha-vantage.query}")
  private static String query;


  interface FuseServer {

    @RequestLine("GET /query?function=TIME_SERIES_DAILY&symbol={symbol}&outputsize={size}&apikey=PFPOE75HO1WCKW7H")
    AlphaVantageReturnMessage alphaVantageApiCall(@Param("symbol") String symbol, @Param("size") String outputSize);

  }

  /**
   * Makes api call to Alpha Vantage to retrieve data not persisted in database
   * @param symbol stock symbol
   * @param numDays number of days request is for, used to determine full or compact
   * @return AlphaVantageReturnMessage containing the data from the Alpha Vantage request
   */
  public AlphaVantageReturnMessage alphaVantageCall(String symbol, int numDays) {
    String hostName = uri;
    FuseServer fuseServer = Feign.builder()
        .decoder(new JacksonDecoder())
        .encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);
    log.info("Calling Alpha Vantage for symbol: {} and numDays: {}", symbol, numDays);
    long startTime = System.currentTimeMillis();
    AlphaVantageReturnMessage alphaVantageReturnMessage = fuseServer.alphaVantageApiCall(symbol, (numDays > 100) ? "full" : "compact");
    long endTime = System.currentTimeMillis();
    log.info("Alpha Vantage call completed in: {}ms", endTime-startTime);
    return alphaVantageReturnMessage;
  }


}
