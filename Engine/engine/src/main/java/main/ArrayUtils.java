package main;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils
{
    @NotNull
    @Contract(pure = true)
    public static float[] toPrimitiveArrayF(@NotNull List<Float> list)
    {
        float[] primitiveArray = new float[list.size()];
        int insertIndex = 0;
        for (Float element : list)
        {
            primitiveArray[insertIndex++] = element;
        }
        return primitiveArray;
    }

    @NotNull
    @Contract(pure = true)
    public static int[] toPrimitiveArrayI(@NotNull List<Integer> list)
    {
        int[] primitiveArray = new int[list.size()];
        int insertIndex = 0;
        for (Integer element : list)
        {
            primitiveArray[insertIndex++] = element;
        }
        return primitiveArray;
    }

    @NotNull
    public static List<Float> toList(@NotNull float[] array)
    {
        List<Float> list = new ArrayList<>();
        for (float f : array)
        {
            list.add(f);
        }
        return list;
    }

    @NotNull
    public static List<Integer> toList(@NotNull int[] array)
    {
        List<Integer> list = new ArrayList<>();
        for (int i : array)
        {
            list.add(i);
        }
        return list;
    }
}
