public class BrainfuckAss {

    public static void interpret(String input) {}


    public static void _precompile(String path)
    {

    }

    private static boolean isReserved(char c)
    {
        switch(c){
            case '[':
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
            case '{':
            case '}':
                return true;
            default:
                System.out.print((int)c);
                break;

        }

        return false;   //doesn't mean anything, code added just to keep ide happy.
    }
}
