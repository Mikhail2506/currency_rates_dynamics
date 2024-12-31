package by.toukach.currencies;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import by.toukach.currencies.controllers.impl.CurrencyRateControllerImpl;
import by.toukach.currencies.dto.CurrencyRateDynamicResponse;
import by.toukach.currencies.dto.RateShort;
import by.toukach.currencies.exceptions.CurrencyApiException;
import by.toukach.currencies.exceptions.CurrencyServiceException;
import by.toukach.currencies.services.impl.CurrencyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CurrencyRateControllerImpl.class)
public class CurrencyRateControllerImplTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CurrencyServiceImpl currencyService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void testIndex() throws Exception {

    List<RateShort> currencies = Arrays.asList(
        new RateShort("USD", 1, "USD"),
        new RateShort("EUR", 2, "EUR")
    );

    when(currencyService.getCurrencies()).thenReturn(currencies);

    mockMvc.perform(get("/api/currency/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(model().attribute("currencies", currencies));
  }

  @Test
  void testGetRates_Success() throws Exception {

    int curId = 431;
    String startDate = "2024-12-01T00:00:00";
    String endDate = "2024-12-20T00:00:00";

    List<CurrencyRateDynamicResponse> rates = Arrays.asList(
        new CurrencyRateDynamicResponse(LocalDateTime.parse("2024-12-02T00:00:00"), 3.5701, curId),
        new CurrencyRateDynamicResponse(LocalDateTime.parse("2024-12-03T00:00:00"), 3.5309, curId)
    );

    when(currencyService.getCurrencyRateDynamics(curId, startDate, endDate)).thenReturn(rates);

    mockMvc.perform(post("/api/currency/rates")
            .param("curId", String.valueOf(curId))
            .param("startDate", startDate)
            .param("endDate", endDate)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isOk())
        .andExpect(view().name("rates"))
        .andExpect(model().attribute("rates", objectMapper.writeValueAsString(rates)));

  }

  @Test
  void testGetRates_CurrencyApiException() throws Exception {

    when(currencyService.getCurrencies()).thenReturn(Arrays.asList(
        new RateShort("USD", 1, "USD"),
        new RateShort("EUR", 2, "EUR")
    ));
    when(currencyService.getCurrencyRateDynamics(1, "2024-12-01T00:00:00", "2024-12-20T00:00:00"))
        .thenThrow(new CurrencyApiException("Ошибка при запросе к API"));

    mockMvc.perform(post("/api/currency/rates")
            .param("curId", "1")
            .param("startDate", "2024-12-01T00:00:00")
            .param("endDate", "2024-12-20T00:00:00")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isInternalServerError())
        .andExpect(view().name("error"))
        .andExpect(
            model().attribute("error", "Ошибка при запросе к API"));
  }

  @Test
  void testGetRates_CurrencyServiceException() throws Exception {

    when(currencyService.getCurrencies()).thenReturn(Arrays.asList(
        new RateShort("USD", 1, "USD"),
        new RateShort("EUR", 2, "EUR")
    ));
    when(currencyService.getCurrencyRateDynamics(1, "2024-12-01T00:00:00", "2024-12-20T00:00:00"))
        .thenThrow(new CurrencyServiceException("Ошибка сервиса валют"));

    mockMvc.perform(post("/api/currency/rates")
            .param("curId", "1")
            .param("startDate", "2024-12-01T00:00:00")
            .param("endDate", "2024-12-20T00:00:00")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isInternalServerError())
        .andExpect(view().name("error"))
        .andExpect(
            model().attribute("error", "Ошибка сервиса валют"));
  }
}
