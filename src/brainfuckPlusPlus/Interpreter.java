package brainfuckPlusPlus;

import java.util.Map; import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings({"CatchMayIgnoreException", "RedundantSuppression", "FieldMayBeFinal", "DuplicatedCode"})
public class Interpreter
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
    private static int sectionCode = -1;
    private static byte activeVault = 0; //activeVault stores 0 at the start of the program.
    private static List<Operator> operators;

    public static void interpret(List<Operator> operators) {
        Interpreter.operators = operators;
        fillOutLocatorMap(operators); //decided what may be called "indicators" are officially called "locators"
        //Tool.Debugger.debug("Interpreter", regularLocatorMap);
        //Tool.Debugger.debug("Interpreter", zLocatorMap);
        sectionCode = -64; //can't call a-y without loading through appropriate z-call-thing (badly written comment to be improved later) //TODO
        try {
            interpretCore(operators);
        } catch(RuntimeError error) {
            Repl.runtimeError(error);
        }
    }


    @SuppressWarnings({"DataFlowIssue", "UnnecessaryLocalVariable", "UnusedAssignment"})
    public static void interpretCore(List<Operator> operators) {
        //for (int i = 0; i < s.length(); i++)
        while (true) {
            if (operators.get(operatorIndex).type == OperatorType.EOF) break;
            //callStack.debug_print_stack();
            //debugInterpretTop(operators);

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
                Locator targetLocator = null;
                Operator currentOperator = operators.get(operatorIndex);
                int previousPartOfCodeExecutedIndex = operatorIndex;
                if(regularLocatorMap.containsKey((int)currentOperator.literal)){
                    targetLocator = regularLocatorMap.get((int)currentOperator.literal);
                } else {
                    throw new RuntimeError(currentOperator, "Tried to call method into a locator that does not exist.");
                }

                if(targetLocator != null){
                    boolean validToMethodCall = sectionCode == targetLocator.sectionCode;
                    if(validToMethodCall) {
                        callStack.push(previousPartOfCodeExecutedIndex);  //so when RET is hit, returns to the next instruction after the call is made.
                        operatorIndex = targetLocator.operatorIndex;
                    } else {
                        throw new RuntimeError(currentOperator,
                                "Tried to call method into a locator that has not been correctly loaded through a z-locator call.");
                    }
                }
            }

            //For the BRA operator, of symbol: "|a"
            else if(operators.get(operatorIndex).type == OperatorType.BRA)
            {
                Locator targetLocator = null;
                Operator currentOperator = operators.get(operatorIndex);
                if(regularLocatorMap.containsKey((int)currentOperator.literal)){
                    targetLocator = regularLocatorMap.get((int)currentOperator.literal);
                } else {
                    throw new RuntimeError(currentOperator, "Tried to jump to a locator that does not exist.");
                }

                if(targetLocator != null){
                    boolean validToMethodCall = sectionCode == targetLocator.sectionCode;
                    if(validToMethodCall) {
                        operatorIndex = targetLocator.operatorIndex;
                    } else {
                        throw new RuntimeError(currentOperator,
                                "Tried to jump to a locator that has not been correctly loaded through a z-locator call.");
                    }
                }
            }

            //For the RET operator, of symbol: "$"
            else if(operators.get(operatorIndex).type == OperatorType.RET)
            {
                //Tool.Debugger.debug("<Interpreter>", "\nEvaluating a RET operator. Operator index: " + operatorIndex);
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
                Locator targetLocator = null;
                Operator currentOperator = operators.get(operatorIndex);
                int previousPartOfCodeExecutedIndex = operatorIndex;
                if(zLocatorMap.containsKey((int)currentOperator.literal)){
                    targetLocator = zLocatorMap.get((int)currentOperator.literal);
                } else {
                    throw new RuntimeError(currentOperator, "Tried to call method at locator that does not exist.");
                }
                if(targetLocator != null)   //successfully executes the CALL_Z operator:
                {
                    if(targetLocator.type == OperatorType.SET_LOCATOR_Z) {
                        callStack.push(previousPartOfCodeExecutedIndex);   //so when RET is hit, returns to the next instruction after the call is made.
                        operatorIndex = targetLocator.operatorIndex;
                        sectionCode = (int)targetLocator.literal;
                        //Tool.Debugger.debug("<Interpreter>", "\nsection code set to: " + sectionCode + " when operator index is: " + operatorIndex);
                    } else if(targetLocator.type == OperatorType.SET_LOCATOR_Z_EOF){
                        sectionCode = (int)targetLocator.literal;
                    }
                }
            }

            else if(operators.get(operatorIndex).type == OperatorType.DEBUG_INSTANT_SAFE_QUIT)
            {
                break;
            }

            operatorIndex++;
        }
    }

    private static void fillOutLocatorMap(List<Operator> operators)
    {
        for (Operator op : operators) {
            if (op.type == OperatorType.SET_LOCATOR)
            {
                int literal = (int)op.literal;
                Locator locator = new Locator(literal, op.operatorIndex, op.type, sectionCode);
                regularLocatorMap.put((int) op.literal, locator);
            }
            else if(op.type == OperatorType.CALL_Z && sectionCode == -1){
                sectionCode = (int)op.literal;
                //Tool.Debugger.debug("Interpreter", "new section code in fill stage: " + sectionCode);
            }
            else if(op.type == OperatorType.SET_LOCATOR_Z || op.type == OperatorType.SET_LOCATOR_Z_EOF) {
                int literal = (int)op.literal;
                Locator locator = new Locator(literal, op.operatorIndex, op.type, -64); //value -64 is used to indicate section codes are (CON'T)
                //[...] irrelevant for these set_locator operators.
                zLocatorMap.put((int) op.literal, locator);
                if(sectionCode == (int)op.literal){
                    sectionCode = -1;
                }
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

