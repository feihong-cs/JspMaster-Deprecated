package com.feihong.util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BasicUtil {

    public static String getContent(String fileName){
        try {
            InputStream in = new FileInputStream(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len = 0;
            byte[] buffer = new byte[1024];
            while((len = in.read(buffer)) != -1){
                baos.write(buffer, 0, len);
            }

            baos.close();
            in.close();

            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void findStringList(File dir, List<String> allFile) {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        File[] files = dir.listFiles();
        if(files != null){
            for (File file : files) {// 循环，添加文件名或回调自身
                if (file.isFile()) {// 如果文件
                    allFile.add(file.getName());// 添加文件全路径名
                } else {// 如果是目录
                    findStringList(file, allFile);// 回调自身继续查询
                }
            }
        }
    }

    public static void findFileList(File dir, List<File> allFile) {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        File[] files = dir.listFiles();
        if(files != null){
            for (File file : files) {// 循环，添加文件名或回调自身
                if (file.isFile()) {// 如果文件
                    allFile.add(file);// 添加文件全路径名
                } else {// 如果是目录
                    findFileList(file, allFile);// 回调自身继续查询
                }
            }
        }
    }

    public static List<String> getClassName(){
        List<String> list = new ArrayList<>();
        URL url = BasicUtil.class.getProtectionDomain().getCodeSource().getLocation();
        if(url.getPath().endsWith(".jar")){
            JarFile jar = null;
            try {
                jar = new JarFile(url.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Enumeration enumeration = jar.entries();
            while(enumeration.hasMoreElements()){
                JarEntry entry = (JarEntry)enumeration.nextElement();
                if(entry.getName().startsWith("com/feihong/executor") && entry.getName().endsWith(".class")){
                    list.add(entry.getName().split("/",4)[3]);
                }
            }
        }else{
            String path = BasicUtil.class.getResource("/com/feihong/executor").getPath();
            File file = new File(path);
            for(File f : file.listFiles()){
                list.add(f.getName());
            }
        }

        return list;

    }
}
