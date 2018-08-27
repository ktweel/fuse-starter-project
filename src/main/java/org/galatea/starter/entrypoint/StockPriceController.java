package org.galatea.starter.entrypoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockPriceController {

  @RequestMapping("/price")
  public String getStockPrices(@RequestParam(value="stock") String stock,
      @RequestParam(value="days") int days){
    String base = "Stock: %s, Days: %d";
    return String.format(base, stock, days);

  }

}