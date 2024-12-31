package by.toukach.currencies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.toukach.currencies.dto.CurrencyRateDynamicResponse;
import by.toukach.currencies.dto.RateShort;
import by.toukach.currencies.exceptions.CurrencyApiException;
import by.toukach.currencies.exceptions.CurrencyServiceException;
import by.toukach.currencies.feign.CurrencyApiClient;
import by.toukach.currencies.services.impl.CurrencyServiceImpl;
import feign.FeignException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CurrencyServiceImplTest {

  @Mock
  private CurrencyApiClient currencyApiClient;

  @InjectMocks
  private CurrencyServiceImpl currencyService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetCurrencies_Success() {

    List<RateShort> expectedCurrencies = Arrays.asList(
        new RateShort("Доллар", 1, "USD"),
        new RateShort("Евро", 2, "EUR")
    );
    when(currencyApiClient.getCurrencies()).thenReturn(expectedCurrencies);

    List<RateShort> actualCurrencies = currencyService.getCurrencies();

    assertEquals(expectedCurrencies, actualCurrencies);
    verify(currencyApiClient, times(1)).getCurrencies();
  }

  @Test
  void testGetCurrencies_EmptyList() {

    when(currencyApiClient.getCurrencies()).thenReturn(Collections.emptyList());

    CurrencyServiceException exception = assertThrows(CurrencyServiceException.class, () -> {
      currencyService.getCurrencies();
    });
    assertEquals("Перечень валют не найден.", exception.getMessage());

    verify(currencyApiClient, times(1)).getCurrencies();
  }

  @Test
  void testGetCurrencies_NullList() {

    when(currencyApiClient.getCurrencies()).thenReturn(null);

    CurrencyServiceException exception = assertThrows(CurrencyServiceException.class, () -> {
      currencyService.getCurrencies();
    });
    assertEquals("Перечень валют не найден.", exception.getMessage());

    verify(currencyApiClient, times(1)).getCurrencies();
  }

  @Test
  void testGetCurrencies_FeignException() {

    FeignException feignException = mock(FeignException.class);
    when(feignException.status()).thenReturn(404);
    when(feignException.getMessage()).thenReturn("Not Found");
    when(currencyApiClient.getCurrencies()).thenThrow(feignException);

    CurrencyApiException exception = assertThrows(CurrencyApiException.class, () -> {
      currencyService.getCurrencies();
    });
    assertEquals("Ошибка получения перечня валют по API банка", exception.getMessage());

    verify(currencyApiClient, times(1)).getCurrencies();
  }

  @Test
  void testGetCurrencyRateDynamics_Success() {

    int curId = 1;
    String startDate = "2023-01-01";
    String endDate = "2023-01-31";
    List<CurrencyRateDynamicResponse> expectedDynamics = List.of(
        new CurrencyRateDynamicResponse(LocalDateTime.now(), 1.23, curId)
    );
    when(currencyApiClient.getRateDynamics(curId, 0, startDate, endDate)).thenReturn(
        expectedDynamics);

    List<CurrencyRateDynamicResponse> actualDynamics = currencyService.getCurrencyRateDynamics(
        curId, startDate, endDate);

    assertEquals(expectedDynamics, actualDynamics);
    verify(currencyApiClient, times(1)).getRateDynamics(curId, 0, startDate, endDate);
  }

  @Test
  void testGetCurrencyRateDynamics_InvalidDates() {

    int curId = 1;
    String startDate = "2023-01-31";
    String endDate = "2023-01-01";

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      currencyService.getCurrencyRateDynamics(curId, startDate, endDate);
    });
    assertEquals("Начальная дата должна быть меньше или равна конечной дате.",
        exception.getMessage());

    verify(currencyApiClient, never()).getRateDynamics(anyInt(), anyInt(), anyString(),
        anyString());
  }

  @Test
  void testGetCurrencyRateDynamics_EmptyList() {

    int curId = 1;
    String startDate = "2023-01-01";
    String endDate = "2023-01-31";
    when(currencyApiClient.getRateDynamics(curId, 0, startDate, endDate)).thenReturn(
        Collections.emptyList());

    CurrencyApiException exception = assertThrows(CurrencyApiException.class, () -> {
      currencyService.getCurrencyRateDynamics(curId, startDate, endDate);
    });
    assertEquals(
        "Не удалось получить данные: Данные для указанной валюты и диапазона дат не найдены.",
        exception.getMessage());

    verify(currencyApiClient, times(1)).getRateDynamics(curId, 0, startDate, endDate);
  }

  @Test
  void testGetCurrencyRateDynamics_FeignException() {

    int curId = 1;
    String startDate = "2023-01-01";
    String endDate = "2023-01-31";
    FeignException feignException = mock(FeignException.class);
    when(feignException.getMessage()).thenReturn("Service Unavailable");
    when(currencyApiClient.getRateDynamics(curId, 0, startDate, endDate)).thenThrow(feignException);

    CurrencyApiException exception = assertThrows(CurrencyApiException.class, () -> {
      currencyService.getCurrencyRateDynamics(curId, startDate, endDate);
    });
    assertEquals("Ошибка при запросе к API: Service Unavailable", exception.getMessage());

    verify(currencyApiClient, times(1)).getRateDynamics(curId, 0, startDate, endDate);
  }

  @Test
  void testGetCurrencyRateDynamics_GeneralException() {

    int curId = 1;
    String startDate = "2023-01-01";
    String endDate = "2023-01-31";
    RuntimeException exception = new RuntimeException("Internal Server Error");
    when(currencyApiClient.getRateDynamics(curId, 0, startDate, endDate)).thenThrow(exception);

    CurrencyApiException apiException = assertThrows(CurrencyApiException.class, () -> {
      currencyService.getCurrencyRateDynamics(curId, startDate, endDate);
    });
    assertEquals("Не удалось получить данные: Internal Server Error", apiException.getMessage());

    verify(currencyApiClient, times(1)).getRateDynamics(curId, 0, startDate, endDate);
  }
}
