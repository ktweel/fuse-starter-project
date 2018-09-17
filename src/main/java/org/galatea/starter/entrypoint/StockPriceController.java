package org.galatea.starter.entrypoint;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.service.StockPriceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


/**
 * Entrypoint for requests for stock price data.
 */
@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class StockPriceController {

  private final StockPriceService service;

  /**
   * method handling /price requests.
   * @param stock stock symbol for which data requested
   * @param days number of days for which data requested
   * @return StockDataMessage containing relevant stock price data
   */
  @RequestMapping(value = "/price", produces = "application/json")
  public List<StockDataMessage> getStockPrices(@Size(min = 1) @RequestParam(value = "stock")
      List<String> stock, @Min(0) @RequestParam(value = "days", required = false) Integer days) {
    if (days == null) {
      return stock.parallelStream().map(s -> service.getPriceData(s.toUpperCase()))
          .collect(Collectors.toList());
    } else {
      return stock.parallelStream().map(s -> service.getPriceData(s.toUpperCase(), days))
          .collect(Collectors.toList());
    }

  }

}