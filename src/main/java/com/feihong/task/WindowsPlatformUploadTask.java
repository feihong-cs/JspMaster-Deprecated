package com.feihong.task;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.executor.CommandExecutorFactory;
import javafx.concurrent.Task;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class WindowsPlatformUploadTask extends Task<Boolean> {
    private String serverPath;
    private String localPath;
    private int threadNum;
    private int blocksize;

    public WindowsPlatformUploadTask(String serverPath, String localPath, int threadNum, int blocksize){
        this.serverPath = serverPath;
        this.localPath = localPath;
        this.threadNum = threadNum;
        this.blocksize = blocksize;
    }

    @Override
    protected Boolean call() throws Exception {
        long time1 = System.currentTimeMillis();

        String cmd = "del \"" + serverPath + "\"";
        CommandExecutorFactory.getInstance().exec(cmd);

        FileInputStream in = new FileInputStream(localPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while((len = in.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        in.close();

        byte[] bytes = Base64.getDecoder().decode(baos.toByteArray());
        baos.close();
        StringBuffer sb = new StringBuffer(new String(bytes));
        int length = sb.length();
        System.out.println("文件大小：" + length);

        int totalPart = (int) Math.ceil((double) length / blocksize);
        System.out.println("Total part: " + totalPart);

        List<Integer> list = new ArrayList<>();
        for(int i =0;i < totalPart; i++){
            list.add(i);
        }

        List<Runnable> threadPool = new ArrayList<>();
        Iterator<Integer> it = list.iterator();
        List<Thread> runThreads = new ArrayList<>();
        int count = 0;
        while( !isCancelled() && it.hasNext()){
            if(threadPool.size() < threadNum){
                int partNum = it.next();
                Runnable runnable = new WindowsPlatformUploadThread(serverPath, partNum, blocksize, sb, length, threadPool, time1 + "");
                threadPool.add(runnable);
                Thread t = new Thread(runnable);
                t.start();
                runThreads.add(t);
                updateProgress(++count, totalPart+1);
            }
        }

        if(isCancelled()){
            //删除文件操作必须等所有线程结束，否则会出现删除了又出现的情况
            for(Thread t : runThreads)
                try{t.join();}catch(Throwable e){}

            System.out.println("Abort...");
            cmd = "del tmp" + time1 + "*";
            CommandExecutorFactory.getInstance().exec(cmd);
            return true;
        }

        for(Thread t : runThreads)
            try{t.join();}catch(Throwable e){}

        cmd = "powershell -nop -ep bypass -c \"$Files=Get-ChildItem -Path .\\* -Include " +
                "tmp" + time1 + "-* -Name | sort-object {[int]$_.split('-')[1]}; Get-Content $Files | Set-Content tmp" + time1 + "xyz;\"";
        CommandExecutionResult res = CommandExecutorFactory.getInstance().exec(cmd);


        //使用 powershell 重组文件的时候耗时较长，大于 2MB 以上的文件即会超过 5s，此时命令执行超时，会继续执行下面的 del 语句，导致在 powershell 尚未执行完毕的情况下，临时小文件被删除，重组文件的 powershell 线程会卡死
        //为了避免这个问题，开始上传时，会删除 serverPath 文件，然后在执行 powershell 语句过后，循环判断指定路径的文件是否存在，如果存在，说明文件重组成功
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //加入 isCancelled 判断，防止重组的文件不正确，大小计算不正确，根据大小判断的跳出条件不满足，造成死循环
                while (!isCancelled()) {
                    try{
                        String result = CommandExecutorFactory.getInstance().exec("dir tmp" + time1 + "xyz").getResponseResult();
                        String size = "";
                        for(String str : result.split("\r\n")){
                            if(str.indexOf(":")>0 && str.indexOf("tmp" + time1 + "xyz") > 0){
                                size = str.split("\\s+", 4)[2];
                                size = size.replaceAll(",","");
                                break;
                            }
                        }

                        System.out.println(Integer.parseInt(size) + "==" + (length  + totalPart * 2));
                        //根据实际情况计算的，非是通过理论计算得到的
                        if(Integer.parseInt(size) == (length  + totalPart * 2)){
                            break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                if(isCancelled()){
                    System.out.println("Abort...");
                    String cmd = "del tmp" + time1 + "*";
                    CommandExecutorFactory.getInstance().exec(cmd);
                }
            }
        });
        t.start();
        t.join();

        System.out.println("continue...");

        cmd = "powershell -nop -ep bypass -c \"$dest='tmp" + time1 + "xyz';$content = (Get-Content $dest) -join" +
                " '';$bytes  = [System.Convert]::FromBase64String($content);[System.IO.File]::WriteAllBytes" +
                "('" + serverPath  + "',$bytes)\"";
        CommandExecutorFactory.getInstance().exec(cmd);

        //防止目标文件（serverPath）被创建出来之前，重组的 tmp 文件被删除了
        final boolean[] flag = {true};
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag[0]) {
                    String result = CommandExecutorFactory.getInstance().exec("dir \"" + serverPath + "\"").getResponseResult();
                    for(String str : result.split("\r\n")){
                        if(str.indexOf(":") > 0 && str.indexOf(new File(serverPath).getName()) > 0){
                            flag[0] = false;
                            break;
                        }
                    }
                }
            }
        });
        t2.start();
        t2.join();

        cmd = "del tmp" + time1 + "*";
        CommandExecutorFactory.getInstance().exec(cmd);

        updateProgress(1.0,1.0);
        System.out.println("上传完毕");

        long time2 = System.currentTimeMillis();
        System.out.println("耗时：" + (time2 - time1)/1000.0 + "s");
        return true;
    }
}