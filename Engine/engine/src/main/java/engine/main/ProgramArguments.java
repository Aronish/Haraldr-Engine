package engine.main;

import java.util.HashMap;
import java.util.Map;

public class ProgramArguments
{
    private static final Map<String, String> programArguments = new HashMap<>();

    public static void setArgument(String argument, String value)
    {
        programArguments.put(argument, value);
    }

    public static String getStringValue(String argument)
    {
        return programArguments.get(argument);
    }

    public static boolean isArgumentSet(String argument)
    {
        return programArguments.containsKey(argument);
    }
}
