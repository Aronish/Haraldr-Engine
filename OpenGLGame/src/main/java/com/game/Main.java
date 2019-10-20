package com.game;

import static com.game.Application.MAIN_LOGGER;

public class Main
{
    public static boolean MULTI_RENDER;

    public static void main(String[] args) throws Exception
    {
        System.out.print("Arguments: ");
        for (String s: args) { System.out.print(s + " "); }
        System.out.println();
        if (args.length > 0)
        {
            if (args[0].equals("MULTI_RENDER"))
            {
                MULTI_RENDER = true;
            }else if (args[0].equals("INSTANCED")){
                MULTI_RENDER = false;
            }else{
                MAIN_LOGGER.fatal(new IllegalArgumentException("Unknown Render System Type!"));
            }
        }else{
            MAIN_LOGGER.fatal(new IllegalArgumentException("Missing Render System Type!"));
        }

        Application application = new Application();
        application.start();
        application.dispose();
    }

    public static int fastFloor(double x)
    {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
}
