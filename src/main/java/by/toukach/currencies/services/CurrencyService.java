package by.toukach.currencies.services;

import by.toukach.currencies.dto.RateShort;
import by.toukach.currencies.dto.CurrencyRateDynamicResponse;
import java.util.List;

public interface CurrencyService {

  List<CurrencyRateDynamicResponse> getCurrencyRateDynamics(Integer curId, String startDate,
      String endDate);

  List<RateShort> getCurrencies();
}
