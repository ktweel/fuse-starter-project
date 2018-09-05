package org.galatea.starter.entrypoint.exception;

public class NonPositiveValueException extends RuntimeException{
    public NonPositiveValueException(int days) {
      super("Invalid days value given: " + days + ", must be positive");
    }
}
