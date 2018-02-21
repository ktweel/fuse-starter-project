package org.galatea.starter.utils.translation;

public class TranslationException extends RuntimeException{
  public TranslationException() {
  }

  public TranslationException(String message) {
    super(message);
  }

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }

  public TranslationException(Throwable cause) {
    super(cause);
  }
}
