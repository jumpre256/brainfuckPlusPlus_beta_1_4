package BrainfuckAss;

public class RuntimeError extends RuntimeException {
  final Operator operator;

  public RuntimeError(Operator operator, String message) {
    super(message);
    this.operator = operator;
  }
}
