package haraldr.main;

import java.util.HashMap;
import java.util.Map;

public class ProgramArguments
{
    private static final Map<String, String> programArguments = new HashMap<>();

    public static void setArgument(String argument, String value)
    {
        programArguments.put(argument, value);
    }

    public static boolean isArgumentSet(String argument)
    {
        return programArguments.containsKey(argument);
    }

    public static int getIntOrDefault(String argument, int defaultValue)
    {
        return programArguments.containsKey(argument) ? Integer.parseInt(programArguments.get("MSAA")) : defaultValue;
    }
}