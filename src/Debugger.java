public class Debugger {

    public static void debug(String caller, Object message)
    {
        debug(caller, message, '\n');
    }
    public static void debug(Object caller, Object message)
    {
        debug(caller, message, '\n');
    }

    public static void debug(Object caller, Object message, char lineEnder)
    {
        //System.out.print(caller + ": " + message + lineEnder);
        System.out.print(message.toString() + lineEnder);
    }
}
