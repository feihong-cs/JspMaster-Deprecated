package com.feihong.util;

import com.feihong.bean.CommandExecutionResult;

public class WrappedHttpRequest {

    public static CommandExecutionResult post(String url, String body){
        boolean b = BasicSetting.getInstance().encrypt;
        if(BasicSetting.getInstance().encrypt && body != null && !body.trim().equals("")){
            String encryptedCmd = EncryptUtil.encrypt(body, BasicSetting.getInstance().encryptKey.getBytes(), BasicSetting.getInstance().iv.getBytes());
            CommandExecutionResult result = HttpConnectionUtil.post(encryptedCmd);
            String enecryptedResult = result.getResponseResult();
            if(result.getException() == null){
                String decrypt = EncryptUtil.decrypt(enecryptedResult, BasicSetting.getInstance().encryptKey.getBytes(), BasicSetting.getInstance().iv.getBytes());
                result.setResponseResult(decrypt);
            }

            return result;
        }

        return HttpConnectionUtil.post(url, body);
    }

    public static CommandExecutionResult post(String body){
        boolean b = BasicSetting.getInstance().encrypt;
        if(BasicSetting.getInstance().encrypt && body != null && !body.trim().equals("")){
            String encryptedCmd = EncryptUtil.encrypt(body, BasicSetting.getInstance().encryptKey.getBytes(), BasicSetting.getInstance().iv.getBytes());
            CommandExecutionResult result = HttpConnectionUtil.post(encryptedCmd);
            String enecryptedResult = result.getResponseResult();
            if(result.getException() == null){
                String decrypt = EncryptUtil.decrypt(enecryptedResult, BasicSetting.getInstance().encryptKey.getBytes(), BasicSetting.getInstance().iv.getBytes());
                result.setResponseResult(decrypt);
            }

            return result;
        }

        return HttpConnectionUtil.post(BasicSetting.getInstance().shellUrl, body);
    }
}
