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
    protected Integer call() {
        try{
            BasicSetting.getInstance().initialize(entry);
            this.result = ConnectionUtil.getConnectionStatus();
            System.out.println("Info: ConnectionUtil.getConnectionStatus(): " + this.result);
            if(result.equals("连接成功!")){
                System.out.println("Info: match");
                mainUI.initialize(result);
                this.status = 1;
            }else{
                System.out.println("Info: not match");
                this.status = -1;
            }
        }catch(Exception e){
            //尝试解决 Issues1，怀疑是哪里抛出了异常，导致的bug
            System.out.println("Info: Exception occured");
            e.printStackTrace();
            this.status = -1;
        }

        return 1;
    }
}
//参考：https://blog.csdn.net/loongshawn/article/details/52996382