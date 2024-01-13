import java.io.*;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "AccessStaticViaInstance", "DuplicatedCode", "RedundantSuppression"})
public class RunBrainfuckAss {

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
                //debug(line);
                codeOfFileStrBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {}
        String noWhitespace = stripCodeOfWhitespace(codeOfFileStrBuilder.toString());
        BrainfuckAss._precompile(noWhitespace, "program.bfac");
        //BrainfuckAss.interpret(noWhitespace);
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