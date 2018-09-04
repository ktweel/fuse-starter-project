package org.galatea.starter.entrypoint;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockDataMessage;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@SpringBootTest (properties = "spring.datasource.url=jdbc:h2:mem:runtests")
@DataJpaTest
public class StockPriceControllerIntegrationTest {

  //@Value("${stock-test.url}")
  private String hostName = "http://localhost:8080";

  @Test
  public void testPrice() {
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);

    StockDataMessage priceResponse = fuseServer.priceEndpoint("MSFT", 2);
    log.info(priceResponse.toString());
    assertEquals(2, priceResponse.getTimeSeriesData().size());
  }

  @Test
  public void testPriceCallAlphaVantage() {
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);

    StockDataMessage message = fuseServer.priceEndpointNoDays("MSFT");
    int numDataPoints = message.getTimeSeriesData().size();

    StockDataMessage priceResponse = fuseServer.priceEndpoint("MSFT", numDataPoints + 5);
    log.info(priceResponse.toString());
    assertEquals(numDataPoints + 5, priceResponse.getTimeSeriesData().size());
  }

  @Test
  public void testInvalidSymbol() {
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);

    try {
      StockDataMessage priceResponse = fuseServer.priceEndpoint("MSFTAAAA", 2);
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Invalid API call"));
    }
  }

  @Test
  public void testNoSymbol() {
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);

    try {
      StockDataMessage priceResponse = fuseServer.priceEndpointNoSymbol(2);
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Required String parameter 'stock' is not present"));
    }
  }

  @Test
  public void testInvalidDays() {
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);

    try {
      StockDataMessage priceResponse = fuseServer.priceEndpoint("MSFT", -1);
      assertTrue(false);
    } catch (Exception e) {
      log.info(e.getMessage());
      assertTrue(e.getMessage().contains("Invalid days value given"));
    }
  }

  @Test
  public void testStockDump() {

    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);
    StockDataMessage message = fuseServer.priceEndpointNoDays("MSFT");
    int curr_size = message.getTimeSeriesData().size();
    fuseServer.priceEndpoint("MSFT", curr_size + 10);
    fuseServer.priceEndpoint("AAPL", 3);
    fuseServer.priceEndpoint("MSFT", 10);

    StockDataMessage databaseDump = fuseServer.priceEndpointNoDays("MSFT");
    assertEquals(curr_size + 10, databaseDump.getTimeSeriesData().size());
  }


  interface FuseServer {
    @RequestLine("GET /price?stock={symbol}&days={days}")
    StockDataMessage priceEndpoint(@Param("symbol") String stock, @Param("days") int days);

    @RequestLine("GET /price?stock={symbol}")
    StockDataMessage priceEndpointNoDays(@Param("symbol") String stock);

    @RequestLine("GET /price?days={days}")
    StockDataMessage priceEndpointNoSymbol(@Param("days") int days);
}
}
