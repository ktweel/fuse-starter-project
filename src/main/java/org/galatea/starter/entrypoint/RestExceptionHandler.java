package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.entrypoint.exception.NonPositiveValueException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A centralized REST handler that intercepts exceptions thrown by controller calls, enabling a
 * custom response to be returned.
 *
 * The returned ResponseEntity body object will be serialised into JSON (hence the need for the
 * ApiError wrapper class).
 */
@ControllerAdvice
@Slf4j
public class RestExceptionHandler {



  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException exception) {
    String errorMessage = "Incorrectly formatted message.  Please consult the documentation.";
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, errorMessage);
    return buildResponseEntity(error);
  }

  @ExceptionHandler(DataAccessException.class)
  protected ResponseEntity<Object> handleDataAccessException(final DataAccessException exception) {
    log.error("Unexpected data access error", exception);

    String errorMessage = "An internal application error occurred.";
    ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    return buildResponseEntity(error);
  }


  @ExceptionHandler(JsonProcessingException.class)
  protected ResponseEntity<Object> handleJsonProcessingException(
      final JsonProcessingException exception) {
    log.debug("Error converting to JSON", exception);
    ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exception.toString());
    return buildResponseEntity(error);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  protected ResponseEntity<Object> handleMissingServletRequestParameterException(
      final MissingServletRequestParameterException exception) {
    log.error("Missing required parameter", exception);
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, exception.toString());
    return buildResponseEntity(error);
  }

  @ExceptionHandler(NonPositiveValueException.class)
  protected ResponseEntity<Object> handleNonPositiveValueException (
      final NonPositiveValueException exception) {
    log.error("negative or zero value given for days parameter", exception);
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, exception.toString());
    return buildResponseEntity(error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolationException (
      final ConstraintViolationException exception) {
    log.error("constraint violation exception: ", exception);
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, exception.toString());
    return buildResponseEntity(error);
  }




  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

}
