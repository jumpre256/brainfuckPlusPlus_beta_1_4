package brainfuckPlusPlus;

import java.io.*;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "AccessStaticViaInstance", "DuplicatedCode", "RedundantSuppression"})
public class Repl {

    private static final String replLanguageTitle = "Brainfuck++ beta 1.4";
    private static final String sourceFileExtension = ".bfpp";
    private static final String preCompliedFileName = "compiled"; //honestly a slightly bad name since this implementation of (CON'T)
    //[..] brainfuck++ is interpreted not compiled, but if you look inside the "pre-compiled" file you'll understand what this thing is.
    private static final String preCompiledFileExtension = ".bfppc";
    private static final Scanner javaScanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println(replLanguageTitle);
        if (args.length == 1) {
            if(FileHandleTools.fileExists(args[0])) runFile(args[0]);
            else System.err.println("File passed as args[0] does not exist.");
        } else {
            String filePath = userInterface_chooseFile();
            runFile(filePath);
        }
    }

    public static void runFile(String filePath)
    {
        String codeOfFile = loadFile(filePath);
        String noWhitespace = stripCodeOfWhitespace(codeOfFile);
        Assembler assembler = new Assembler(noWhitespace);
        List<Operator> code = assembler.assemble(preCompliedFileName + preCompiledFileExtension);
        if(!assembler.hadError) {
            Interpreter.interpret(code);
        }
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
                //Tool.debugger.debug("brainfuckPlusPlus", line);
                codeOfFileStrBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {}

        return codeOfFileStrBuilder.toString();
    }

    @SuppressWarnings("UnusedAssignment")
    private static String userInterface_chooseFile() //not amazingly-well coded method, but very much acceptable.
    {
        System.out.println("Type \"run\" to run program" + sourceFileExtension + ".");
        System.out.println("Type anything else to attempt to run that file.");
        String userInput;
        boolean hadError = false;
        while(true){
            if(!hadError) System.out.print("> ");
            else System.out.print("\n> ");
            hadError = false;
            userInput = javaScanner.nextLine();
            if(userInput.equals("run")){
                userInput = "program" + sourceFileExtension;
                break;
            } else{
                if(FileHandleTools.fileExists(userInput)) break;
                else {
                    System.err.println("File " + userInput + " does not exist.");
                    hadError = true;
                }
            }
        }
        return userInput;
    }
}