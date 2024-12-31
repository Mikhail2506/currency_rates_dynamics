package by.toukach.currencies.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface CurrencyRateController {

  @GetMapping("/")
  String index(Model model);

  @PostMapping("/rates")
  String getRates(
      @RequestParam int curId,
      @RequestParam String startDate,
      @RequestParam String endDate,
      Model model) throws JsonProcessingException;
}
