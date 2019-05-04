package main.java;

/**
 * Logger with different log levels. Much nicer than println.
 */
public class Logger {

    private static LogLevels logLevel;

    /**
     * Initializes the logger with info level.
     */
    public Logger(){
        logLevel = LogLevels.INFO;
    }

    /**
     * Sets the log level to info.
     */
    public static void setInfoLevel(){
        logLevel = LogLevels.INFO;
    }

    /**
     * Sets the log level to warning.
     */
    public static void setWarningLevel(){
        logLevel = LogLevels.WARNING;
    }

    /**
     * Sets the log level to error.
     */
    public static void setErrorLevel(){
        logLevel = LogLevels.ERROR;
    }

    public static <T> void log(T message){
        System.out.print(logLevel.toString() + ": ");
        System.out.println(message);
    }

    /**
     * Contains the available log levels.
     */
    public enum LogLevels {
        ERROR("[ERROR]"),
        WARNING("[WARNING]"),
        INFO("[INFO]");

        private final String string;

        LogLevels(String string){
            this.string = string;
        }

        public String toString(){
            return this.string;
        }
    }
}
