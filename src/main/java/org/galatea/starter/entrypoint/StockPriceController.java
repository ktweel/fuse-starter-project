package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.entrypoint.exception.NegativeValueException;
import org.galatea.starter.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Entrypoint for requests for stock price data
 */
@RestController
@Slf4j
public class StockPriceController {

  @Autowired
  StockPriceService service;

  @RequestMapping("/price")
  public String getStockPrices(@RequestParam(value="stock") String stock,
      @RequestParam(value="days", required=false) Integer days) throws JsonProcessingException{
    if (days == null) {
      return service.dumpDatabase(stock.toUpperCase());
    }
    if (days < 1) {
      throw new NegativeValueException(days);
    }
    return service.getPriceData(stock.toUpperCase(), days);
  }

}