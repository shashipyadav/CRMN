package com.example.myapplication.util;

public class StringUtil {
    public static String replaceChar(String str, char ch, int index) {
        StringBuilder myString = new StringBuilder(str);
        myString.setCharAt(index, ch);
        return myString.toString();
    }

    public static int getCharIndex(Character character, String str){
        int index = str.lastIndexOf(character);
        return index;
    }

    public static String getTagTitle( String str){

        if(str.contains("@")){
            String[] arr = str.split("@");
            return arr[0];
        }

        return str;
    }
}

