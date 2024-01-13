package BrainfuckAss;

public class Operator
{
    final OperatorType type;
    final Object literal;
    final int line;

    public Operator(OperatorType type, Object literal, int line)
    {
        this.type = type;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString()
    {
        if(literal == null) {
            return type.toString();
        } else {
            return type + ": " + literal.toString();
        }
    }
}
