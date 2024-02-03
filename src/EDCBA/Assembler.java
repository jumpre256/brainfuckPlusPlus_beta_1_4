package EDCBA;

import Tool.Debugger;

import java.util.List;

@SuppressWarnings({"RedundantSuppression", "StatementWithEmptyBody"})
public class Assembler extends AssemblerOperations{

    public boolean hadError = false;

    public Assembler(String source) {
        super(source);
    }

    @SuppressWarnings({"UnusedReturnValue", "UnnecessaryLocalVariable"})
    public List<Operator> assemble(String outputPath) {
        //Remove fluff:
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (isReserved(c)) {
                strBuilder.append(c);
            }
        }
        String fluffRemoved = strBuilder.toString();
        source = fluffRemoved;
        //convert to a list of operators:
        List<Operator> operators = null;
        try {
            operators = doAssembleMain();
            operators.add(new Operator(OperatorType.EOF, null, lineNumber, operatorIndex));
        } catch (AssemblerError error) {
            System.err.printf("Error: [line %d]: %s%n", error.getLineNumber(), error.getMessage());
            hadError = true;
        }


        if (!hadError) {
            writeToFile(operators, outputPath);  //for debug purposes.
            return operators;
        } else return null;
    }

    @SuppressWarnings("ConstantValue")
    private List<Operator> doAssembleMain() throws AssemblerError
    {
        while (!isAtEnd()) {
            char c = advance();


            switch(c) {
                case '[':
                    addOperator(OperatorType.LOOP_OPEN); break;
                case ']':
                    addOperator(OperatorType.LOOP_CLOSE); break;
                case '+':
                    addOperator(OperatorType.ADD); break;
                case '-':
                    addOperator(OperatorType.MINUS); break;
                case '.':
                    addOperator(OperatorType.DOT); break;
                case ',':
                    addOperator(OperatorType.COMMA); break;
                case '>':
                    addOperator(OperatorType.RIGHT); break;
                case '<':
                    addOperator(OperatorType.LEFT); break;
                case ':':
                    set_locator(); break;
                case ';':
                    method_call(); break;
                case '|':
                    bra(); break;
                case '*':
                    addOperator(OperatorType.STAR); break;
                case '^':
                    addOperator(OperatorType.SET_AV); break;
                case '$':
                    addOperator(OperatorType.RET); break;
                case '@':
                    addOperator(OperatorType.AT_SYMBOL); break;
                case '"':
                    addOperator(OperatorType.DOUBLE_QUOTE); break;
                case '!':
                    addOperator(OperatorType.BANG); break;
                case '#':
                    hash(); break;
                case '?':
                    addOperator(OperatorType.DEBUG_INSTANT_SAFE_QUIT); break;
                case '\n': {
                    lineNumber++; break;
                }
                case '{':
                case '}':   //'{' and '}' characters on their own are ignored for now.
                    break;      //TODO: more rigorously evaluate in the case of this situation.
                default:
                    boolean isReserved = ((c >= 48) && (c <= 57)) || ((c >= 97) && (c <= 122));
                    if(isReserved){
                        //I think it is best for now if unexpect char characters found in source are treated as a comment for now.
                        //addOperator(OperatorType.STRING_LITERAL_CHAR);
                    }
                    break;
            }

        }
        return operators;
    }

    private String _removeCommentsAndWhitespace(String input) throws AssemblerError
    {
        StringBuilder strBuilder = new StringBuilder();
        while (!isAtEnd()) {
            char c = advance();
            boolean isReserved = isReserved(c);
            if (isReserved) {
                if (c == '#') hash();
                else if (c == '}') throw new AssemblerError(lineNumber,"Unexpected '}' character.");
                else if (c == '\n') {
                    lineNumber++;
                } else {
                    strBuilder.append(c);
                }
            }
        }
        return strBuilder.toString();
    }

    //@SuppressWarnings("DuplicatedCode")
    private void z_character(OperatorType operatorType) throws AssemblerError
    {
        char[] digits = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int z_locator_key;
        if(match('0')) z_locator_key = 0;
        else{
            StringBuilder strBuilder = new StringBuilder();
            char firstChar = consume(digits, "'z' character must be followed by an integer.");
            strBuilder.append(firstChar);
            char c = peek();
            while((c >= 48) && (c <= 57)){
                strBuilder.append(advance());
                c = peek();
            }
            z_locator_key = Integer.parseInt(strBuilder.toString());
        }
        switch(operatorType)
        {
            case METHOD_CALL:
            case BRA:
            {
                addOperator(OperatorType.CALL_Z, z_locator_key+26);
                break;
            }
            case SET_LOCATOR:
            default:
            {
                while(peek() == '\n' && !isAtEnd()){
                    advance();
                    lineNumber++;
                }
                //Debugger.debug(this, "isAtEnd?: " + isAtEnd());
                if(isAtEnd()) {
                    addOperator(OperatorType.SET_LOCATOR_Z_EOF, z_locator_key+26);
                } else if(peek() != '$') {
                    throw new AssemblerError(lineNumber,
                            "Must be '$' or EOF in source after setting a z locator.");
                } else {
                    addOperator(OperatorType.SET_LOCATOR_Z, z_locator_key + 26);
                }
                break;
            }
        }
    }

    private void set_locator() throws AssemblerError
    {
        char nextChar = advance();
        while(nextChar == '\n'){
            nextChar = advance();
            lineNumber++;
        }
        boolean isValidLocator = (nextChar >= 97) && (nextChar <= 121); //is 'a' to 'y'
        if(isValidLocator){
            addOperator(OperatorType.SET_LOCATOR, ((int)nextChar) - 96);
        } else if (nextChar == 122) {
            z_character(OperatorType.SET_LOCATOR);
        } else if(nextChar == '\0') {
            //do nothing.
         } else {
            throw new AssemblerError(lineNumber, "a ':' character must be followed by an 'a' to 'z' character.");
         }
    }

    private void method_call() throws AssemblerError
    {
        char nextChar = advance();
        while(nextChar == '\n'){
            nextChar = advance();
            lineNumber++;
        }
        boolean isValidLocator = (nextChar >= 97) && (nextChar <= 121); //is 'a' to 'y'
        if(isValidLocator){
            addOperator(OperatorType.METHOD_CALL, ((int)nextChar) - 96);
        } else if (nextChar == 122) {
            z_character(OperatorType.METHOD_CALL);
        } else if(nextChar == '\0') {
            //do nothing.
        } else {
            throw new AssemblerError(lineNumber, "a ';' character must be followed by an 'a' to 'z' character.");
        }
    }

    private void bra() throws AssemblerError
    {
        char nextChar = advance();
        while(nextChar == '\n'){
            nextChar = advance();
            lineNumber++;
        }
        boolean isValidLocator = (nextChar >= 97) && (nextChar <= 121); //is 'a' to 'y'
        if(isValidLocator){
            addOperator(OperatorType.BRA, ((int)nextChar) - 96);
        } else if (nextChar == 122) {
            z_character(OperatorType.BRA);
        } else if(nextChar == '\0') {
            //do nothing.
        } else {
            throw new AssemblerError(lineNumber, "a '|' character must be followed by an 'a' to 'z' character.");
        }
    }

    private void hash() throws AssemblerError   //handle streams of commented out text initiated with a hashtag character.
            //inspiration for code's structure from Robert Nystrom.
    {
        if(match('{')){
            //in a multiline comment.
            while(true){
                if(peek() == '\n') lineNumber++;
                else if(peek() == '#' && peek(2) == '{') throw new AssemblerError(lineNumber,"Cannot initiate a new multiline comment within an existing multiline comment.");

                else if(peek() == '}' && peek(2) == '#'){
                    advance(); advance();
                    break;      //break out of while loop.
                }

                if(peek() == '\0')    //this has to be an "if" not an "else if" to make sure if false, runs advance().
                {
                    throw new AssemblerError(lineNumber,"Unterminated multiline comment.");
                } else {
                    advance(); //eats up characters in the source code stream while appropriately updating the "operatorIndex" variable.
                }
            }
        } else if(peek() == '\0') {
            //do nothing.
        } else {
            //a comment goes until the end of the line.
            while (peek() != '\n' && !isAtEnd()){
                advance();
            }
        }
    }

    private boolean isReserved(char c)
    {
        boolean returnValue = false;
        switch(c){
            case '[':
            case '$':
            case '?':
            case ']':
            case '+':
            case '-':
            case '.':
            case ',':
            case ':':
            case ';':
            case '|':
            case '>':
            case '<':
            case '*':
            case '^':
            case '@': //14th reserved character
            case '"':
            case '!':
            case '#':
            case '\n':
            case '{':
            case '}':
                returnValue = true; break;
            default:
                returnValue = ((c >= 48) && (c <= 57)) || ((c >= 97) && (c <= 122));
                break;

        }
        return returnValue;
    }


}
