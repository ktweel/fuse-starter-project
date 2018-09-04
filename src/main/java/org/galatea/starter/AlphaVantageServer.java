package org.galatea.starter;

import feign.Param;
import feign.RequestLine;
import org.galatea.starter.domain.AlphaVantageReturnMessage;

public interface AlphaVantageServer {

  @RequestLine("GET /query?function=TIME_SERIES_DAILY&symbol={symbol}&outputsize={size}&apikey=PFPOE75HO1WCKW7H")
  AlphaVantageReturnMessage alphaVantageApiCall(@Param("symbol") String symbol, @Param("size") String outputSize);
}
