package com.game;

import java.util.ArrayList;
import java.util.List;

import static com.game.Application.MAIN_LOGGER;

public class ArrayUtils
{
    public static float[] toPrimitiveArrayF(List<Float> list)
    {
        float[] primitiveArray = new float[list.size()];
        int insertIndex = 0;
        for (Float element : list)
        {
            primitiveArray[insertIndex++] = element;
        }
        return primitiveArray;
    }

    public static int[] toPrimitiveArrayI(List<Integer> list)
    {
        int[] primitiveArray = new int[list.size()];
        int insertIndex = 0;
        for (Integer element : list)
        {
            primitiveArray[insertIndex++] = element;
        }
        return primitiveArray;
    }

    public static List<Float> toList(float[] array)
    {
        List<Float> list = new ArrayList<>();
        for (float f : array)
        {
            list.add(f);
        }
        return list;
    }

    public static List<Integer> toList(int[] array)
    {
        List<Integer> list = new ArrayList<>();
        for (int i : array)
        {
            list.add(i);
        }
        return list;
    }
}
