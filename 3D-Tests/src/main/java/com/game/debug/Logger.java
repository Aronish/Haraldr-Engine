package com.game.debug;

/**
 * Logger with different log levels. Much nicer than println.
 */
public class Logger {

    private static LogLevels logLevel = LogLevels.INFO;

    /**
     * Sets the log level to Info.
     */
    private static void setInfoLevel(){
        logLevel = LogLevels.INFO;
    }

    /**
     * Sets the log level to Warning.
     */
    private static void setWarningLevel(){
        logLevel = LogLevels.WARNING;
    }

    /**
     * Sets the log level to Error.
     */
    private static void setErrorLevel(){
        logLevel = LogLevels.ERROR;
    }

    /**
     * Logs a message as info.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public static <T> void info(T message){
        setInfoLevel();
        System.out.print(logLevel.label + ": " + message.toString());
    }

    /**
     * Logs a message as a warning.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public static <T> void warn(T message){
        setWarningLevel();
        System.out.print(logLevel.label + ": " + message.toString());
    }

    /**
     * Logs an error message.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public static <T> void error(T message){
        setErrorLevel();
        System.out.print(logLevel.label + ": " + message.toString());
    }

    /**
     * Contains the available log levels.
     */
    private enum LogLevels {

        ERROR("[ERROR]"),
        WARNING("[WARNING]"),
        INFO("[INFO]");

        public final String label;

        LogLevels(String label){
            this.label = label;
        }
    }
}
