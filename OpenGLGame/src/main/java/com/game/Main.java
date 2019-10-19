package com.game;

import static com.game.Application.MAIN_LOGGER;

public class Main
{
    public static boolean MULTI_RENDER;

    private static Application application;

    public static void main(String[] args)
    {
        MAIN_LOGGER.info("Arguments: ");
        for (String s: args)
        {
            MAIN_LOGGER.info(s);
        }
        if (args.length == 0)
        {
            MULTI_RENDER = false;
        }else{
            MULTI_RENDER = args[0].equals("MULTI_RENDER");
        }
        application = new Application();
        application.start();
        application.cleanUp();
    }

    static Application getApplication()
    {
        return application;
    }

    public static int fastFloor(double x)
    {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
}
