package BrainfuckAss;

import java.util.List;
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

    public static void interpret(List<Operator> operators) {
        fullInterpret(operators);
        //lazyInterpret(input);
    }

    private static void lazyInterpret(String input) {
        BrainfuckOriginal.BrainfuckEngine.interpret(input);
    }

    public static void fullInterpret(List<Operator> operators) {
        //for (int i = 0; i < s.length(); i++)
        while (true) {
            if (operators.get(operatorIndex).type == OperatorType.EOF) break;
            // BrainFuck is a small language with only eight instructions. In this loop we check and execute each of those eight instructions

            /*Tool.Debugger.debug("Interpreter",
                    "Executing: " + input.charAt(operatorIndex));
            Tool.Debugger.debug("Interpreter",
                    "LOOPTOP:\n\t" + "loopDepth: " + loopDepth + "\n"
                    + "\t" + "operatorIndex: " + operatorIndex);
            Tool.Debugger.debug("Interpreter",
                    "LOOPBOTTOM:");*/

            // > moves the pointer to the right
            if (operators.get(operatorIndex).type == OperatorType.LEFT) {
                if (ptr == length - 1)//If memory is full
                    ptr = 0;//pointer is returned to zero
                else
                    ptr++;
            }

            // < moves the pointer to the left
            else if (operators.get(operatorIndex).type == OperatorType.RIGHT) {
                if (ptr == 0) // If the pointer reaches zero
                    // pointer is returned to rightmost memory
                    // position
                    ptr = length - 1;
                else
                    ptr--;
            }

            // + increments the value of the memory
            // cell under the pointer
            else if (operators.get(operatorIndex).type == OperatorType.ADD)
                memory[ptr]++;

                // - decrements the value of the memory cell
                // under the pointer
            else if (operators.get(operatorIndex).type == OperatorType.MINUS)
                memory[ptr]--;

                // . outputs the character signified by the
                // cell at the pointer
            else if (operators.get(operatorIndex).type == OperatorType.DOT)
                System.out.print((char) (memory[ptr]));

                // , inputs a character and store it in the
                // cell at the pointer
            else if (operators.get(operatorIndex).type == OperatorType.COMMA)
                memory[ptr] = (byte) (ob.next().charAt(0));

                // [ jumps past the matching ] if the cell
                // under the pointer is 0
            else if (operators.get(operatorIndex).type == OperatorType.LOOP_OPEN) {
                if (memory[ptr] == 0) {
                    operatorIndex++;
                    while (loopDepth > 0 || operators.get(operatorIndex).type != OperatorType.LOOP_CLOSE) {
                        if (operators.get(operatorIndex).type == OperatorType.LOOP_OPEN)
                            loopDepth++;
                        else if (operators.get(operatorIndex).type == OperatorType.LOOP_CLOSE)
                            loopDepth--;
                        operatorIndex++;

                        //debugAttributesDisplay();
                    }
                }
            }

            // ] jumps back to the matching [ if the
            // cell under the pointer is nonzero
            else if (operators.get(operatorIndex).type == OperatorType.LOOP_CLOSE) {
                if (memory[ptr] != 0) {
                    operatorIndex--;
                    while (loopDepth > 0 || operators.get(operatorIndex).type != OperatorType.LOOP_OPEN) {
                        if (operators.get(operatorIndex).type == OperatorType.LOOP_CLOSE)
                            loopDepth++;
                        else if (operators.get(operatorIndex).type == OperatorType.LOOP_OPEN)
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

