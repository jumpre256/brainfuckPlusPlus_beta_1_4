import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

@SuppressWarnings({"CatchMayIgnoreException", "RedundantSuppression"})
public class BrainfuckAss {

    public static void interpret(String input) {}


    @SuppressWarnings("UnusedReturnValue")
    public static String _precompile(String input, String outputPath)
    {
        //Remove fluff:
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            if(isReserved(c)){
                strBuilder.append(c);
            }
        }
        String fluffRemoved = strBuilder.toString();
        //remove comments and newlines:
        String rawCode = removeCommentsAndNewlines(fluffRemoved);
        writeToFile(fluffRemoved, outputPath);  //for debug purposes.
        return rawCode;
    }

    private static String removeCommentsAndNewlines(String input)
    {
        BrainfuckAss.PrecompileState state = BrainfuckAss.PrecompileState.Code;
        int operatorIndex = 0;
        while(true)
        {
            if(!(operatorIndex < input.length())) break;

            operatorIndex++;
        }
        return "";
    }

    private static boolean isReserved(char c)
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
                //Debugger.debug("BrainfuckAss", c + ": " + (int)c + "\n", ' ');
                break;

        }

        return returnValue;
    }

    @SuppressWarnings({"TryWithIdenticalCatches", "ResultOfMethodCallIgnored"})
    private static void writeToFile(String input, String filePath) {
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
        System.out.println("Successfully precompiled code.");
    }

    private enum PrecompileState {
        Code, LineComment, MultilineComment
    }
}
