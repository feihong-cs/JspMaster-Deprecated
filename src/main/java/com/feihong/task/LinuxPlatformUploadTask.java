package com.feihong.task;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.executor.CommandExecutorFactory;
import javafx.concurrent.Task;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class LinuxPlatformUploadTask extends Task<Boolean> {
    private String serverPath;
    private String localPath;
    private int threadNum;
    private int blocksize;

    public LinuxPlatformUploadTask(String serverPath, String localPath, int threadNum, int blocksize){
        this.serverPath = serverPath;
        this.localPath = localPath;
        this.threadNum = threadNum;
        this.blocksize = blocksize;
    }

    @Override
    protected Boolean call() throws Exception {
        long time1 = System.currentTimeMillis();

        String cmd = "rm -rf '" + serverPath + "'";
        CommandExecutorFactory.getInstance().exec(cmd);

        FileInputStream in = new FileInputStream(localPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
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
                Runnable runnable = new LinuxPlatformUploadThread(serverPath, partNum, blocksize, sb, length, threadPool, time1 + "");
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

            cmd = "rm -rf /tmp/tmp" + time1 + "*";
            CommandExecutorFactory.getInstance().exec(cmd);

            return true;
        }

        for(Thread t : runThreads)
            try{t.join();}catch(Throwable e){}


        cmd = "python -c \"import base64;\n" +
                "import os;\n" +
                "fout=open('/tmp/tmp" + time1 + "xyz','ab+');\n" +
                "list = []\n" +
                "for file in os.listdir(r'/tmp'):\n" +
                "    if file.startswith('tmp" + time1 + "-'):\n" +
                "          list.append(file)\n" +
                "list.sort(key=lambda x:int(x.split('-')[1]))\n" +
                "for file in list:\n" +
                "    fin = open('/tmp/'+file, 'r');\n" +
                "    for line in fin.readlines():  \n" +
                "        fout.write(line.strip());\n" +
                "    fin.close();\n" +
                "fout.close();\n" +
                "fin2 = open(r'/tmp/tmp" + time1 + "xyz','r');\n" +
                "fout2 = open(r'" + serverPath + "','wb');\n" +
                "base64.decode(fin2,fout2);\n" +
                "fin2.close();\n" +
                "fout2.close();\"";
        CommandExecutionResult res = CommandExecutorFactory.getInstance().exec(cmd);
        cmd = "rm -rf /tmp/tmp" + time1 + "*";
        CommandExecutorFactory.getInstance().exec(cmd);

        updateProgress(1.0,1.0);
        System.out.println("上传完毕");

        long time2 = System.currentTimeMillis();
        System.out.println("耗时：" + (time2 - time1)/1000.0 + "s");
        return true;
    }

}
