package com.viewol.sys.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {

        String path = "C:\\Users\\jhss-jishu\\Desktop\\22222222222222222.txt";

        readFileByLines(path);
    }


    public static void readFileByLines(String fileName) {

        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
//                if(tempString.length()>400){
//                    continue;
//                }
                System.out.println("\""+line+"\":"+tempString.substring(tempString.indexOf("d=")+2, tempString.indexOf("/>"))+",");
                line++;
            }
            reader.close();

            for(int i = 0; i<line; i++){
                if(i%2==1){
                    System.out.println("\""+i+"\": {attrs: {fill: \"#000\"}, attrsHover: {fill: \"#000\"}},");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
