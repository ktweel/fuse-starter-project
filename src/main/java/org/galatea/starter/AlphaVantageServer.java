package org.galatea.starter;

import feign.Param;
import feign.RequestLine;
import org.galatea.starter.domain.AlphaVantageReturnMessage;

public interface AlphaVantageServer {

  //TODO: put query string in application.yml

  @RequestLine("GET /query?function=TIME_SERIES_DAILY&symbol={symbol}&outputsize={size}&"
      + "apikey={apikey}")
  AlphaVantageReturnMessage alphaVantageApiCall(@Param("symbol") String symbol,
      @Param("size") String outputSize, @Param("apikey") String apikey);
}
