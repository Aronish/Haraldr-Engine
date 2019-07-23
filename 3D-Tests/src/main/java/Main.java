package main.java;

public class Main {

    private static Application application;

    public static void main(String[] args){
        application = new Application();
        application.run();
        application.cleanUp();
    }

    static Application getApplication(){
        return application;
    }

    public static int fastFloor(double x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
}
