package com.feihong.task;

import com.feihong.db.DBUtil;
import com.feihong.util.BasicSetting;
import com.feihong.util.EncryptUtil;
import com.feihong.util.PasswordUtil;
import javafx.concurrent.Task;

public class CheckLoginPassword extends Task<Integer> {
    private int status = 0;

    public int getStatus(){
        return status;
    }

    @Override
    protected Integer call() throws Exception {
        try{
            String name = BasicSetting.getInstance().dbFile;
            String pass = PasswordUtil.password;
            //打开时解密 sqlite 数据库文件
            EncryptUtil.decryptFile(BasicSetting.getInstance().dbFile, PasswordUtil.password);
            //调用这个方法主要是看会不会抛出异常。如果正常执行未抛出异常，说明解密成功，否则说明密码错误，解密失败
            DBUtil.queryAll();
            status = 1;
        }catch(Exception e) {
            if (e.getMessage().contains("file is not a database")) {
                status = -1;
            }
            status = -1;
        }

        return 1;
    }
}
//参考：https://blog.csdn.net/loongshawn/article/details/52996382