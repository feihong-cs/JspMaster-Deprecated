package com.feihong.executor;

import com.feihong.util.BasicSetting;
import com.feihong.util.MyClassLoader;

public class CommandExecutorFactory {

    public static CommandExecutor getInstance(){
        try {
            String className = BasicSetting.getInstance().shells.get(BasicSetting.getInstance().shellType);
            MyClassLoader classLoader = new MyClassLoader();
            Class clazz = classLoader.findClass(className);

            return (CommandExecutor) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
