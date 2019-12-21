package com.feihong.util;

import com.feihong.bean.ShellEntry;
import com.feihong.executor.CommandExecutor;
import java.io.File;
import java.util.*;

public class BasicSetting {
    public String shellUrl;
    public String shellPwd;
    public String shellType;
    public String shellPlatform;
    public Boolean isConnected = false;
    public boolean encrypt;
    public Map<String, String> headers;
    public String fileSeprator;
    public String dbFile;
    public String encryptKey;
    public String iv;
    public Map<String, String> shells;

    private static BasicSetting basicSetting = new BasicSetting();

    private BasicSetting(){
//        dbFile = this.getClass().getResource("/").getPath() + "database/data.db";
        dbFile = System.getProperty("user.dir") + "/database/data.db";
        encryptKey = ConfigUtil.getCommunicationKey();
        shells = new HashMap<>();

        List<String> list = BasicUtil.getClassName();

        //把 plugin 目录的内容也加入进来
        String path = System.getProperty("user.dir") + "/plugin/";
        BasicUtil.findStringList(new File(path), list);

        for(String fileName : list){
            if(!fileName.endsWith(".class")){
                continue;
            }

            fileName = fileName.substring(0, fileName.indexOf("."));

            if(!fileName.equalsIgnoreCase("CommandExecutor") && !fileName.equalsIgnoreCase("CommandExecutorFactory")){
                String className = "com.feihong.executor." + fileName;

                try {
                    MyClassLoader classLoader = new MyClassLoader();
                    Class clazz = classLoader.findClass(className);
                    if(clazz == null){
                        continue;
                    }
                    CommandExecutor commandExecutor = (CommandExecutor) clazz.newInstance();
                    shells.put(commandExecutor.getName(), className);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static BasicSetting getInstance(){
        return basicSetting;
    }

    public void initialize(ShellEntry entry){
        basicSetting.shellUrl = entry.getUrl();
        basicSetting.shellPwd = entry.getPassword();
        basicSetting.shellType = entry.getType();
        basicSetting.encrypt = entry.getIsEncrypt() ==1 ? true : false;
        basicSetting.headers = entry.getHeaders();

        if(entry.getEncryptKey() != null && !entry.getEncryptKey().trim().equals("")){
            basicSetting.encryptKey = entry.getEncryptKey();
        }else{
            basicSetting.encryptKey = ConfigUtil.getCommunicationKey();
        }

        if(entry.getIV() != null && !entry.getIV().trim().equals("")){
            basicSetting.iv = entry.getIV();
        }else{
            String iv = ConfigUtil.getIV();
            basicSetting.iv = ConfigUtil.getIV();
        }
    }

}
