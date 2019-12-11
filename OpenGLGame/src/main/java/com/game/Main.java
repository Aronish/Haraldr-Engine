package com.game;

import com.game.graphics.RenderSystemType;
import org.jetbrains.annotations.NotNull;

public class Main
{
    public static RenderSystemType renderSystemType;

    public static void main(@NotNull String[] args) throws Exception
    {
        System.out.print("Arguments: ");
        for (String s : args) { System.out.print(s + " "); }
        System.out.println();

        if (args.length > 0)
        {
            renderSystemType = RenderSystemType.validateArgument(args[0]);
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

    public static int fastCeil(double x)
    {
        int xi = (int) x;
        return xi < x ? xi + 1 : xi;
    }
}