package org.galatea.starter.entrypoint.exception;

public class NegativeValueException extends RuntimeException{
    public NegativeValueException(int days) {
      super("Invalid days value given: " + days + ", must be positive");
    }
}
