package EDCBA;

public class Locator {

    public int literal;
    public int operatorIndex;
    public OperatorType type;
    public int sectionCode;

    public Locator(int _literal, int _index, OperatorType _type, int _sectionCode)
    {
        literal = _literal;
        operatorIndex = _index;
        type = _type;
        sectionCode = _sectionCode;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public String toString() {
        String selfValue = "{" +
                "literal: " + literal + ", " +
                "operatorIndex: " + operatorIndex + ", " +
                "type: " + type + ", " +
                "sectionCode: " + sectionCode + "}";
        return selfValue;
    }
}
