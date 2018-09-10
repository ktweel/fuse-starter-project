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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AppConfig {

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

}

