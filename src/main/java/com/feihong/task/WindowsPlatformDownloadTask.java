package com.feihong.task;

import com.feihong.executor.CommandExecutorFactory;
import com.feihong.util.BasicUtil;
import javafx.concurrent.Task;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class WindowsPlatformDownloadTask extends Task<Boolean> {
    private String serverPath;
    private String localPath;
    private int threadNum;
    private int blocksize;

    public WindowsPlatformDownloadTask(String serverPath, String localPath, int threadNum, int blocksize){
        this.serverPath = serverPath;
        this.localPath = localPath;
        this.threadNum = threadNum;
        this.blocksize = blocksize;
    }

    @Override
    protected Boolean call() throws Exception {
        long time1 = System.currentTimeMillis();

        String cmd = "powershell -nop -w hidden -ep bypass -c \"[System.Convert]::ToBase64String([System.IO.File]::ReadAllBytes('" + serverPath + "')).Length\"";
        String size = CommandExecutorFactory.getInstance().exec(cmd).getResponseResult();
        int length = Integer.parseInt(size);
        System.out.println("Total size: " + length);

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
        while( !isCancelled() && it.hasNext()){ ;
            if(threadPool.size() < threadNum){
                int partNum = it.next();
                Runnable runnable = new WindowsPlatformDownloadThread(serverPath, partNum, blocksize, localPath, length, threadPool);
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

            new File(localPath + ".tmp").delete();
            return true;
        }


        for(Thread t : runThreads)
            try{t.join();}catch(Throwable e){}

        Base64.Decoder decoder = Base64.getDecoder();
        FileOutputStream fos = new FileOutputStream(localPath);
        fos.write(decoder.decode(BasicUtil.getContent(localPath + ".tmp")));
        fos.close();

        new File(localPath +".tmp").delete();

        updateProgress(1.0,1.0);
        System.out.println("下载完毕");

        long time2 = System.currentTimeMillis();
        System.out.println("耗时：" + (time2 - time1)/1000.0 + "s");
        return true;
    }
}
