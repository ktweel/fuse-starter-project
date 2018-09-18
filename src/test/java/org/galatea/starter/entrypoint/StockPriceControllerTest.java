package org.galatea.starter.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@Slf4j
@SpringBootTest
public class StockPriceControllerTest extends ASpringTest {

  @Autowired
  private WebApplicationContext context;
  private MockMvc mvc;

  @Autowired
  StockDataRepository repository;
  @Autowired
  ObjectMapper mapper;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  /**
   * Basic endpoint test
   * @throws Exception from MockMvc
   */
  @Test
  public void testGetPrice() throws Exception {
    MvcResult result = mvc.perform(get("/price?stock=MSFT&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage[] stockDataMessage = mapper.readValue(json, StockDataMessage[].class);
    log.info(stockDataMessage[0].toString());
    assertFalse(json.contains("null"));
    assertEquals(stockDataMessage[0].getTimeSeriesData().size(), 3);

  }

  /**
   * test call in which data should be in database
   * @throws Exception from MockMvc
   */
  @Test
  public void testDatabase() throws Exception {
    MvcResult result = mvc.perform(get("/price?stock=MSFT&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage[] stockDataMessage = mapper.readValue(json, StockDataMessage[].class);
    log.info(stockDataMessage[0].toString());
    assertFalse(json.contains("null"));
    assertEquals(stockDataMessage[0].getTimeSeriesData().size(), 3);

  }

  /**
   * test call in which all data retrieved from alpha vantage
   * @throws Exception from MockMvc
   */
  @Test
  public void testAlphaVantageCall() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKN&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage[] stockDataMessage = mapper.readValue(json, StockDataMessage[].class);
    assertFalse(json.contains("null"));
    assertThat(json).contains("open").contains("close").contains("DNKN");
    assertEquals(stockDataMessage[0].getTimeSeriesData().size(), 3);

  }

  /**
   * test call with multiple stock symbols
   * @throws Exception from MockMvc
   */
  @Test
  public void testMultipleStocksymbols() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=MSFT,IBM&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage[] stockDataMessage = mapper.readValue(json, StockDataMessage[].class);
    assertThat(stockDataMessage.length).isEqualTo(2);
    assertFalse(json.contains("null"));
    assertThat(json).contains("open").contains("close").contains("MSFT");
    assertEquals(stockDataMessage[0].getTimeSeriesData().size(), 3);
    assertEquals(stockDataMessage[1].getTimeSeriesData().size(), 3);

  }

  /**
   * test database dump
   * @throws Exception from MockMvc
   */
  @Test
  public void testDatabaseDump() throws Exception {

    MvcResult initial = mvc.perform(get("/price?stock=DNKN&days=5")).andExpect(
        status().isOk()).andReturn();

    MvcResult result = mvc.perform(get("/price?stock=DNKN")).andExpect(
        status().isOk()).andReturn();

    String initialJson = initial.getResponse().getContentAsString();
    String json = result.getResponse().getContentAsString();

    StockDataMessage[] stockDataMessage = mapper.readValue(json, StockDataMessage[].class);
    log.info(json);
    assertThat(json).isEqualTo(initialJson);
    assertEquals(stockDataMessage[0].getTimeSeriesData().size(), 5);

  }

  /**
   * test negative days value
   * @throws Exception from MockMvc
   */
  @Test
  public void testInvalidDays() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKN&days=-5")).andExpect(
        status().isBadRequest()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    assertThat(json).contains("must be greater than or equal to 0");
  }

  /**
   * test no symbol given
   * @throws Exception from MockMvc
   */
  @Test
  public void testNoSymbol() throws Exception {

    MvcResult result = mvc.perform(get("/price?days=5")).andExpect(
        status().isBadRequest()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    assertThat(json).contains("Required List parameter 'stock' is not present");
  }

  /**
   * test invalid stock symbol
   * @throws Exception from MockMvc
   */
  @Test
  public void testInvalidSymbol() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKNAAAAAAA&days=5")).andExpect(
        status().isBadRequest()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    assertThat(json).contains("Invalid API call");
  }

  /**
   * test call in which some data retrieved from database and some data
   * retrieved from alpha vantage
   * @throws Exception if error in MockMvc.perform()
   */
  @Test
  public void testAlphaVantageAndDB() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKN&days=3")).andExpect(
        status().isOk()).andReturn();

    MvcResult resultdb = mvc.perform(get("/price?stock=DNKN&days=5")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    String jsondb = resultdb.getResponse().getContentAsString();
    log.info(json);
    log.info(jsondb);

  }

  /**
   * remove database entries to reset tests and clear cache
   * @throws Exception if error in MockMvc.perform()
   */
  @After
  public void cleanup() throws Exception{
    repository.deleteByStockSymbol("DNKN");
    mvc.perform(get("/clearCache")).andReturn();

  }

}
