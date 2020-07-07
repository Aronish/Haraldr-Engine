package engine.debug;

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
        System.out.println(String.format("%s [%s]: %s", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), logLevel, message));
    }

    public enum LogLevel
    {
        ERROR,
        WARN,
        INFO
    }
}
