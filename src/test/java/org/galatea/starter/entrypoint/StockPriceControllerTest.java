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

  @Test
  public void testGetPrice() throws Exception {
    MvcResult result = mvc.perform(get("/price?stock=MSFT&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage stockDataMessage = mapper.readValue(json, StockDataMessage.class);
    log.info(stockDataMessage.toString());
    assertFalse(json.contains("null"));
    assertEquals(stockDataMessage.getTimeSeriesData().size(), 3);

  }

  @Test
  public void testDatabase() throws Exception {
    MvcResult result = mvc.perform(get("/price?stock=MSFT&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage stockDataMessage = mapper.readValue(json, StockDataMessage.class);
    log.info(stockDataMessage.toString());
    assertFalse(json.contains("null"));
    assertEquals(stockDataMessage.getTimeSeriesData().size(), 3);

  }

  @Test
  public void testAlphaVantageCall() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKN&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    StockDataMessage stockDataMessage = mapper.readValue(json, StockDataMessage.class);
    assertFalse(json.contains("null"));
    assertThat(json).contains("open").contains("close").contains("DNKN");
    assertEquals(stockDataMessage.getTimeSeriesData().size(), 3);

  }

  @Test
  public void testDatabaseDump() throws Exception {

    MvcResult initial = mvc.perform(get("/price?stock=DNKN&days=5")).andExpect(
        status().isOk()).andReturn();

    MvcResult result = mvc.perform(get("/price?stock=DNKN")).andExpect(
        status().isOk()).andReturn();
    String initialJson = initial.getResponse().getContentAsString();
    String json = result.getResponse().getContentAsString();
    StockDataMessage stockDataMessage = mapper.readValue(json, StockDataMessage.class);
    log.info(json);
    assertThat(json).isEqualTo(initialJson);
    assertEquals(stockDataMessage.getTimeSeriesData().size(), 5);

  }

  @Test
  public void testInvalidDays() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKN&days=-5")).andExpect(
        status().isBadRequest()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    assertThat(json).contains("must be greater than or equal to 0");
  }

  @Test
  public void testNoSymbol() throws Exception {

    MvcResult result = mvc.perform(get("/price?days=5")).andExpect(
        status().isBadRequest()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    assertThat(json).contains("Required String parameter 'stock' is not present");
  }

  @Test
  public void testInvalidSymbol() throws Exception {

    MvcResult result = mvc.perform(get("/price?stock=DNKNAAAAAAA&days=5")).andExpect(
        status().is5xxServerError()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
    assertThat(json).contains("Invalid API call");
  }


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

  @After
  public void cleanup() throws Exception{
    repository.deleteByStockSymbol("DNKN");

  }

}