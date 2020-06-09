package com.feihong.util;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.executor.CommandExecutorFactory;

public class ConnectionUtil {

    public static String getConnectionStatus() {
        String result = "";

        CommandExecutionResult windowsResult = CommandExecutorFactory.getInstance().exec("ipconfig");
        CommandExecutionResult linuxResult = CommandExecutorFactory.getInstance().exec("ifconfig");

        if(windowsResult.getResponseStatusCode() == 404 || linuxResult.getResponseStatusCode() == 404){
            BasicSetting.getInstance().isConnected = false;
            result = "连接失败，文件不存在!";
        }else if(windowsResult.getResponseStatusCode() == 200 || linuxResult.getResponseStatusCode() == 200){
            if(windowsResult.getException() == null && linuxResult.getException() == null) {
                if(windowsResult.getResponseResult() != null && windowsResult.getResponseResult().contains("Windows")){
                    BasicSetting.getInstance().isConnected = true;
                    result = "连接成功!";
                    BasicSetting.getInstance().shellPlatform = "windows";
                    BasicSetting.getInstance().fileSeprator = "\\";
                }else if(linuxResult.getResponseResult() != null && linuxResult.getResponseResult().contains("flags")){
                    BasicSetting.getInstance().isConnected = true;
                    result = "连接成功!";
                    BasicSetting.getInstance().shellPlatform = "linux";
                    BasicSetting.getInstance().fileSeprator = "/";
                }else{
                    BasicSetting.getInstance().isConnected = false;
                    result = "连接失败，未获得有效的执行结果，可能是因为使用了错误的通信密钥！";
                }
            }else {

                if (windowsResult.getException().contains("ConnectException") || linuxResult.getException().contains("ConnectException")) {
                    BasicSetting.getInstance().isConnected = false;
                    result = "连接失败，服务器拒绝访问！";
                } else if (windowsResult.getException().contains("SocketTimeoutException") || linuxResult.getException().contains("SocketTimeoutException")) {
                    BasicSetting.getInstance().isConnected = false;
                    result = "连接失败，连接超时！";
                } else {
                    BasicSetting.getInstance().isConnected = false;
                    result = "出问题了，连接失败！";
                }
            }
        }else{
            BasicSetting.getInstance().isConnected = false;
            result = "连接异常！";
        }


        return result;
    }
}
