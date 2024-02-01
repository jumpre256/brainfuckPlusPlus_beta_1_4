package EDCBA;

import java.io.*;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "AccessStaticViaInstance", "DuplicatedCode", "RedundantSuppression"})
public class Repl {

    private static final Scanner javaScanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 1) {
            runFile(args[0]);
        } else {
            System.err.print("Invalid use of program.");
        }
    }

    public static void runFile(String filePath)
    {
        String codeOfFile = loadFile(filePath);
        String noWhitespace = stripCodeOfWhitespace(codeOfFile);
        Assembler assembler = new Assembler(noWhitespace);
        List<Operator> code = assembler.assemble("program.bfac");
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

    public static void runtimeError(RuntimeError error)
    {
        Operator operator = error.getOperator();
        if (operator.type == OperatorType.EOF) {
            System.err.printf("%nError: [line %d]: at end: %s%n", operator.line, error.getMessage());
        } else {
            System.err.printf("%nError: [line %d]: %s%n", operator.line, error.getMessage());
        }
        //System.exit(-1);  //no need to do this anymore I'm pretty certain.
    }

    private static String loadFile(String filePath)
    {
        File file = new File(filePath);
        StringBuilder codeOfFileStrBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //Tool.debugger.debug("EDCBA", line);
                codeOfFileStrBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {}

        return codeOfFileStrBuilder.toString();
    }
}