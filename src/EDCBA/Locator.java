package EDCBA;

public class Locator {

    public int literal;
    public int operatorIndex;
    public OperatorType type;

    public Locator(int _literal, int _index, OperatorType _type)
    {
        literal = _literal;
        operatorIndex = _index;
        type = _type;
    }
}
