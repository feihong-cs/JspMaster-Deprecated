package com.feihong.bean;

import java.util.HashMap;
import java.util.Map;

public class ShellEntry {
    private int id = 0;
    private String url = "";
    private String password = "";
    private String type = "";
    private String createTime = "";
    private String lastvisitTime = "";
    private String remarks = "";
    private Map<String, String> headers = new HashMap<>();
    private int isEncrypt = 1;
    private String encryptKey = "";
    private String iv = "";

    public ShellEntry(){}

    public ShellEntry(int id, String url, String password, String type, String createTime, String lastvisitTime,
                      String remarks, Map<String, String> headers, int isEncrypt, String encryptKey, String iv) {
        this.id = id;
        this.url = url;
        this.password = password;
        this.type = type;
        this.createTime = createTime;
        this.lastvisitTime = lastvisitTime;
        this.remarks = remarks;
        this.headers = headers;
        this.isEncrypt = isEncrypt;
        this.encryptKey = encryptKey;
        this.iv = iv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastvisitTime() {
        return lastvisitTime;
    }

    public void setLastvisitTime(String lastvisitTime) {
        this.lastvisitTime = lastvisitTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(int isEncrypt) {
        this.isEncrypt = isEncrypt;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getIV() {
        return iv;
    }

    public void setIV(String iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "ShellEntry{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", createTime='" + createTime + '\'' +
                ", lastvisitTime='" + lastvisitTime + '\'' +
                ", remarks='" + remarks + '\'' +
                ", headers=" + headers +
                ", isEncrypt=" + isEncrypt +
                ", encryptKey='" + encryptKey + '\'' +
                ", iv='" + iv + '\'' +
                '}';
    }
}
