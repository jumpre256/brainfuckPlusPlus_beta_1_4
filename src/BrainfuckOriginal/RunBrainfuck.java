package BrainfuckOriginal;

import java.io.*;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "AccessStaticViaInstance"})
public class RunBrainfuck {

    private static Scanner javaScanner = new Scanner(System.in);
    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    private static final reading_BrainfuckCode brainfuckCore = new reading_BrainfuckCode();

    public static void main(String args[]) {
        if (args.length == 1) {
            runFile(args[0]);
        } else if (args.length == 0) {
            System.out.println("Enter the code:");
            String code = javaScanner.nextLine();
            System.out.println("Output:");
            brainfuckCore.interpret(code);
        }
    }

    public static void runFile(String filePath) {
        File file = new File(filePath);
        StringBuilder codeOfFileStrBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //debug(line);
                //codeOfFileStrBuilder.append(line).append("\n");
                codeOfFileStrBuilder.append(line);
            }
            bufferedReader.close();
        } catch (Exception e) {}
        brainfuckCore.interpret(stripCodeOfWhitespace(codeOfFileStrBuilder.toString()));
    }

    private static String stripCodeOfWhitespace(String input)
    {
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            if(c != ' ' && c != '\n' && c != '\r' && c != '\t'){
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }
}