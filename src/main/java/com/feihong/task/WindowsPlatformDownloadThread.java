package com.feihong.task;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.executor.CommandExecutorFactory;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Objects;

public class WindowsPlatformDownloadThread implements Runnable{
    private int partNum;
    private int blockSize;
    private String serverPath;
    private int totalSize;
    private List<Runnable> threadPool;
    private String localPath;
    private int retry;


    public WindowsPlatformDownloadThread(String serverPath, int partNum, int blockSize, String localPath, int totalSize, List<Runnable> threadPool){
        this.serverPath = serverPath;
        this.partNum = partNum;
        this.blockSize = blockSize;
        this.totalSize = totalSize;
        this.threadPool = threadPool;
        this.localPath = localPath;
    }

    @Override
    public void run() {
        boolean flag = true;
        int offset = partNum * blockSize;

        //增加 flag 是为了当线程下载失败时，让其重新尝试再次下载
        while(flag) {
            int windowSize = (offset + blockSize) < totalSize ? blockSize : totalSize - offset;
            String fethChuck = "powershell -nop -w hidden -ep bypass -c \"[System.Convert]::ToBase64String([System.IO" +
                    ".File]::ReadAllBytes('" + serverPath + "')).substring(" + offset + "," + windowSize + ")\"";
            CommandExecutionResult result = CommandExecutorFactory.getInstance().exec(fethChuck);
            String chunk = result.getResponseResult();

            try {
                RandomAccessFile file = new RandomAccessFile(localPath + ".tmp", "rw");
                file.setLength(totalSize);
                file.seek(offset);
                file.write(chunk.getBytes());
                file.close();
                flag = false;
            } catch (NullPointerException | IOException e) {
                //增加跳出机制，防止死循环
                retry++;
                if(retry >= 5){
                    System.out.println("Thread task failed!!!");
                    break;
                }
                System.out.println("retrying...");
            }
        }

        threadPool.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowsPlatformDownloadThread that = (WindowsPlatformDownloadThread) o;
        return partNum == that.partNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partNum);
    }
}
