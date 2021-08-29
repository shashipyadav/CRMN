package com.example.myapplication.util;

public class ExtensionUtil {
   public static String getExtension(String str){
        //check if its a file url
       if (str.matches(".*\\.\\D{3,6}$") ){

           int begin = str.lastIndexOf(".");
           if(begin == -1)
               return "";
           String ext = str.substring(begin + 1);
           return ext;
       }else{
           return "";
       }

    }

}
