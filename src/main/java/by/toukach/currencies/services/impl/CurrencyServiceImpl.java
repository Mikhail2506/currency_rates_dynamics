package by.toukach.currencies.services.impl;

import by.toukach.currencies.dto.RateShort;
import by.toukach.currencies.dto.CurrencyRateDynamicResponse;
import by.toukach.currencies.exceptions.CurrencyApiException;
import by.toukach.currencies.exceptions.CurrencyServiceException;
import by.toukach.currencies.feign.CurrencyApiClient;
import by.toukach.currencies.services.CurrencyService;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

  private final CurrencyApiClient currencyApiClient;

  @Override
  public List<CurrencyRateDynamicResponse> getCurrencyRateDynamics(Integer curId, String startDate,
      String endDate) {

    if (startDate.compareTo(endDate) > 0) {
      throw new IllegalArgumentException("Начальная дата должна быть меньше или равна конечной дате.");
    }

    int parammode = 0;

    log.info("Получение динамики курса валюты с  ID: {}, начальная дата : {}, конечная дата: {}",
        curId,
        startDate, endDate);

    try {
      List<CurrencyRateDynamicResponse> dynamics = currencyApiClient.getRateDynamics(curId,
          parammode,
          startDate, endDate);

      if (dynamics.isEmpty()) {
        throw new CurrencyApiException("Данные для указанной валюты и диапазона дат не найдены.");
      }
      return dynamics;
    } catch (FeignException e) {
      throw new CurrencyApiException("Ошибка при запросе к API: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new CurrencyApiException("Не удалось получить данные: " + e.getMessage(), e);
    }
  }

  @Override
  public List<RateShort> getCurrencies() {
    try {
      log.info("Получение данных по API...");
      List<RateShort> currencies = currencyApiClient.getCurrencies();

      if (currencies == null || currencies.isEmpty()) {
        log.warn("Не найден перечень валют.");
        throw new CurrencyServiceException("Перечень валют не найден.");
      }
      log.info("Перечень валют  получен {}", currencies.size());
      return currencies;
    } catch (FeignException e) {
      log.error("Ошибка получения перечня валют по API банка. Статус: {}, Сообщение: {}",
          e.status(),
          e.getMessage());
      throw new CurrencyApiException("Ошибка получения перечня валют по API банка", e);
    }
  }
}