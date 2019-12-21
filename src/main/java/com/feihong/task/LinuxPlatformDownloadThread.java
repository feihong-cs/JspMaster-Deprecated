package com.feihong.task;

import com.feihong.executor.CommandExecutorFactory;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class LinuxPlatformDownloadThread implements Runnable{
    private int partNum;
    private int blockSize;
    private String serverPath;
    private int totalSize;
    private List<Runnable> threadPool;
    private String localPath;
    private int retry;


    public LinuxPlatformDownloadThread(String serverPath, int partNum, int blockSize, String localPath, int totalSize, List<Runnable> threadPool){
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
            String cmd = "xxd -l " + blockSize + " -s +" + offset + " '" + serverPath + "'";
            String content = CommandExecutorFactory.getInstance().exec(cmd).getResponseResult();

            String[] strs = content.split("\\r\\n|\\r|\\n");
            List<Byte> list = new ArrayList<>();
            for(String str : strs){
                // 消除空行
                if(str.equals("")){
                    continue;
                }

                //提取中间那段 16 进制字符
                str = str.substring(10,49);
                String[] strs_inner = str.split("\\s+");
                for(String str_inner : strs_inner){
                    if(str_inner.length() == 4){
                        int a = Integer.parseInt(str_inner.substring(0,2), 16);
                        list.add((byte) a);
                        int b = Integer.parseInt(str_inner.substring(2), 16);
                        list.add((byte) b);
                    }else{
                        int a = Integer.parseInt(str_inner, 16);
                        list.add((byte) a);
                    }
                }
            }

            // 将 list<Byte> 转换为 byte[]
            // 又循环一次，感觉不太好
            int i = 0;
            byte[] bytes = new byte[list.size()];
            Iterator<Byte> iterator = list.iterator();
            while (iterator.hasNext()) {
                bytes[i++] = iterator.next();
            }

            try {
                RandomAccessFile file = new RandomAccessFile(localPath, "rw");
                file.setLength(totalSize);
                file.seek(offset);
                file.write(bytes);
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
        LinuxPlatformDownloadThread that = (LinuxPlatformDownloadThread) o;
        return partNum == that.partNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partNum, blockSize, serverPath, totalSize, threadPool, localPath);
    }
}
