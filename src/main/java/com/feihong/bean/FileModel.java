package com.feihong.bean;

public class FileModel {
    public String name;
    public String type;
    public String size;
    public String date;

    @Override
    public String toString() {
        return "FileEntry{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public FileModel(String name, String type, String size, String date) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
