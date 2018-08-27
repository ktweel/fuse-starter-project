package org.galatea.starter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.AlphaVantageReturnMessage;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class StockPriceService {

  public static String getPriceData(String symbol, int days) {
    final String uri = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&outputsize=%s&apikey=PFPOE75HO1WCKW7H";

    ObjectMapper mapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    log.info("calling alpha vantage");
    AlphaVantageReturnMessage result = restTemplate.getForObject(String.format(uri, symbol, "compact"), AlphaVantageReturnMessage.class);

    log.info("alpha vantage called");


    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    try {
        String json = mapper.writeValueAsString(result);
        log.info(json);
        return json;
    } catch (JsonProcessingException e) {
        log.error("unable to convert object to json");
        return "error converting to json: " + e.getMessage();
    }

  }

}
