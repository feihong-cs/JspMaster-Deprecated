package com.feihong.executor;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.WrappedHttpRequest;

public class JNDICommandExecutor implements CommandExecutor {

    @Override
    public String getName() {
        return "JNDI";
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

        return WrappedHttpRequest.post(wrappedCommand);
    }
}