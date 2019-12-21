package com.feihong.util;

import com.feihong.bean.FileModel;
import com.feihong.executor.CommandExecutor;
import com.feihong.executor.CommandExecutorFactory;
import java.util.*;

public class FileOperationUtil {

    public static void ModifyFile(String path, String newContent){
        String base64String = Base64.getEncoder().encodeToString(newContent.getBytes());
        String cmd;
        if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
            cmd = "powershell -nop -w hidden -ep bypass -c \"[System.Text.Encoding]::UTF8.GetString([System" +
                    ".Convert]::FromBase64String('" + base64String + "')) > '" + path + "'\"";
        }else{
            cmd = "python -c \"import base64;content=base64.b64decode" +
                    "(r'" + base64String + "');fout=open(r'" + path + "','wb');fout.write(content);fout.close();\"";
        }
        CommandExecutorFactory.getInstance().exec(cmd);
    }

    public static void deleteFile(String path, String type){
        if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
            if(type == "文件"){
                CommandExecutorFactory.getInstance().exec("del \"" + path + "\"");
            }else{
                CommandExecutorFactory.getInstance().exec("rd /s/q \"" + path + "\"");
            }
        }else{
            CommandExecutorFactory.getInstance().exec("rm -rf \'" + path + "\'");
        }
    }

    public static void renameFile(String oldFilename, String newFilename){
        if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
            CommandExecutorFactory.getInstance().exec("move \"" + oldFilename + "\" \"" + newFilename + "\"");
        }else{
            CommandExecutorFactory.getInstance().exec("mv '" + oldFilename + "' '" + newFilename + "'");
        }
    }

    public static void newFile(String path){
        if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
            CommandExecutorFactory.getInstance().exec("powershell -nop -w hidden -ep bypass -c New-Item -Path '" + path + "' -ItemType File");
        }else{
            CommandExecutorFactory.getInstance().exec("touch '" + path + "'");
        }
    }

    public static void newDirectory(String path){
        if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
            CommandExecutorFactory.getInstance().exec("md \"" + path + "\"");
        }else{
            CommandExecutorFactory.getInstance().exec("mkdir '" + path + "'");
        }
    }

    public static List<String> getWindowsDrivers() {
        List<String> result = new ArrayList<>();
        String[] drivers = CommandExecutorFactory.getInstance().exec("wmic logicaldisk where drivetype=3 get deviceid").getResponseResult().split("\r\n");

        for (String driver : drivers) {
            driver = driver.replaceAll("\r|\n", "");
            if (driver.indexOf(":") > 0) {
                result.add(driver.trim());
            }
        }

        return result;
    }

    public static List<FileModel> getFileEntry(String dir){
        List<FileModel> list;
        if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
            list = getFileEntryForWindows(dir);
        }else{
            list = getFileEntryForLinux(dir);
        }

        //排序，文件夹放前面，文件放后面
        Collections.sort(list, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel o1, FileModel o2) {
                if(o1.getType().equals("文件夹") && o2.getType().equals("文件夹")){
                    return 0;
                }

                if(o1.getType().equals("文件") && o2.getType().equals("文件")){
                    return 0;
                }

                if(o1.getType().equals("文件夹")){
                    return -1;
                }else{
                    return 1;
                }
            }
        });

        return list;
    }

    private static List<FileModel> getFileEntryForWindows(String dir){
        List<FileModel> result = new ArrayList<>();

        //这里这么写，主要是为了解决一个bug
        //如果当前在D:/apache/tomcat目录，当执行 dir D:时看到的不是D盘根路径下的文件，而是D:/apache/tomcat里面的内容
        //这里通过执行 dir D:\. 来规避这个bug
        if(dir.matches("^[a-zA-Z]:$")){
            dir = dir + "\\.";
        }
        String records[] = CommandExecutorFactory.getInstance().exec("dir \"" + dir + "\"").getResponseResult().replaceAll("\r","").split("\r\n|\n");

        for(String record : records){
            if(record.indexOf(":") > 0 && record.indexOf("/") > 0){
                // 这里要加 limit 4，因为有些文件夹或者文件名中含有空格，如果不加这个限制，拿到的值就可能存在问题
                String[] strs = record.split("\\s+",4);
                if(strs[3].equalsIgnoreCase(".") || strs[3].equalsIgnoreCase("..")){
                    continue;
                }

                if(strs[2].equalsIgnoreCase("<DIR>")){
                    result.add(new FileModel(strs[3],"文件夹", "", strs[0] + " " + strs[1]));
                }else{
                    result.add(new FileModel(strs[3],"文件", strs[2], strs[0] + " " + strs[1]));
                }
            }
        }

        return result;
    }

    private static List<FileModel> getFileEntryForLinux(String dir) {
        List<FileModel> result = new ArrayList<>();
        String records[] = CommandExecutorFactory.getInstance().exec("ls -al '" + dir + "'").getResponseResult().split("\\r\\n|\\r|\\n");

        for(String record : records){
            // 任何一个 entry 都应该至少有一个r或者x权限才对
            if(record.indexOf("r") > 0 || record.indexOf("x") > 0){
                String[] strs = record.split("\\s+",9);
                if(strs[8].equalsIgnoreCase(".") || strs[8].equalsIgnoreCase("..")){
                    continue;
                }

                if(strs[0].startsWith("d")){
                    result.add(new FileModel(strs[8],"文件夹", "", strs[5] + " " + strs[6] + " " + strs[7]));
                }else{
                    result.add(new FileModel(strs[8],"文件", strs[4], strs[5] + " " + strs[6] + " " + strs[7]));
                }
            }
        }

        return result;
    }
}