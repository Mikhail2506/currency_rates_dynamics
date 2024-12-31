package by.toukach.currencies.controllers.impl;

import by.toukach.currencies.controllers.CurrencyRateController;
import by.toukach.currencies.dto.RateShort;
import by.toukach.currencies.dto.CurrencyRateDynamicResponse;
import by.toukach.currencies.services.CurrencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyRateControllerImpl implements CurrencyRateController {

  private final CurrencyService currencyService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @GetMapping("/")
  @Override
  public String index(Model model) {
    List<RateShort> currencies = currencyService.getCurrencies();
    model.addAttribute("currencies", currencies);
    return "index";
  }

  @PostMapping("/rates")
  @Override
  public String getRates(
      @RequestParam int curId,
      @RequestParam String startDate,
      @RequestParam String endDate,
      Model model) throws JsonProcessingException {

    List<CurrencyRateDynamicResponse> rates = currencyService.getCurrencyRateDynamics(curId, startDate, endDate);
    objectMapper.registerModule(new JavaTimeModule());
    String ratesJson = objectMapper.writeValueAsString(rates);
    model.addAttribute("rates", ratesJson);
    return "rates";
  }
}
