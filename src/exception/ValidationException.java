package exception;

public class ValidationException extends RuntimeException {
  public ValidationException(String field, String message) {
    super("Validation error in field '" + field + "': " + message);
  }
}