package haraldr.math;

import org.jetbrains.annotations.Contract;

public class MathUtils
{
    @Contract(pure = true)
    public static int fastFloor(double x)
    {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    @Contract(pure = true)
    public static int fastCeil(double x)
    {
        int xi = (int) x;
        return xi < x ? xi + 1 : xi;
    }

    @Contract(pure = true)
    public static int bit(int x)
    {
        return 1 << x;
    }
}
