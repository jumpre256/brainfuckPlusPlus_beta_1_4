package EDCBA;

import EDCBA_inspiration.EDCBA_inspirationEngine;

import java.util.Map; import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "RedundantSuppression", "FieldMayBeFinal"})
public class Interpreter   //Interpreter to the EDCBA_inspiration language.
{
    private static Scanner ob = new Scanner(System.in);
    private static int ptr; // Data pointer
    private static int length = 65535;
    // Array of byte type simulating memory of max 65535 bits. Goes from 0 to 65534.
    private static byte[] memory = new byte[length];
    private static final Stack callStack = new Stack();
    private static int loopDepth = 0;
    private static int operatorIndex = 0; // Parsing through each character of the code
    private static final Map<Integer, Locator> regularLocatorMap = new HashMap<>();
    private static final Map<Integer, Locator> zLocatorMap = new HashMap<>();
    private static byte activeVault = 0; //activeVault stores 0 at the start of the program.
    private static List<Operator> operators;

    public static void interpret(List<Operator> operators) {
        Interpreter.operators = operators;
        fillOutLocatorMap(operators); //decided what may be called "indicators" are officially called "locators"
        //Tool.Debugger.debug("Interpreter", locatorMap);
        try {
            fullInterpret(operators);
        } catch(RuntimeError error) {
            Repl.runtimeError(error);
        }
        //lazyInterpret(input);
    }

    private static void lazyInterpret(String input) {
        EDCBA_inspirationEngine.interpret(input);
    }

    @SuppressWarnings({"DataFlowIssue", "UnnecessaryLocalVariable"})
    public static void fullInterpret(List<Operator> operators) {
        //for (int i = 0; i < s.length(); i++)
        while (true) {
            if (operators.get(operatorIndex).type == OperatorType.EOF) break;
            //callStack.debug_print_stack();
            //debugInterpretTop(operators)

            // RIGHT moves the pointer to the right
            if (operators.get(operatorIndex).type == OperatorType.RIGHT) {
                if (ptr == length - 1) //If memory is full
                    ptr = 0; //pointer is returned to zero
                else
                    ptr++;
            }

            // LEFT moves the pointer to the left
            else if (operators.get(operatorIndex).type == OperatorType.LEFT) {
                if (ptr == 0) // If the pointer tries to go left past zero for now I have decided the pointer is returned to index 0.
                    ptr = 0;
                else
                    ptr--;
            }

            else if (operators.get(operatorIndex).type == OperatorType.ADD) {
                memory[ptr]++;
            }

            else if (operators.get(operatorIndex).type == OperatorType.MINUS) {
                memory[ptr]--;
            }

                // DOT outputs the character signified by the cell at the pointer
            else if (operators.get(operatorIndex).type == OperatorType.DOT) {
                System.out.print((char) (memory[ptr]));
            }

                // COMMA inputs a character and stores it in the cell at the pointer
            else if (operators.get(operatorIndex).type == OperatorType.COMMA) {
                memory[ptr] = (byte) (ob.next().charAt(0));
            }

                // LOOP_OPEN jumps past the matching LOOP_CLOSE if the cell under the pointer is 0
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

            // LOOP_CLOSE jumps back to the matching LOOP_OPEN if the cell under the pointer is nonzero
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

            //For the METHOD_CALL operator, of symbol: ";a"
            else if(operators.get(operatorIndex).type == OperatorType.METHOD_CALL)
            {
                Operator currentOperator = operators.get(operatorIndex);
                int previousPartOfCodeExecutedIndex = operatorIndex;
                callStack.push(previousPartOfCodeExecutedIndex);        //so when RET is hit, returns to the next instruction after the call is made.
                if(regularLocatorMap.containsKey((int)currentOperator.literal)){
                    operatorIndex = regularLocatorMap.get((int)currentOperator.literal).operatorIndex;
                } else {
                    throw new RuntimeError(currentOperator, "Tried to call method at locator that does not exist.");
                }
            }

            //For the BRA operator, of symbol: "|a"
            else if(operators.get(operatorIndex).type == OperatorType.BRA)
            {
                Operator currentOperator = operators.get(operatorIndex);
                if(regularLocatorMap.containsKey((int)currentOperator.literal)){
                    operatorIndex = regularLocatorMap.get((int)currentOperator.literal).operatorIndex;
                } else {
                    throw new RuntimeError(currentOperator, "Tried to jump to a locator that does not exist.");
                }
            }

            //For the RET operator, of symbol: "$"
            else if(operators.get(operatorIndex).type == OperatorType.RET)
            {
                //Tool.Debugger.debug("Interpreter", "tried to evaluate a RET operator.");
                int returnAddress = callStack.pop();
                operatorIndex = returnAddress;
            }

            else if(operators.get(operatorIndex).type == OperatorType.SET_AV)
            {
                activeVault = memory[ptr];
            }

            else if(operators.get(operatorIndex).type == OperatorType.STAR)
            {
                memory[ptr] = activeVault;
            }

            else if(operators.get(operatorIndex).type == OperatorType.CALL_Z)
            {
                //Locator targetLocator = zLocatorMap.get(...
                Operator currentOperator = operators.get(operatorIndex);
                int previousPartOfCodeExecutedIndex = operatorIndex;
                callStack.push(previousPartOfCodeExecutedIndex);        //so when RET is hit, returns to the next instruction after the call is made.
                if(regularLocatorMap.containsKey((int)currentOperator.literal)){
                    Locator targetLocator = regularLocatorMap.get((int)currentOperator.operatorIndex);
                } else {
                    throw new RuntimeError(currentOperator, "Tried to call method at locator that does not exist.");
                }
            }

            else if(operators.get(operatorIndex).type == OperatorType.DEBUG_INSTANT_SAFE_QUIT)
            {
                break;
            }

            operatorIndex++;
        }
    }

    private static void fillOutLocatorMap(List<Operator> operators) //for now SET_LOCATOR_Z works the same (CON'T)
        //[] as SET_LOCATOR, I think this is for the best and is probably the correct final solution too.
    {
        for (Operator op : operators) {
            if (op.type == OperatorType.SET_LOCATOR)    //for now SET_LOCATOR_Z works the same (CON'T)
                //[] as SET_LOCATOR, I think this is for the best and is probably the correct final solution too.
            {
                int literal = (int)op.literal;
                Locator locator = new Locator(literal, op.operatorIndex, op.type);
                regularLocatorMap.put((int) op.literal, locator);
            } else if(op.type == OperatorType.SET_LOCATOR_Z || op.type == OperatorType.SET_LOCATOR_Z_EOF) {
                int literal = (int)op.literal;
                Locator locator = new Locator(literal, op.operatorIndex, op.type);
                zLocatorMap.put((int) op.literal, locator);
            }
        }
    }

    public static Operator getCurrentOperator()
    {
        return operators.get(operatorIndex);
    }

    private static void debugInterpretTop(List<Operator> operators)
    {
        Tool.Debugger.debug("Interpreter",
                "Executing: " + operators.get(operatorIndex));
        Tool.Debugger.debug("Interpreter",
                "LOOPTOP:\n\t" + "loopDepth: " + loopDepth + "\n"
                        + "\t" + "operatorIndex: " + operatorIndex);
        Tool.Debugger.debug("Interpreter",
                "LOOPBOTTOM:");
    }

    private static void debugAttributesDisplay()
    {
        Tool.Debugger.debug("Interpreter",
                "\tloopDepth: " + loopDepth + "\n"
                        + "\toperatorIndex: " + operatorIndex);
    }

}

