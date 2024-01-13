package BrainfuckAss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

@SuppressWarnings({"RedundantSuppression", "StatementWithEmptyBody"})
public class Lexer {

    private boolean hadError = false;
    private int lineNumber = 1;
    private int operatorIndex = 0;
    private final String source;

    public Lexer(String source) {
        this.source = source;
    }

    @SuppressWarnings("UnusedReturnValue")
    public String _precompile(String outputPath) {
        //Remove fluff:
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (isReserved(c)) {
                strBuilder.append(c);
            }
        }
        String fluffRemoved = strBuilder.toString();
        //remove comments and newlines:
        String rawCode = "";
        try {
            rawCode = removeCommentsAndNewlines(fluffRemoved);
        } catch (LexerError error) {
            System.err.printf("Error: [line %d]: %s%n", error.getLineNumber(), error.getMessage());
            hadError = true;
        }
        if (!hadError) {
            writeToFile(rawCode, outputPath);  //for debug purposes.
            return rawCode;
        } else return "";
    }

    private String removeCommentsAndNewlines(String input) throws LexerError    //not yet implemented
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
        else return source.charAt(operatorIndex);
    }

    private char peek(int n)    //code credit: Robert Nystrom.
    {
        int current = operatorIndex;
        int i = n - 1;
        if (current + i >= source.length()) return '\0';
        else return source.charAt(current + i);
    }

    //@SuppressWarnings("DataFlowIssue")
    private boolean match(char expected)
    {
        boolean returnValue;
        if(isAtEnd()) returnValue = false;
        else if(expected == source.charAt(operatorIndex)){
            operatorIndex++;
            returnValue = true;
        } else{
            returnValue = false;
        }
        return returnValue;
    }

    private char advance()
    {
        int beforeIncrement = operatorIndex;
        operatorIndex++;
        return source.charAt(beforeIncrement);
    }


    private boolean isAtEnd()
    {
        return operatorIndex >= source.length();
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
    private void writeToFile(String input, String filePath) {
        boolean success = false;
        while (!success) {
            File file = new File(filePath);
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write(input);
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
