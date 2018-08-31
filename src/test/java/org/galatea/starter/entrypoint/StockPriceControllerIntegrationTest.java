package org.galatea.starter.entrypoint;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockData;
import org.galatea.starter.domain.StockDataMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class StockPriceControllerIntegrationTest {

  @Value("${stock-test.url}")
  private String hostName;

  @Test
  public void testPrice() {
    log.info(hostName);
    FuseServer fuseServer = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(FuseServer.class, "http://localhost:8080");

    StockDataMessage priceResponse = fuseServer.priceEndpoint("MSFT", "2");
    log.info(priceResponse.toString());
  }

  interface FuseServer {
    @RequestLine("GET /price?stock={symbol}&days={days}")
    StockDataMessage priceEndpoint(@Param("symbol") String stock, @Param("days") String days);
  }
}
