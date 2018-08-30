package org.galatea.starter.entrypoint;

import org.galatea.starter.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Entrypoint for requests for stock price data
 */
@RestController
public class StockPriceController {

  @Autowired
  StockPriceService service;

  @RequestMapping("/price")
  public String getStockPrices(@RequestParam(value="stock") String stock,
      @RequestParam(value="days") int days) {
    return service.getPriceData(stock, days);

  }

}