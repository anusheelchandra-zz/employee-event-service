package employeeservice.exception;

public class UpdateNotPossibleException extends RuntimeException {

  public UpdateNotPossibleException(String message) {
    super(message);
  }
}
