package engine.main;

import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

/**
 * This library runs clients from a main method inside the library itself.
 * Clients must supply an implementation for this abstract class which has
 * a static initializer for initializing EntryPoint#application.
 */
public abstract class EntryPoint
{
    // Cannot have static abstract methods, therefore a static initializer is the only option.
    public static Application application;
    protected static ArgumentValidator argumentValidator;

    public static void main(@NotNull String[] args) throws Exception
    {
        System.out.print("Arguments: ");
        for (String s : args) { System.out.print(s + " "); }
        System.out.println();

        argumentValidator.validateArguments(args);

        if (application == null) MAIN_LOGGER.fatal(new IllegalStateException("Client codebase must supply an Application!"));
        application.start();
        application.dispose();
    }

    ///// UTILITY ///////////////////////

    public static int fastFloor(double x)
    {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    public static int fastCeil(double x)
    {
        int xi = (int) x;
        return xi < x ? xi + 1 : xi;
    }

    public static int bit(int x)
    {
        return 1 << x;
    }
}