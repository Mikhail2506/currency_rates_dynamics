package by.toukach.currencies.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleInvalidArgumentException(IllegalArgumentException e, Model model) {
    model.addAttribute("error", e.getMessage());
    return "error";
  }

  @ExceptionHandler(CurrencyServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleCurrencyServiceException(CurrencyServiceException e, Model model) {
    model.addAttribute("error", e.getMessage());
    return "error";
  }

  @ExceptionHandler(CurrencyApiException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleCurrencyApiException(CurrencyApiException e, Model model) {
    model.addAttribute("error", e.getMessage());
    return "error";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleException(Exception e, Model model) {
    model.addAttribute("error", e.getMessage());
    return "error";
  }

}
