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

    /**
     * Prints the log level and the provided message.
     * @param message the log message.
     */
    public static void log(String message){
        System.out.print(logLevel.toString() + " ");
        System.out.println(message);
    }

    /**
     * Prints the log level and the provided boolean.
     * @param bool the boolean to log.
     */
    public static void log(boolean bool){
        System.out.print(logLevel.toString() + " ");
        System.out.println(bool);
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
