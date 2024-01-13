package BrainfuckAss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.List; import java.util.ArrayList;

@SuppressWarnings({"RedundantSuppression", "StatementWithEmptyBody"})
public class Lexer {

    private boolean hadError = false;
    private int lineNumber = 1;
    private int current = 0;
    private int operatorIndex = 0;
    private final String source;
    @SuppressWarnings("FieldMayBeFinal")
    private List<Operator> operators = new ArrayList<>();

    public Lexer(String source) {
        this.source = source;
    }

    @SuppressWarnings("UnusedReturnValue")
    public List<Operator> assemble(String outputPath) {
        //Remove fluff:
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (isReserved(c)) {
                strBuilder.append(c);
            }
        }
        String fluffRemoved = strBuilder.toString();
        //convert to a list of operators:
        List<Operator> operators = null;
        try {
            operators = doLexMain(fluffRemoved);
            operators.add(new Operator(OperatorType.EOF, null, lineNumber, operatorIndex));
        } catch (LexerError error) {
            System.err.printf("Error: [line %d]: %s%n", error.getLineNumber(), error.getMessage());
            hadError = true;
        }


        if (!hadError) {
            writeToFile(operators, outputPath);  //for debug purposes.
            return operators;
        } else return null;
    }

    @SuppressWarnings("ConstantValue")
    private List<Operator> doLexMain(String input) throws LexerError
    {
        while (!isAtEnd()) {
            char c = advance();


            switch(c) {
                case '[':
                    addOperator(OperatorType.LOOP_OPEN); break;

                case ']':
                    addOperator(OperatorType.LOOP_CLOSE); break;
                case '+':
                    addOperator(OperatorType.ADD); break;
                case '-':
                    addOperator(OperatorType.MINUS); break;
                case '.':
                    addOperator(OperatorType.DOT); break;
                case ',':
                    addOperator(OperatorType.COMMA); break;
                case '>':
                    addOperator(OperatorType.RIGHT); break;
                case '<':
                    addOperator(OperatorType.LEFT); break;
                case ':':
                    set_locator(); break;
                case ';':
                    method_call(); break;
                case '|':
                    bra(); break;
                case '*':
                case '^':
                case '$':
                    addOperator(OperatorType.RET); break;
                case '@':
                case '"':
                case '!':
                    addOperator(OperatorType.OTHER); break;
                case '#':
                    hash(); break;
                case '\n':
                    lineNumber++; break;
                case '{':
                case '}':
                    break;
                default:
                    boolean isReserved = ((c >= 48) && (c <= 57)) || ((c >= 97) && (c <= 122));
                    if(isReserved) addOperator(OperatorType.OTHER);
                    break;
            }

        }
        return operators;
    }

    private String _removeCommentsAndWhitespace(String input) throws LexerError
    {
        StringBuilder strBuilder = new StringBuilder();
        while (!isAtEnd()) {
            char c = advance();
            boolean isReserved = isReserved(c);
            if (isReserved) {
                if (c == '#') hash();
                else if (c == '}') throw new LexerError(lineNumber,"Unexpected '}' character.");
                else if (c == '\n') {
                    lineNumber++;
                } else {
                    strBuilder.append(c);
                }
            }
        }
        return strBuilder.toString();
    }


    private void set_locator() throws LexerError
    {
        char nextChar = advance();
        boolean isValidLocator = (nextChar >= 97) && (nextChar <= 122);
        if(isValidLocator){
            addOperator(OperatorType.SET_LOCATOR, nextChar);
        } else if(nextChar == '\0') {
            //do nothing.
         } else {
            throw new LexerError(lineNumber, "a ':' character must be followed by an 'a' to 'z' character.");
         }
    }

    private void method_call()
    {

    }

    private void bra() throws LexerError
    {
        char nextChar = advance();
        boolean isValidLocator = (nextChar >= 97) && (nextChar <= 122);
        if(isValidLocator){
            addOperator(OperatorType.BRA, nextChar);
        } else if(nextChar == '\0') {
            //do nothing.
        } else {
            throw new LexerError(lineNumber, "a '|' character must be followed by an 'a' to 'z' character.");
        }
    }

    private void hash() throws LexerError   //handle streams of commented out text initiated with a hashtag character.
            //inspiration for code's structure from Robert Nystrom.
    {
        if(match('{')){
            //in a multiline comment.
            while(true){
                if(peek() == '\n') lineNumber++;
                else if(peek() == '#' && peek(2) == '{') throw new LexerError(lineNumber,"Cannot initiate a new multiline comment within an existing multiline comment.");

                else if(peek() == '}' && peek(2) == '#'){
                    advance(); advance();
                    break;      //break out of while loop.
                }

                if(peek() == '\0')    //this has to be an "if" not an "else if" to make sure if false, runs advance().
                {
                    throw new LexerError(lineNumber,"Unterminated multiline comment.");
                } else {
                    advance(); //eats up characters in the source code stream while appropriately updating the "operatorIndex" variable.
                }
            }
        } else if(peek() == '\0') {
            //do nothing.
        } else {
            //a comment goes until the end of the line.
            while (peek() != '\n' && !isAtEnd()) advance();
        }
    }

    private char peek()
    {
        if (isAtEnd()) return '\0';
        else return source.charAt(current);
    }

    private char peek(int n)    //code credit: Robert Nystrom.
    {
        int current = this.current;
        int i = n - 1;
        if (current + i >= source.length()) return '\0';
        else return source.charAt(current + i);
    }

    //@SuppressWarnings("DataFlowIssue")
    private boolean match(char expected)
    {
        boolean returnValue;
        if(isAtEnd()) returnValue = false;
        else if(expected == source.charAt(current)){
            current++;
            returnValue = true;
        } else{
            returnValue = false;
        }
        return returnValue;
    }

    private char advance()
    {
        int beforeIncrement = current;
        current++;
        return source.charAt(beforeIncrement);
    }


    private boolean isAtEnd()
    {
        return current >= source.length();
    }

    private void addOperator(OperatorType type)
    {
        addOperator(type, null);
    }

    private void addOperator(OperatorType type, Object literal)
    {
        operators.add(new Operator(type, literal, lineNumber, operatorIndex));
        operatorIndex++;
    }

    private boolean isReserved(char c)
    {
        boolean returnValue;
        switch(c){
            case '[':
            case ']':
            case '+':
            case '-':
            case '.':
            case ',':
            case ':':
            case ';':
            case '|':
            case '>':
            case '<':
            case '*':
            case '^':
            case '@': //14th reserved character
            case '"':
            case '!':
            case '#':
            case '\n':
            case '{':
            case '}':
                returnValue = true; break;
            default:
                returnValue = ((c >= 48) && (c <= 57)) || ((c >= 97) && (c <= 122));
                //Tool.Debugger.debug(this, c + ": " + (int)c + "\n", ' ');
                break;

        }
        return returnValue;
    }

    @SuppressWarnings({"TryWithIdenticalCatches", "ResultOfMethodCallIgnored"})
    private void writeToFile(List<Operator> operators, String filePath) {
        boolean success = false;
        while (!success) {
            File file = new File(filePath);
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                if(operators != null){
                    for(Operator op : operators){
                        bufferedWriter.write(op.toString() + "\n");
                    }
                }
                bufferedWriter.close();
                success = true;
            } catch (FileNotFoundException e) {
                try {
                    file.createNewFile();
                } catch(Exception e1) {}
            } catch(Exception e0) {}
        }
        //Tool.Debugger.debug(this, "Succesfully wrote to file.");
    }

}
