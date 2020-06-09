package com.feihong.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyClassLoader extends ClassLoader{

    @Override
    public Class<?> findClass(String name){
        Class clazz = null;
        try{
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                String mypath = System.getProperty("user.dir") + "/plugin/" + name + ".class";
                if(!new File(mypath).exists()){
                    mypath = System.getProperty("user.dir") + "/plugin/" + name.substring(name.lastIndexOf(".") + 1) + ".class";
                }

                Path path  = Paths.get(mypath);
                byte[] cLassBytes = Files.readAllBytes(path);
                clazz = defineClass(name, cLassBytes, 0, cLassBytes.length);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return clazz;
    }
}
