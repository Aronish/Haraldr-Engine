package main;

import org.jetbrains.annotations.NotNull;

import java.lang.instrument.Instrumentation;

public class Premain
{
    public static void premain(String agentArgs, @NotNull Instrumentation instrumentation)
    {
        System.out.println("Ran Preprocessor");
    }
}
