package main.java;

public class BitTest {

    public static void main(String[] args){
        for (int i = 0, counter = 0; i < 256; ++i, ++counter){
            System.out.println(1 << (i / 64));
        }
    }
}
