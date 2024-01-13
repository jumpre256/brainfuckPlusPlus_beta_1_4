package BrainfuckAss;

import java.io.*;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "AccessStaticViaInstance", "DuplicatedCode", "RedundantSuppression"})
public class BrainfuckAss {

    private static final Scanner javaScanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 1) {
            runFile(args[0]);
        } else {
            System.err.print("Invalid use of program.");
        }
    }

    public static void runFile(String filePath) {
        File file = new File(filePath);
        StringBuilder codeOfFileStrBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //Tool.debugger.debug("BrainfuckAss", line);
                codeOfFileStrBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {}
        String noWhitespace = stripCodeOfWhitespace(codeOfFileStrBuilder.toString());
        Lexer lexer = new Lexer(noWhitespace);
        List<Operator> code = lexer.assemble("program.bfac");
        Interpreter.interpret(code);
    }

    private static String stripCodeOfWhitespace(String input)
    {
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            if(c != ' ' && c != '\r' && c != '\t'){
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }
}