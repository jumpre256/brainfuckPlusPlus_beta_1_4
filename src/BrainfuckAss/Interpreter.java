package BrainfuckAss;

import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "RedundantSuppression"})
public class Interpreter   //Interpreter to the brainfuckAss language.
{
    private static Scanner ob = new Scanner(System.in);
    private static int ptr; // Data pointer
    private static int length = 65535;
    // Array of byte type simulating memory of max 65535 bits. Goes from 0 to 65534.
    private static byte memory[] = new byte[length];
    private static int loopDepth = 0;
    private static int operatorIndex = 0; // Parsing through each character of the code

    public static void interpret(String input) {
        fullInterpret(input);
        //lazyInterpret(input);
    }

    private static void lazyInterpret(String input) {
        BrainfuckOriginal.BrainfuckEngine.interpret(input);
    }

    public static void fullInterpret(String input) {
        //for (int i = 0; i < s.length(); i++)
        while (true) {
            if (!(operatorIndex < input.length())) break;
            // BrainFuck is a tiny language with only
            // eight instructions. In this loop we check
            // and execute all those eight instructions

            /*Tool.Debugger.debug("Interpreter",
                    "Executing: " + input.charAt(operatorIndex));
            Tool.Debugger.debug("Interpreter",
                    "LOOPTOP:\n\t" + "loopDepth: " + loopDepth + "\n"
                    + "\t" + "operatorIndex: " + operatorIndex);
            Tool.Debugger.debug("Interpreter",
                    "LOOPBOTTOM:");*/

            // > moves the pointer to the right
            if (input.charAt(operatorIndex) == '>') {
                if (ptr == length - 1)//If memory is full
                    ptr = 0;//pointer is returned to zero
                else
                    ptr++;
            }

            // < moves the pointer to the left
            else if (input.charAt(operatorIndex) == '<') {
                if (ptr == 0) // If the pointer reaches zero

                    // pointer is returned to rightmost memory
                    // position
                    ptr = length - 1;
                else
                    ptr--;
            }

            // + increments the value of the memory
            // cell under the pointer
            else if (input.charAt(operatorIndex) == '+')
                memory[ptr]++;

                // - decrements the value of the memory cell
                // under the pointer
            else if (input.charAt(operatorIndex) == '-')
                memory[ptr]--;

                // . outputs the character signified by the
                // cell at the pointer
            else if (input.charAt(operatorIndex) == '.')
                System.out.print((char) (memory[ptr]));

                // , inputs a character and store it in the
                // cell at the pointer
            else if (input.charAt(operatorIndex) == ',')
                memory[ptr] = (byte) (ob.next().charAt(0));

                // [ jumps past the matching ] if the cell
                // under the pointer is 0
            else if (input.charAt(operatorIndex) == '[') {
                if (memory[ptr] == 0) {
                    operatorIndex++;
                    while (loopDepth > 0 || input.charAt(operatorIndex) != ']') {
                        if (input.charAt(operatorIndex) == '[')
                            loopDepth++;
                        else if (input.charAt(operatorIndex) == ']')
                            loopDepth--;
                        operatorIndex++;

                        //debugAttributesDisplay();
                    }
                }
            }

            // ] jumps back to the matching [ if the
            // cell under the pointer is nonzero
            else if (input.charAt(operatorIndex) == ']') {
                if (memory[ptr] != 0) {
                    operatorIndex--;
                    while (loopDepth > 0 || input.charAt(operatorIndex) != '[') {
                        if (input.charAt(operatorIndex) == ']')
                            loopDepth++;
                        else if (input.charAt(operatorIndex) == '[')
                            loopDepth--;
                        operatorIndex--;

                        //debugAttributesDisplay();
                    }
                }
            }
            operatorIndex++;
        }
    }

    private static void debugAttributesDisplay()
    {
        Tool.Debugger.debug("Interpreter",
                "\tloopDepth: " + loopDepth + "\n"
                        + "\toperatorIndex: " + operatorIndex);
    }

}

