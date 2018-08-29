package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.galatea.starter.domain.StockData;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service which requests data from Alpha Vantage and transforms it to return desired dates
 */
@Slf4j
@Component
public class StockPriceService {

  @Autowired
  StockDataRepository repository;
  @Autowired
  ObjectMapper mapper;
  private final String uri = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&outputsize=%s&apikey=PFPOE75HO1WCKW7H";
  public String getPriceData(String symbol, int days) {

    List<LocalDate> dates = getListDates(days);

    AlphaVantageReturnMessage result = databaseCheck(symbol, days, dates);

    List<LocalDate> remainingDates = new ArrayList<>();
    AlphaVantageReturnMessage trimmedMessage = trimData(result, dates, remainingDates);
    if (!remainingDates.isEmpty()) {
//      trimmedMessage = fillData(trimmedMessage, days, remainingDates);
      fillData(trimmedMessage, days, remainingDates);
    }
    log.info(remainingDates.toString());
    String json = messageToJson(trimmedMessage);
    persistToDatabase(trimmedMessage);

    return json;
  }

  private AlphaVantageReturnMessage alphaVantageCall(String symbol, int numDays, List<LocalDate> dates) {
    ObjectMapper mapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    log.info("calling alpha vantage");
    String outputSize;
    if (numDays > 100) outputSize = "full"; else outputSize = "compact";
    AlphaVantageReturnMessage result = restTemplate.getForObject(String.format(uri, symbol, outputSize), AlphaVantageReturnMessage.class);

    log.info("alpha vantage called");
    return result;
  }

  private String messageToJson(AlphaVantageReturnMessage message) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    try {
      String json = mapper.writeValueAsString(message);
      log.info(json);
      return json;
    } catch (JsonProcessingException e) {
      log.error("error converting to json");
      return String.format("error converting to json: %s", e.getMessage());
    }
  }

  private void persistToDatabase(AlphaVantageReturnMessage message) {
    List<StockData> data = repository.findByStockSymbol(message.getMetaData().getSymbol());
    StockData stockData;
    if (data.isEmpty()) {
      stockData = new StockData(message.getMetaData().getSymbol(), message);
    } else {
      stockData = data.get(0);
      AlphaVantageReturnMessage oldData = stockData.getData();
      for (String key : oldData.getTimeSeriesData().keySet()) {
        if (!message.getTimeSeriesData().containsKey(key)) {
          message.setTimeSeriesData(key, oldData.getTimeSeriesData(key));
        }
        stockData.setData(message);
      }

    }
    if (repository == null) log.error("null repository");
    log.info(stockData.toString());
    repository.save(stockData);

  }

  private AlphaVantageReturnMessage databaseCheck(String symbol, int days, List<LocalDate> dates) {
    List<StockData> data = repository.findByStockSymbol(symbol);
    if (data.isEmpty()) {
      return alphaVantageCall(symbol, days, dates);
    } else {
      return data.get(0).getData();
    }
  }

  private static AlphaVantageReturnMessage trimData(AlphaVantageReturnMessage fullData,
       List<LocalDate> dates, List<LocalDate> remainingDates) {
    AlphaVantageReturnMessage trimmedData = new AlphaVantageReturnMessage();
    trimmedData.setMetaData(fullData.getMetaData());
    for (LocalDate d:dates
        ) {
      if (fullData.getTimeSeriesData().containsKey(d.toString())) {
        trimmedData.setTimeSeriesData(d.toString(), fullData.getTimeSeriesData(d.toString()));
      } else {
        remainingDates.add(d);
      }

    }
    log.info(dates.toString());
    return trimmedData;
  }

  private void fillData(AlphaVantageReturnMessage compactData, int days, List<LocalDate> dates) {
    AlphaVantageReturnMessage fullData = alphaVantageCall(compactData.getMetaData().getSymbol(),
        days, dates);
    log.info("alpha vantage call to fill data");
    for (LocalDate d:dates) {
      log.info("here");
      compactData.setTimeSeriesData(d.toString(), fullData.getTimeSeriesData(d.toString()));
    }
    log.info(compactData.toString());
//    return compactData;
  }


  /**
   * Generate list of desired dates based on number of days requested
   */
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
