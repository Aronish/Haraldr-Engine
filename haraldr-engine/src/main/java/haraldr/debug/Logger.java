package haraldr.debug;

import haraldr.main.EntryPoint;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class Logger
{
    public static LogLevel logLevel = LogLevel.INFO;

    public static <T> void info(T message)
    {
        if (LogLevel.INFO.compareTo(logLevel) <= 0) log(message, LogLevel.INFO);
    }

    @SafeVarargs
    public static <T> void info(T... messages)
    {
        for (T message : messages) info(message);
    }

    public static <T> void warn(T message)
    {
        if (LogLevel.WARN.compareTo(logLevel) <= 0) log(message, LogLevel.WARN);
    }

    public static <T> void error(T message)
    {
        log(message, LogLevel.ERROR);
    }

    private static <T> void log(T message, LogLevel logLevel)
    {
        if (EntryPoint.DEBUG) System.out.printf("%s [%s]: %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), logLevel, message);
    }

    public enum LogLevel
    {
        ERROR,
        WARN,
        INFO
    }
}
