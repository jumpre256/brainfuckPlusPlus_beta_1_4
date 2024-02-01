package EDCBA;

public class Operator
{
    final OperatorType type;
    final Object literal;
    final int line;
    final int operatorIndex;

    public Operator(OperatorType type, Object literal, int line, int operatorIndex)
    {
        this.type = type;
        this.literal = literal;
        this.line = line;
        this.operatorIndex = operatorIndex;
    }

    @Override
    public String toString()
    {
        if(literal == null) {
            return type.toString() + ": " + operatorIndex;
        } else {
            return type + ": " + literal + ": " + operatorIndex;
        }
    }
}
