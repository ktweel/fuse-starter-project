package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.galatea.starter.domain.StockData;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service which requests data from Alpha Vantage and transforms it to return desired dates
 */
@Slf4j
@Service
public class StockPriceService {

  @Autowired
  StockDataRepository repository;
  @Autowired
  ObjectMapper mapper;
  @Autowired
  AlphaVantageService alphaVantageService;


  /**
   * Given a stock symbol and number of days, returns json containing stock price data for
   * the past n number of days for the given stock
   * @param symbol
   * @param days
   * @return
   * @throws JsonProcessingException
   */
  public String getPriceData(String symbol, int days) throws JsonProcessingException{

    List<String> dates = getListDates(days);

    StockDataMessage result = databaseCheck(symbol, days, dates);

    if (!dates.isEmpty()) {
      alphaVantageService.alphaVantageCall(symbol, days, result, dates);
    }

    return messageToJson(result);
  }

  /**
   * converts StockDataMessage to a json string
   * @param message
   * @return
   * @throws JsonProcessingException
   */
  private String messageToJson(StockDataMessage message) throws JsonProcessingException{
    try {
      String json = mapper.writeValueAsString(message);
      log.info("object converted to json {}", json);
      log.debug(json);
      return json;
    } catch (JsonProcessingException e) {
      log.error("error converting to json", e);
      throw e;
    }
  }

  /**
   * returns json for the given symbol containing any price data for that stock
   * that is stored in the database
   * @param symbol
   * @return
   * @throws JsonProcessingException
   */
  public String dumpDatabase(String symbol) throws JsonProcessingException{
    List<StockData> stocks = repository.findByStockSymbol(symbol);
    StockDataMessage message = new StockDataMessage();

    message.setSymbol(symbol);
    for (StockData d:stocks) {
      message.setTimeSeriesData(d.getDate(), d.getPriceData());
    }

    return messageToJson(message);
  }

  /**
   * Checks the database for price data for the given stock symbol and list of dates,
   * returns a StockDataMessage containing any price data in the database, removes any
   * dates for which data was found from the list of dates given
   * @param symbol
   * @param days
   * @param dates
   * @return
   */
  private StockDataMessage databaseCheck(String symbol, int days, List<String> dates) {
    List<StockData> data = repository.findByStockSymbolAndDateIn(symbol, dates);
    StockDataMessage message = new StockDataMessage();

    message.setSymbol(symbol);
    for (StockData d:data) {
      message.setTimeSeriesData(d.getDate(), d.getPriceData());
      dates.remove(d.getDate());
    }

    return message;
  }

  /**
   * Generate list of desired dates based on number of days requested, excluding weekend dates
   * TODO: exclude holidays from date list
   */
  private List<String> getListDates(int days) {
    LocalDate today = LocalDate.now();
    List<String> dates = new ArrayList<>();
    int j = 0;
    while (dates.size() < days) {
      LocalDate day = today.minusDays(j);
      if (day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY) {
        dates.add(day.toString());
      }
      j++;
    }

    return dates;
  }

}
