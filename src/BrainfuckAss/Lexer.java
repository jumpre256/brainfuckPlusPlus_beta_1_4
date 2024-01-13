package BrainfuckAss;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

@SuppressWarnings({"RedundantSuppression", "StatementWithEmptyBody"})
public class Lexer {

    private boolean hadError = false;
    private int operatorIndex = 0;
    private final String source;

    public Lexer(String source){
        this.source = source;
    }

    @SuppressWarnings("UnusedReturnValue")
    public String _precompile(String outputPath)
    {
        //Remove fluff:
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < source.length(); i++){
            char c = source.charAt(i);
            if(isReserved(c)){
                strBuilder.append(c);
            }
        }
        String fluffRemoved = strBuilder.toString();
        //remove comments and newlines:
        String rawCode = "";
        try{
            rawCode = removeCommentsAndNewlines(fluffRemoved);
        } catch(LexerError error) {
            System.err.println("Lexing error: " + error.getMessage());
            hadError = true;
        }
        if(!hadError){
            writeToFile(fluffRemoved, outputPath);  //for debug purposes.
            return rawCode;
        }
        else return "";
    }

    private String removeCommentsAndNewlines(String input) throws LexerError    //not yet implemented
    {
        return "";
    }

    @SuppressWarnings("ConstantValue")
    private String removeCommentsAndNewlines_fail(String input) throws LexerError
    //code for this method is a pretty huge mess, doesn't execute currently, to redo another time, coding (CON'T)
            //[] with a different design philosophy for this method.
    {
        StringBuilder strBuilder = new StringBuilder();
        PrecompileState state = PrecompileState.Code;
        while(true)
        {
            if(!(operatorIndex < input.length())) break;
            char c = input.charAt(operatorIndex);
            Tool.Debugger.debug(this, "Processing: " + c);
            if(state == PrecompileState.Code) {
                if (c == '#' && !lastChar()) {
                    advance();  //consume the # character.
                    if (match('{')) {
                        if (state == PrecompileState.Code) state = PrecompileState.MultilineComment;
                        else
                            throw new LexerError("Cannot initiate a new multiline comment within an existing multiline comment.");
                    }
                /*else if(match('}')) {
                    throw new LexerError("Invalid comment format.");
                }*/
                    else {
                        state = PrecompileState.LineComment;
                    /*if(match('\n')) state = PrecompileState.Code;
                    else advance();*/
                    }
                } else if (c == '}' && !lastChar()) {
                    //Tool.Debugger.debug(this, "found }");
                    advance();
                    if (match('#')) {
                        //Tool.Debugger.debug(this, "found }#");
                        if (state == PrecompileState.MultilineComment) state = PrecompileState.Code;
                        else throw new LexerError("Unmatched multiline comment.");
                    } else {
                        //do nothing
                    }
                }
            }
        }
        return "";
    }

    private char peek()
    {
        return source.charAt(operatorIndex+1);
    }

    private boolean match(char expected)
    {
        boolean returnValue = false;
        if(expected == source.charAt(operatorIndex)){
            operatorIndex++;
            returnValue = true;
        }
        return returnValue;
    }

    private void advance()
    {
        operatorIndex++;
    }

    private boolean lastChar()
    {
        return operatorIndex == (source.length() - 1);
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
                Tool.Debugger.debug("BrainfuckAss.BrainfuckAss", c + ": " + (int)c + "\n", ' ');
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
        System.out.println("Succesfully wrote to file.");
    }

    private enum PrecompileState {
        Code, LineComment, MultilineComment
    }
}
