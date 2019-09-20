package com.test;

import java.io.IOException;
import java.io.InputStream;

public class FileInputTest {

    public static void main(String[] args){
        StringBuilder sb = new StringBuilder();
        try (InputStream file = FileInputTest.class.getResourceAsStream("test/test.txt")){
            int data = file.read();
            while (data != -1){
                sb.append((char) data);
                data = file.read();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(sb.toString());
    }

}
