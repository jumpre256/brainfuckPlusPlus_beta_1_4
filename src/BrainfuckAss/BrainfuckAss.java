package BrainfuckAss;

import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

@SuppressWarnings({"CatchMayIgnoreException", "RedundantSuppression"})
public class BrainfuckAss   //Interpreter to the brainfuckAss language.
{
    public static void interpret(String input) {}

}


/*private String removeCommentsAndNewlines(String input) throws LexerError
    {
        PrecompileState state = PrecompileState.Code;
        while(true)
        {
            if(!(operatorIndex < input.length())) break;
            char c = input.charAt(operatorIndex);
            if(c == '#' && lastChar(input, operatorIndex)) {
                state = PrecompileState.LineComment;
            } else if(c == '#' && input.charAt(operatorIndex + 1) != '{' && input.charAt(operatorIndex + 1) != '}'){
                state = PrecompileState.LineComment;
            } else if(c == '#' && input.charAt(operatorIndex + 1) == '{' && state == PrecompileState.Code){
                state = PrecompileState.MultilineComment;
            } else if(c == '#' && input.charAt(operatorIndex + 1) == '{' && state != PrecompileState.Code) {
                throw new LexerError("Cannot initiate a new multiline comment within an existing multiline comment.");
            }

            if(c == '}' && input.charAt(operatorIndex + 1) != '{') {}

            operatorIndex++;
        }
        return "";
    }*/