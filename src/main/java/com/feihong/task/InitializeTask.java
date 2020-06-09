package com.feihong.task;

import com.feihong.util.*;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class InitializeTask extends Task<Boolean> {
    private String key;
    private int status;
    private boolean encrypt;

    public int getStatus(){
        return this.status;
    }

    public InitializeTask(String key, boolean encrypt){
        this.key = key;
        this.encrypt = encrypt;
    }

    @Override
    protected Boolean call() {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(key.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            String key = new String(buf).substring(0, 32);
            String iv = new StringBuffer(new String(buf)).reverse().toString().substring(0,16);
            ConfigUtil.setCommnunicationKey(key);
            ConfigUtil.setIV(iv);

            String path = System.getProperty("user.dir") + "/shell";
            File file = new File(path);
            List<File> files = new ArrayList<>();
            BasicUtil.findFileList(file, files);
            for (File f : files) {
                String fileContent = FileUtils.readFileToString(f, "UTF-8");
                fileContent = fileContent.replaceAll("\\[key_placeholder\\]", key);
                fileContent = fileContent.replaceAll("\\[iv_placeholder\\]", iv);
                FileUtils.writeStringToFile(f, fileContent, "UTF-8");
            }
        } catch (Exception e) {
            this.status = -1;
            e.printStackTrace();
        }

        //初始化的时候，如果选择使用登录密码，在这里需要对 dbFile 进行初始化的加密
        if (encrypt) {
            EncryptUtil.encryptFile(BasicSetting.getInstance().dbFile, PasswordUtil.password);
        }

        this.status = 1;
        return true;
    }
}
