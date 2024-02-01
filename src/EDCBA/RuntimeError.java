package EDCBA;

public class RuntimeError extends RuntimeException {
  private final Operator operator;

  public RuntimeError(Operator operator, String message) {
    super(message);
    this.operator = operator;
  }

  public Operator getOperator() {
    return operator;
  }
}
