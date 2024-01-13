package BrainfuckAss;

public class LexerError extends Exception {

    private final int lineNumber;
    LexerError(int lineNumber, String message){
        super(message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {return  lineNumber; }
}
