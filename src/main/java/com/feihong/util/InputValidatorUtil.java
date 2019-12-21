package com.feihong.util;

import java.net.URL;

public class InputValidatorUtil {

    public static boolean isValidURL(String url){
        try{
            URL u = new URL(url);
        }catch(Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isValidEncryptKey(String encrypteKey){
        if(encrypteKey == null || encrypteKey.equalsIgnoreCase("")){
            return true;
        }

        if(encrypteKey.length() != 32){
            return false;
        }

        String regex = "^[a-zA-Z0-9]{32}$";
        return encrypteKey.matches(regex);
    }

    public static boolean isValidIV(String iv){
        if(iv == null || iv.equalsIgnoreCase("")){
            return true;
        }

        if(iv.length() != 16){
            return false;
        }

        String regex = "^[a-zA-Z0-9]{16}$";
        return iv.matches(regex);
    }
}
