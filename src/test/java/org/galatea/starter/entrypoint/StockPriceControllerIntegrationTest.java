package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.galatea.starter.service.StockPriceService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
//@WebMvcTest
public class StockPriceControllerIntegrationTest {

//  @Value("${stock-test.url}")
//  private String hostName;
  private static String hostName = "http://localhost:8080";

  @Autowired
  StockDataRepository repository;

  private static StockPriceServer stockPriceServer;


  /**
   * Test general functionality
   */
  @Test
  public void testPrice() {
    String symbol = "MSFT";

    List<StockDataMessage> priceResponse = stockPriceServer.priceEndpoint(symbol, 2);
    log.info(priceResponse.toString());
    assertEquals(2, priceResponse.get(0).getTimeSeriesData().size());
  }

  /**
   * Test case where alpha vantage api must be called
   */
  @Test
  public void testPriceCallAlphaVantage() throws JsonProcessingException {
    String symbol = "DNKN";

    List<StockDataMessage> message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.get(0).getTimeSeriesData().size();

    List<StockDataMessage> priceResponse = stockPriceServer.priceEndpoint(symbol,
        numDataPoints + 5);
    log.info(priceResponse.toString());
    assertEquals(numDataPoints + 5, priceResponse.get(0).getTimeSeriesData().size());
  }

  /**
   * Test case where data is retrieved solely from database, if database is empty, initial
   * call made to populate database before test call is made
   */
  @Test
  public void testPriceNoAlphaVantage() {
    String symbol = "MSFT";

    List<StockDataMessage> message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.get(0).getTimeSeriesData().size();
    int requestNum;
    if (numDataPoints > 1) {
      requestNum = numDataPoints - 1;
    } else {
      stockPriceServer.priceEndpoint(symbol, 10);
      requestNum = 5;
    }

    List<StockDataMessage> priceResponse = stockPriceServer.priceEndpoint(symbol, requestNum);
    log.info(priceResponse.toString());
    assertEquals(requestNum, priceResponse.get(0).getTimeSeriesData().size());
  }

  /**
   * test case where some data is retrieved from database and some data retrieved
   * from alpha vantage api call
   */
  @Test
  public void testPriceDatabaseAndAlphaVantage() {
    String symbol = "MSFT";

    List<StockDataMessage> message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.get(0).getTimeSeriesData().size();

    if (numDataPoints == 0) {
      stockPriceServer.priceEndpoint(symbol, 5);
      numDataPoints = 5;
    }

    List<StockDataMessage> priceResponse = stockPriceServer.priceEndpoint(symbol,
        numDataPoints + 5);
    log.info(priceResponse.toString());
    assertEquals(numDataPoints + 5, priceResponse.get(0).getTimeSeriesData().size());
  }

  /**
   * Test case with invalid stock symbol
   */
  @Test
  public void testInvalidSymbol() {

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

    try {
      stockPriceServer.priceEndpointNoSymbol(2);
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Required List parameter 'stock' is not present"));
    }
  }

  /**
   * test case where negative number of days given
   */
  @Test
  public void testInvalidDays() {

    try {
      List<StockDataMessage> priceResponse = stockPriceServer.priceEndpoint("MSFT", -1);
      log.info(priceResponse.toString());
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("must be greater than or equal to 0"));
    }
  }

  /**
   * test alpha vantage full data call
   */
  @Test
  public void testAlphaVantageFull() {

    String symbol = "FB";
    List<StockDataMessage> message = stockPriceServer.priceEndpointNoDays(symbol);
    int numDataPoints = message.get(0).getTimeSeriesData().size();
    int plusValue = numDataPoints + ((numDataPoints > 100) ? 5 : 101);

    List<StockDataMessage> priceResponse = stockPriceServer.priceEndpoint(symbol, plusValue);
    log.info(priceResponse.toString());
    assertEquals(plusValue, priceResponse.get(0).getTimeSeriesData().size());

  }

  /**
   * Test database dump path where no days given and contents of database retrieved
   */
  @Test
  public void testStockDump() {

    List<StockDataMessage> message = stockPriceServer.priceEndpointNoDays("MSFT");
    int curr_size = message.get(0).getTimeSeriesData().size();
    stockPriceServer.priceEndpoint("MSFT", curr_size + 10);
    stockPriceServer.priceEndpoint("MSFT", 8);

    List<StockDataMessage> databaseDump = stockPriceServer.priceEndpointNoDays("MSFT");
    assertEquals(curr_size + 10, databaseDump.get(0).getTimeSeriesData().size());
  }

  interface StockPriceServer {
    @RequestLine("GET /price?stock={symbol}&days={days}")
    List<StockDataMessage> priceEndpoint(@Param("symbol") String stock, @Param("days") int days);

    @RequestLine("GET /price?stock={symbol}")
    List<StockDataMessage> priceEndpointNoDays(@Param("symbol") String stock);

    @RequestLine("GET /price?days={days}")
    List<StockDataMessage> priceEndpointNoSymbol(@Param("days") int days);
  }

  @BeforeClass
  public static void setUpServer() {
    stockPriceServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(StockPriceServer.class, hostName);
  }

  @After
  public void pauseAfterTest() throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
}

}
