package main.java;
//TODO Add JavaDoc
public class Logger {

    private static LogLevels logLevel;

    public Logger(){
        logLevel = LogLevels.INFO;
    }

    public static void setInfoLevel(){
        logLevel = LogLevels.INFO;
    }

    public static void setWarningLevel(){
        logLevel = LogLevels.WARNING;
    }

    public static void setErrorLevel(){
        logLevel = LogLevels.ERROR;
    }

    public static void log(String message){
        System.out.print(logLevel.toString() + " ");
        System.out.println(message);
    }

    public static void log(boolean bool){
        System.out.print(logLevel.toString() + " ");
        System.out.println(bool);
    }

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
