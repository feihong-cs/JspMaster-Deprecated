package com.feihong.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    public static boolean isInitialized(){
        boolean result = false;
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            String key = properties.getProperty("communicationKey");
            if( key != null && !key.trim().equals("")){
                return InputValidatorUtil.isValidEncryptKey(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getCommunicationKey(){
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            return properties.getProperty("communicationKey");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean isEncrypt(){
        boolean result = false;
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            String key = properties.getProperty("encrypt");
            if( key != null && !key.trim().equals("")){
                return key.equalsIgnoreCase("true");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void setEncrypt(boolean bool){
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            if(bool){
                properties.setProperty("encrypt","true");
            }else{
                properties.setProperty("encrypt","false");
            }
//            FileOutputStream fous = new FileOutputStream(ConfigUtil.class.getResource("/").getPath() + "config/config.properties");
            FileOutputStream fous = new FileOutputStream(path);
            properties.store(fous, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setCommnunicationKey(String key){
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            properties.setProperty("communicationKey", key);
//            FileOutputStream fous = new FileOutputStream(ConfigUtil.class.getResource("/").getPath() + "config/config.properties");
            FileOutputStream fous = new FileOutputStream(path);
            properties.store(fous, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getIV(){
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            return properties.getProperty("IV");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void setIV(String iv){
        try {
            Properties properties = new Properties();
//            properties.load(ClassLoader.getSystemResourceAsStream("config/config.properties"));
            String path = System.getProperty("user.dir") + "/config/config.properties";
            properties.load(new FileInputStream(path));
            properties.setProperty("IV", iv);
//            FileOutputStream fous = new FileOutputStream(ConfigUtil.class.getResource("/").getPath() + "config/config.properties");
            FileOutputStream fous = new FileOutputStream(path);
            properties.store(fous, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
