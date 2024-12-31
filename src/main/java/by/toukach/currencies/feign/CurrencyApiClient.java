package by.toukach.currencies.feign;

import by.toukach.currencies.dto.RateShort;
import by.toukach.currencies.dto.CurrencyRateDynamicResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "nbrbApiClient", url = "${currency.api.url}")
public interface CurrencyApiClient {

  @GetMapping("/exrates/currencies")
  List<RateShort> getCurrencies();

  @GetMapping("/exrates/rates/dynamics/{cur_id}")
  List<CurrencyRateDynamicResponse> getRateDynamics(
      @PathVariable("cur_id") int curId, // Цифровой код валюты
      @RequestParam("parammode") int paramMode, // Добавляем parammode
      @RequestParam("startDate") String startDate, // Дата начала
      @RequestParam("endDate") String endDate); // Дата окончания
}

