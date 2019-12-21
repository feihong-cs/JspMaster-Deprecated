package com.feihong.task;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.executor.CommandExecutor;
import com.feihong.executor.CommandExecutorFactory;
import java.util.List;
import java.util.Objects;

public class LinuxPlatformUploadThread implements Runnable{
    final private int partNum;
    private int blockSize;
    private String serverPath;
    private int totalSize;
    private List<Runnable> threadPool;
    private StringBuffer sb;
    private CommandExecutor executor;
    private String prefix;
    private int retry;


    public LinuxPlatformUploadThread(String serverPath, int partNum, int blockSize, StringBuffer sb, int totalSize, List<Runnable> threadPool, String prefix){
        this.serverPath = serverPath;
        this.partNum = partNum;
        this.blockSize = blockSize;
        this.totalSize = totalSize;
        this.threadPool = threadPool;
        this.sb = sb;
        this.executor = CommandExecutorFactory.getInstance();
        this.prefix = prefix;
    }

    @Override
    public void run() {

        boolean flag = true;
        int offset = partNum * blockSize;
        int windowSize = (offset + blockSize < totalSize) ? blockSize : (totalSize - offset);
        //增加 flag 是为了当线程失败时，让其再次尝试
        while(flag) {
            try{
                String path = "/tmp/tmp" + prefix + "-" + partNum;;
                String cmd = "python -c \"f=open('" + path + "','w+')\n" +
                "f.write('" + sb.substring(offset, offset + windowSize) + "')\n" +
                "f.close()\"";
                CommandExecutionResult result = executor.exec(cmd);
                flag = false;
            }catch(Exception e){
                //增加跳出机制，防止死循环
                retry++;
                if(retry >= 5){
                    System.out.println("Thread task failed!!!");
                    break;
                }
                System.out.println("retrying...");
            }
        }

        boolean res = threadPool.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinuxPlatformUploadThread that = (LinuxPlatformUploadThread) o;
        return partNum == that.partNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partNum, blockSize, serverPath, totalSize, threadPool, sb, executor);
    }
}
