package com.feihong.task;

import com.feihong.bean.ShellEntry;
import com.feihong.ui.MainUI;
import com.feihong.util.BasicSetting;
import com.feihong.util.ConnectionUtil;
import javafx.concurrent.Task;

public class OpenMainUITask extends Task<Integer> {
    private MainUI mainUI;
    private ShellEntry entry;
    private String result;
    private int status;

    public OpenMainUITask(MainUI mainUI, ShellEntry entry){
        this.mainUI = mainUI;
        this.entry = entry;
    }

    public String getResult() {
        return result;
    }

    public int getStatus() {
        return status;
    }

    @Override
    protected Integer call() throws Exception {
        try{
            BasicSetting.getInstance().initialize(entry);
            String key = BasicSetting.getInstance().encryptKey;
            String iv = BasicSetting.getInstance().iv;
            this.result = ConnectionUtil.getConnectionStatus();
            if(result.equals("连接成功!")){
                mainUI.initialize(result);
                this.status = 1;
            }else{
                this.status = -1;
            }
        }catch(Exception e){
            this.status = -1;
        }

        return 1;
    }
}
//参考：https://blog.csdn.net/loongshawn/article/details/52996382