package org.galatea.starter.entrypoint;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.opengamma.strata.basics.date.HolidayCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.AlphaVantageServer;
import org.galatea.starter.AppConfig;
import org.galatea.starter.TestAppConfig;
import org.galatea.starter.domain.StockDataMessage;
import org.galatea.starter.domain.rpsy.StockDataRepository;
import org.galatea.starter.service.AlphaVantageService;
import org.galatea.starter.service.DatabaseService;
import org.galatea.starter.service.StockPriceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class StockPriceControllerTest {

  @Autowired
  private WebApplicationContext context;
  private MockMvc mvc;
  @Autowired
  StockDataRepository repository;
  @Autowired
  StockPriceService stockPriceService;
  @Autowired
  DatabaseService databaseService;
  @Autowired
  AlphaVantageService alphaVantageService;
  @Autowired
  StockPriceController stockPriceController;
  @Autowired
  AlphaVantageServer alphaVantageServer;
  @Autowired
  HolidayCalendar holidayCalendar;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  public void testGetPrice() throws Exception {
//    when(stockPriceService.getPriceData("MSFT", 3)).thenReturn(new StockDataMessage());
    MvcResult result = mvc.perform(get("/price?stock=MSFT&days=3")).andExpect(
        status().isOk()).andReturn();

    String json = result.getResponse().getContentAsString();
    log.info(json);
  }
}
