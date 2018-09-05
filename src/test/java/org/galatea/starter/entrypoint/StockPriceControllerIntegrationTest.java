package org.galatea.starter.entrypoint;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockDataMessage;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@SpringBootTest
public class StockPriceControllerIntegrationTest {

  //@Value("${stock-test.url}")
  private String hostName = "http://localhost:8080";

  /**
   * Test general functionality
   */
  @Test
  public void testPrice() {
    String symbol = "MSFT";
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    StockDataMessage priceResponse = stockPriceServer.priceEndpoint(symbol, 2);
    log.info(priceResponse.toString());
    assertEquals(2, priceResponse.getTimeSeriesData().size());
  }

  /**
   * Test case where alpha vantage api must be called
   */
  @Test
  public void testPriceCallAlphaVantage() {
    String symbol = "DNKN";
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    StockDataMessage message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.getTimeSeriesData().size();

    StockDataMessage priceResponse = stockPriceServer.priceEndpoint(symbol, numDataPoints + 5);
    log.info(priceResponse.toString());
    assertEquals(numDataPoints + 5, priceResponse.getTimeSeriesData().size());
  }

  /**
   * Test case where data is retrieved solely from database, if database is empty, initial
   * call made to populate database before test call is made
   */
  @Test
  public void testPriceNoAlphaVantage() {
    String symbol = "MSFT";
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    StockDataMessage message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.getTimeSeriesData().size();
    int requestNum;
    if (numDataPoints > 1) {
      requestNum = numDataPoints - 1;
    } else {
      stockPriceServer.priceEndpoint(symbol, 10);
      requestNum = 5;
    }

    StockDataMessage priceResponse = stockPriceServer.priceEndpoint(symbol, requestNum);
    log.info(priceResponse.toString());
    assertEquals(requestNum, priceResponse.getTimeSeriesData().size());
  }

  /**
   * test case where some data is retrieved from database and some data retrieved
   * from alpha vantage api call
   */
  @Test
  public void testPriceDatabaseAndAlphaVantage() {
    String symbol = "MSFT";
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    StockDataMessage message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.getTimeSeriesData().size();

    if (numDataPoints == 0) {
      stockPriceServer.priceEndpoint(symbol, 5);
      numDataPoints = 5;
    }

    StockDataMessage priceResponse = stockPriceServer.priceEndpoint(symbol, numDataPoints + 5);
    log.info(priceResponse.toString());
    assertEquals(numDataPoints + 5, priceResponse.getTimeSeriesData().size());
  }

  /**
   * Test case with invalid stock symbol
   */
  @Test
  public void testInvalidSymbol() {
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    try {
      stockPriceServer.priceEndpoint("MSFTAAAA", 2);
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Invalid API call"));
    }
  }

  /**
   * Test case with no symbol given
   */
  @Test
  public void testNoSymbol() {
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    try {
      stockPriceServer.priceEndpointNoSymbol(2);
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Required String parameter 'stock' is not present"));
    }
  }

  /**
   * test case where negative number of days given
   */
  @Test
  public void testInvalidDays() {
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    try {
      StockDataMessage priceResponse = stockPriceServer.priceEndpoint("MSFT", -1);
      log.info(priceResponse.toString());
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Invalid days value given"));
    }
  }

  /**
   * test alpha vantage full data call
   */
  @Test
  public void testAlphaVantageFull() {
    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);

    String symbol = "FB";
    StockDataMessage message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.getTimeSeriesData().size();
    int plusValue = numDataPoints + 101;

    StockDataMessage priceResponse = stockPriceServer.priceEndpoint(symbol, plusValue);
    log.info(priceResponse.toString());
    assertEquals(plusValue, priceResponse.getTimeSeriesData().size());

  }

  /**
   * Test database dump path where no days given and contents of database retrieved
   */
  @Test
  public void testStockDump() {

    StockPriceServer stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);
    StockDataMessage message = stockPriceServer.priceEndpointNoDays("MSFT");
    int curr_size = message.getTimeSeriesData().size();
    stockPriceServer.priceEndpoint("MSFT", curr_size + 10);
    stockPriceServer.priceEndpoint("AAPL", 3);
    stockPriceServer.priceEndpoint("MSFT", 8);

    StockDataMessage databaseDump = stockPriceServer.priceEndpointNoDays("MSFT");
    assertEquals(curr_size + 10, databaseDump.getTimeSeriesData().size());
  }

  interface StockPriceServer {
    @RequestLine("GET /price?stock={symbol}&days={days}")
    StockDataMessage priceEndpoint(@Param("symbol") String stock, @Param("days") int days);

    @RequestLine("GET /price?stock={symbol}")
    StockDataMessage priceEndpointNoDays(@Param("symbol") String stock);

    @RequestLine("GET /price?days={days}")
    StockDataMessage priceEndpointNoSymbol(@Param("days") int days);
  }

  @After
  public void pauseAfterTest() throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
  }

}