package org.galatea.starter.entrypoint;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockData;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertEquals;

@Slf4j
@SpringBootTest
public class StockPriceControllerIntegrationTest {

  //@Value("${stock-test.url}")
  private String hostName = "http://localhost:8080";

  @Test
  public void testPrice() {
    log.info(hostName);
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, hostName);

    StockDataMessage priceResponse = fuseServer.priceEndpoint("MSFT", "2");
    log.info(priceResponse.toString());
  }

//  @Test
//  public void testStockDump() {
//    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
//        .target(FuseServer.class, hostName);
//    fuseServer.priceEndpoint("MSFT", "7");
//    fuseServer.priceEndpoint("MSFT", "4");
//    fuseServer.priceEndpoint("AAPL", "3");
//    fuseServer.priceEndpoint("MSFT", "10");
//
//    StockDataMessage databaseDump = fuseServer.priceEndpointNoDays("MSFT");
//    assertEquals(10, databaseDump.getTimeSeriesData().size());
//  }

  interface FuseServer {
    @RequestLine("GET /price?stock={symbol}&days={days}")
    StockDataMessage priceEndpoint(@Param("symbol") String stock, @Param("days") String days);

    @RequestLine("GET /price?stock={symbol}")
    StockDataMessage priceEndpointNoDays(@Param("symbol") String stock);
}
}
