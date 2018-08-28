package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.galatea.starter.domain.AlphaVantageTimeSeriesDaily;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class StockPriceService {

  public static String getPriceData(String symbol, int days) {
    final String uri = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&outputsize=%s&apikey=PFPOE75HO1WCKW7H";

    ObjectMapper mapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    log.info("calling alpha vantage");
    String outputSize;
    if (days > 100) outputSize = "full"; else outputSize = "compact";
    AlphaVantageReturnMessage result = restTemplate.getForObject(String.format(uri, symbol, outputSize), AlphaVantageReturnMessage.class);

    log.info("alpha vantage called");

    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    try {
        String json = mapper.writeValueAsString(trimPriceData(result, days));
        log.info(json);
        return json;
    } catch (JsonProcessingException e) {
        log.error("unable to convert object to json");
        return "error converting to json: " + e.getMessage();
    }

  }

  private static AlphaVantageReturnMessage trimPriceData(AlphaVantageReturnMessage data, int days) {
    AlphaVantageReturnMessage trimmedData = new AlphaVantageReturnMessage();
    trimmedData.setMetaData(data.getMetaData());
//    AlphaVantageTimeSeriesDaily trimmedDaily = new AlphaVantageTimeSeriesDaily();
    List<LocalDate> dates = getListDates(days);
    for (LocalDate d:dates
    ) {
      trimmedData.setTimeSeriesData(d.toString(), data.getTimeSeriesData(d.toString()));
    }

    log.info(dates.toString());
    return trimmedData;
  }

  private static List<LocalDate> getListDates(int days) {
    LocalDate today = LocalDate.now();
    List<LocalDate> dates = new ArrayList<>();
    int j = 0;
    for (int i = 0; i < days; i++) {
      LocalDate day = today.minusDays(j);
      if (day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY) {
        dates.add(day);
        j++;
      } else {
        i--;
        j++;
      }
    }
    return dates;
  }

}
