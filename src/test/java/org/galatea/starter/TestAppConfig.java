package org.galatea.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendarId;
import com.opengamma.strata.basics.date.HolidayCalendarIds;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.galatea.starter.entrypoint.StockPriceController;
import org.galatea.starter.service.AlphaVantageService;
import org.galatea.starter.service.DatabaseService;
import org.galatea.starter.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestAppConfig {


  @Value("${alpha-vantage.uri}")
  private String uri;

  @Bean
  ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    return mapper;
  }

  @Bean
  AlphaVantageServer alphaVantageServer() {
    return Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder())
        .target(AlphaVantageServer.class, uri);
  }

  @Bean
  HolidayCalendar holidayCalendar() {
    HolidayCalendarId holidayCalendarId = HolidayCalendarIds.NYSE;
    return holidayCalendarId.resolve(ReferenceData.standard());
  }

//
//  @Bean
//  StockPriceController stockPriceController(StockPriceService stockPriceService) {
//    return new StockPriceController(stockPriceService);
//  }
//
//  @Bean
//  StockPriceService stockPriceService(DatabaseService databaseService, AlphaVantageService
//      alphaVantageService, HolidayCalendar holidayCalendar) {
//    return new StockPriceService(databaseService, alphaVantageService, holidayCalendar);
//  }
//
//  @Bean
//  AlphaVantageService alphaVantageService(AlphaVantageServer alphaVantageServer) {
//    return new AlphaVantageService(alphaVantageServer);
//  }
//
//  @Bean
//  DatabaseService databaseService(StockDataRepository repository) {
//    return new DatabaseService(repository);
//  }

}
