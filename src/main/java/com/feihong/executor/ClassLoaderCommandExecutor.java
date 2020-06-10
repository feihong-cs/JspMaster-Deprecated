package com.feihong.executor;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.ConfigUtil;
import com.feihong.util.WrappedHttpRequest;

public class ClassLoaderCommandExecutor implements CommandExecutor {

    @Override
    public String getName() {
        return "URLClassLoader";
    }

    @Override
    public CommandExecutionResult exec(String command) {
        String wrappedCommand;
        if(command.equalsIgnoreCase("ipconfig") || command.trim().equalsIgnoreCase("ifconfig")){
            wrappedCommand = command;
        }else{
            if(BasicSetting.getInstance().shellPlatform.trim().equalsIgnoreCase("windows")){
                wrappedCommand =  "cmd.exe,/c," + command;
            }else{
                wrappedCommand = "/bin/bash,-c," + command;
            }
        }

        if(BasicSetting.getInstance().encrypt){
            return WrappedHttpRequest.post(BasicSetting.getInstance().shellUrl + "?key=" + ConfigUtil.getCommunicationKey() + "&iv=" + ConfigUtil.getIV(), wrappedCommand);
        }else{
            return WrappedHttpRequest.post(wrappedCommand);
        }

    }
}