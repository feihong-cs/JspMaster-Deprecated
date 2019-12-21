package com.feihong.bean;

import javafx.beans.property.SimpleStringProperty;

public class BasicInfo {
    private SimpleStringProperty type;
    private SimpleStringProperty value;

    public BasicInfo(String type, String value) {
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleStringProperty(value);
    }

    public BasicInfo(){

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

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
