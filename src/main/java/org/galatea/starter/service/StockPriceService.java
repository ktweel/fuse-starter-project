package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.galatea.starter.domain.StockDataMessage;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service which requests data from Alpha Vantage and transforms it to return desired dates
 */
@Slf4j
@Service
@AllArgsConstructor
public class StockPriceService {

  private final DatabaseService databaseService;
  private final ObjectMapper mapper;
  private final AlphaVantageService alphaVantageService;


  /**
   * Given a stock symbol and number of days, returns json containing stock price data for
   * the past n number of days for the given stock
   * @param symbol stock symbol for which data should be returned
   * @param days number of days for which data should be returned
   * @return string representing json for stock price data
   * @throws JsonProcessingException when error occurs converting to json
   */
  public String getPriceData(String symbol, int days) throws JsonProcessingException{

    log.info("Retrieving price data for symbol: {} and {} days", symbol, days);
    List<String> dates = getListDates(days);

    StockDataMessage result = databaseService.databaseCheck(symbol, days, dates);

    if (!dates.isEmpty()) {
      AlphaVantageReturnMessage avMessage = alphaVantageService.alphaVantageCall(symbol, days, result, dates);
      convertToStockDataMessage(avMessage, dates, result);
    }

    return messageToJson(result);
  }

  /**
   * Given a stock symbol, returns json continaing stock price data for all
   * stored database values for that stock
   * @param symbol stock symbol for which data should be returned
   * @return string representing json for stock price data
   * @throws JsonProcessingException when unable to convert to json
   */
  public String getPriceData(String symbol) throws JsonProcessingException{
    log.info("Retrieving all database entries for stock symbol: {}", symbol);
    StockDataMessage message = databaseService.dumpDatabase(symbol);
    return messageToJson(message);
  }

  /**
   * converts StockDataMessage to a json string
   * @param message StockData message to be converted to json
   * @return string representing json version of message
   * @throws JsonProcessingException when error converting to json
   */
  private String messageToJson(StockDataMessage message) throws JsonProcessingException{
    try {
      String json = mapper.writeValueAsString(message);
      log.info("object converted to json {}", json);
      return json;
    } catch (JsonProcessingException e) {
      log.error("error converting to json", e);
      throw e;
    }
  }

  /**
   * Convert AlphaVantageReturn message generated by  api call to StockDataMessage
   * @param avMessage AlphaVantage message generated by api call
   * @param dates list of relevant dates to be added to stockDataMessage
   * @param stockDataMessage stockDataMessage possibly containing data, to which stock
   *        price data for relevant dates is added
   */
  private void convertToStockDataMessage(AlphaVantageReturnMessage avMessage, List<String> dates, StockDataMessage stockDataMessage) {
    stockDataMessage.setSymbol(avMessage.getMetaData().getSymbol());
    for(String d:dates) {
      stockDataMessage.setTimeSeriesData(d, avMessage.getTimeSeriesData(d));
      databaseService.save(stockDataMessage.getSymbol(), d, avMessage.getTimeSeriesData(d));
    }
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
      LocalDate day = today.minusDays(j++);
      if (day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY) {
        dates.add(day.toString());
      }
    }

    return dates;
  }

}
