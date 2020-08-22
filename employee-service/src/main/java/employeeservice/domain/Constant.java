package employeeservice.domain;

public class Constant {

  public static final String UUID_REGEX =
      "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

  public static final String DEPARTMENT_REGEX = "^[a-zA-Z']*$";
  public static final String NAME_REGEX = "^[a-zA-Z']*$";
  public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  public static final String DATE_REGEX = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
}
