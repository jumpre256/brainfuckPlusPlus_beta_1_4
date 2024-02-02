package EDCBA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AssemblerOperations {

    protected int lineNumber = 1;
    protected int current = 0;
    protected int operatorIndex = 0;
    protected String source;
    @SuppressWarnings("FieldMayBeFinal")
    protected List<Operator> operators = new ArrayList<>();

    public AssemblerOperations(String source)
    {
        this.source = source;
    }

    protected char peek()
    {
        if (isAtEnd()) return '\0';
        else return source.charAt(current);
    }

    protected char peek(int n)    //code credit: Robert Nystrom.
    {
        int current = this.current;
        int i = n - 1;
        if (current + i >= source.length()) return '\0';
        else return source.charAt(current + i);
    }

    //@SuppressWarnings("DataFlowIssue")
    protected boolean match(char expected)
    {
        boolean returnValue;
        if(isAtEnd()) returnValue = false;
        else if(expected == source.charAt(current)){
            current++;
            returnValue = true;
        } else{
            returnValue = false;
        }
        return returnValue;
    }

    protected char advance()
    {
        int beforeIncrement = current;
        current++;
        return source.charAt(beforeIncrement);
    }


    protected boolean isAtEnd()
    {
        return current >= source.length();
    }

    protected void addOperator(OperatorType type)
    {
        addOperator(type, null);
    }

    protected void addOperator(OperatorType type, Object literal)
    {
        operators.add(new Operator(type, literal, lineNumber, operatorIndex));
        operatorIndex++;
    }

    protected void writeToFile(List<Operator> operators, String filePath) {
        boolean success = false;
        while (!success) {
            File file = new File(filePath);
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                if(operators != null){
                    for(Operator op : operators){
                        bufferedWriter.write(op.toString() + "\n");
                    }
                }
                bufferedWriter.close();
                success = true;
            } catch (FileNotFoundException e) {
                try {
                    file.createNewFile();
                } catch(Exception e1) {}
            } catch(Exception e0) {}
        }
        //Tool.Debugger.debug(this, "Succesfully wrote to file.");
    }
}
