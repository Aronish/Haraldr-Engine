package main.java.debug;

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
     * Logs a message with the currently set log level.
     * @param message the message to log. Accepts any type.
     * @param <T> the type of the message.
     */
    public static <T> void log(T message){
        System.out.print(logLevel.toString() + ": ");
        System.out.println(message);
        setInfoLevel();
    }

    /**
     * Contains the available log levels.
     */
    private enum LogLevels {
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
