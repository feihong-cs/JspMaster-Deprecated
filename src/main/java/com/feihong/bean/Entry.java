package com.feihong.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

public class Entry {
    private Label name;
    private SimpleStringProperty date;
    private SimpleStringProperty type;
    private SimpleStringProperty size;
    private TreeItem parent;

    public TreeItem getParent() {
        return parent;
    }

    public void setParent(TreeItem parent) {
        this.parent = parent;
    }

    public Entry(Label name, String date, String type, String size, TreeItem parent){
        this.name = name;
        this.date = new SimpleStringProperty(date);
        this.type = new SimpleStringProperty(type);
        this.size = new SimpleStringProperty(size);
        this.parent = parent;
    }

    public Label getName() {
        return name;
    }

    public void setName(Label name) {
        this.name = name;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getSize() {
        return size.get();
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }
}
