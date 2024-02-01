package EDCBA;

import java.util.Deque;
import java.util.LinkedList;

public class Stack {
    private final Deque<Integer> stackContents = new LinkedList<>();

    public void push(int value)
    {
        stackContents.addFirst(value);
    }

    public int pop() throws RuntimeError
    {
        if(!stackContents.isEmpty()){
            return stackContents.removeFirst();
        } else{
            throw new RuntimeError(Interpreter.getCurrentOperator(),
                    "Attempted to return but failed due to empty call stack.");
        }
    }

    public void debug_print_stack()
    {
        System.out.println(stackContents);
    }


}
