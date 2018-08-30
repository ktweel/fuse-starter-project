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


  public String getPriceData(String symbol, int days) {

    List<LocalDate> dates = getListDates(days);

    AlphaVantageReturnMessage result = databaseCheck(symbol, days, dates);

    AlphaVantageReturnMessage trimmedMessage = trimData(result, dates);

    String json = messageToJson(trimmedMessage);
    persistToDatabase(result);

    return json;
  }


  private String messageToJson(AlphaVantageReturnMessage message) {
//    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    try {
      String json = mapper.writeValueAsString(message);
      log.info("object converted to json {}", json);
      log.debug(json);
      return json;
    } catch (JsonProcessingException e) {
      log.error("error converting to json", e);
      //TODO: throw exception here
      return e.getMessage();
    }
  }

  private void persistToDatabase(AlphaVantageReturnMessage message) {
    List<StockData> data = repository.findByStockSymbol(message.getMetaData().getSymbol());
    StockData stockData;
    if (data.isEmpty()) {
      stockData = new StockData(message.getMetaData().getSymbol(), message);
    } else {
      stockData = data.get(0);
      stockData.setData(message);
    }
    if (repository == null) log.error("null repository");
    log.info("Persisting to Database - {}", stockData.toString());
    repository.save(stockData);
  }

  /**
   * Determine if data for given symbol is already in database - if so returns the
   * AlphaVantageReturnMessage stored, otherwise, makes a call to alpha vantage and
   * returns result
   * @param symbol
   * @param days
   * @param dates
   * @return
   */
  private AlphaVantageReturnMessage databaseCheck(String symbol, int days, List<LocalDate> dates) {
    List<StockData> data = repository.findByStockSymbol(symbol);
    if (data.isEmpty()) {
      return alphaVantageService.alphaVantageCall(symbol, days);
    }
    return data.get(0).getData();
  }

  private AlphaVantageReturnMessage trimData(AlphaVantageReturnMessage fullData,
      List<LocalDate> dates) {
    AlphaVantageReturnMessage trimmedData = new AlphaVantageReturnMessage();
    List<LocalDate> remainingDates = new ArrayList<>();
    trimmedData.setMetaData(fullData.getMetaData());

    for (LocalDate d:dates) {
      if (fullData.getTimeSeriesData().containsKey(d.toString())) {
        trimmedData.setTimeSeriesData(d.toString(), fullData.getTimeSeriesData(d.toString()));
      } else {
        remainingDates.add(d);
      }
    }

    if (!remainingDates.isEmpty()) {
      fillData(trimmedData, fullData, dates.size(), remainingDates);
    }
    log.info(dates.toString());
    return trimmedData;
  }

  private void fillData(AlphaVantageReturnMessage compactData, AlphaVantageReturnMessage historicData,
      int days, List<LocalDate> dates) {
    AlphaVantageReturnMessage fullData = alphaVantageService.alphaVantageCall(compactData.getMetaData().getSymbol(),
        days);
    for (LocalDate d:dates) {
      log.debug("adding date not in database");
      compactData.setTimeSeriesData(d.toString(), fullData.getTimeSeriesData(d.toString()));
      historicData.setTimeSeriesData(d.toString(), fullData.getTimeSeriesData(d.toString()));
    }
    log.info(compactData.toString());
  }


  /**
   * Generate list of desired dates based on number of days requested, excluding weekend dates
   * TODO: exclude holidays from date list
   */
  private List<LocalDate> getListDates(int days) {
    LocalDate today = LocalDate.now();
    List<LocalDate> dates = new ArrayList<>();
    int j = 0;
    while (dates.size() < days) {
      LocalDate day = today.minusDays(j);
      if (day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY) {
        dates.add(day);
      }
      j++;
    }

    return dates;
  }

}
