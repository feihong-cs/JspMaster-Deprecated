package com.feihong.util;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.executor.CommandExecutor;
import com.feihong.executor.CommandExecutorFactory;

import java.io.IOException;

public class ConnectionUtil {

    public static String getConnectionStatus() throws IllegalAccessException, IOException, InstantiationException {
        String result = "";

        CommandExecutionResult commandExecutionResult1 = CommandExecutorFactory.getInstance().exec("ipconfig");
        CommandExecutionResult commandExecutionResult2 = CommandExecutorFactory.getInstance().exec("ifconfig");
        String result1 = commandExecutionResult1.getResponseResult();
        String result2 = commandExecutionResult2.getResponseResult();

        if(commandExecutionResult1.getResponseStatusCode() == 404 || commandExecutionResult2.getResponseStatusCode() == 404){
            BasicSetting.getInstance().isConnected = false;
            result = "连接失败，文件不存在!";
        }else if(commandExecutionResult1.getResponseStatusCode() == 200 || commandExecutionResult2.getResponseStatusCode() == 200){
            if(commandExecutionResult1.getException() == null && commandExecutionResult2.getException() == null) {
                if(result1 != null && result1.contains("Windows")){
                    BasicSetting.getInstance().isConnected = true;
                    result = "连接成功!";
                    BasicSetting.getInstance().shellPlatform = "windows";
                    BasicSetting.getInstance().fileSeprator = "\\";
                }else if(result2 != null && result2.contains("flags")){
                    BasicSetting.getInstance().isConnected = true;
                    result = "连接成功!";
                    BasicSetting.getInstance().shellPlatform = "linux";
                    BasicSetting.getInstance().fileSeprator = "/";
                }else{
                    BasicSetting.getInstance().isConnected = false;
                    result = "连接失败，未获得有效的执行结果，可能是因为使用了错误的通信密钥！";
                }
            }else {
                String exception1 = commandExecutionResult1.getException();
                String exception2 = commandExecutionResult2.getException();

                if (exception1.contains("ConnectException") || exception2.contains("ConnectException")) {
                    BasicSetting.getInstance().isConnected = false;
                    result = "连接失败，服务器拒绝访问！";
                } else if (exception1.contains("SocketTimeoutException") || exception2.contains("SocketTimeoutException")) {
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
