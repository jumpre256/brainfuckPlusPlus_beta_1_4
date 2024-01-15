package BrainfuckAss;

public class AssemblerError extends Exception {

    private final int lineNumber;
    AssemblerError(int lineNumber, String message){
        super(message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {return  lineNumber; }
}
