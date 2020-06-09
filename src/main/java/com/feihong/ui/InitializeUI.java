package com.feihong.ui;

import com.feihong.task.InitializeTask;
import com.feihong.util.ConfigUtil;
import com.feihong.util.PasswordUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InitializeUI {
    private Stage stageA;
    private Stage stageB;
    private PasswordField passA;
    private PasswordField passB;
    private CheckBox check;
    private Tooltip tooltip1;
    private Tooltip tooptip2;
    private Tooltip errortip;
    private String communicationKey;
    private String encryptKey;

    public void show(){
        initializeStageA();
        initializeStageB();
        stageA.show();
    }

    public void initializeStageA(){
        tooltip1 = new Tooltip("客户端管理程序会将您输入的密码和当前时间戳组合，将得到的字符串的哈希值作为客户端管理程序和 JSP shell 之间通信的加密密钥, 以确保您和您的 JSP shell 之间通信的私密性");
        tooltip1.setPrefWidth(250);
        tooltip1.setWrapText(true);

        errortip = new Tooltip("密码不能为空");

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        HBox hbox1 = new HBox();
        Label label = new Label("设置通信密钥");
        passA = new PasswordField();


        passA.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                errortip.hide();
                if(newValue){
                    Point2D p = passA.localToScene(0.0, 0.0);
                    tooltip1.show(passA,
                            p.getX() + passA.getScene().getX() + passA.getScene().getWindow().getX() + passA.getWidth() + 2,
                            p.getY() + passA.getScene().getY() + passA.getScene().getWindow().getY());
                }
            }
        });

        passA.textProperty().addListener((observable, oldValue, newValue) -> {
            if(passA.getText() == null || passA.getText().trim().equals("")){
                Point2D p = passA.localToScene(0.0, 0.0);
                tooltip1.show(passA,
                        p.getX() + passA.getScene().getX() + passA.getScene().getWindow().getX() + passA.getWidth() + 2,
                        p.getY() + passA.getScene().getY() + passA.getScene().getWindow().getY());
            }else{
                tooltip1.hide();
            }
        });

        hbox1.getChildren().addAll(label, passA);
        hbox1.setMargin(label, new Insets(10,10,10,0));
        hbox1.setMargin(passA, new Insets(10,0,10,0));
        hbox1.setAlignment(Pos.CENTER);

        HBox hbox2 = new HBox();
        Button next = new Button("下一步");
        next.setPrefWidth(100);
        hbox2.getChildren().addAll(next);
        hbox2.setAlignment(Pos.CENTER);

        next.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(passA.getText() == null || passA.getText().trim().equals("")){
                    Point2D p = next.localToScene(0.0, 0.0);
                    errortip.show(next,
                            p.getX() + next.getScene().getX() + next.getScene().getWindow().getX(),
                            p.getY() + next.getScene().getY() + next.getScene().getWindow().getY() + next.getHeight());
                }else{
                    communicationKey = passA.getText().trim();
                    tooltip1.hide();
                    errortip.hide();
                    stageA.close();
                    stageB.show();
                    passB.setText(encryptKey);
                }
            }
        });

        vbox.getChildren().addAll(hbox1,hbox2);
        vbox.setAlignment(Pos.CENTER);

        stageA = new Stage();
        stageA.setTitle("初始化");
        stageA.setScene(new Scene(vbox, 380 , 150));
    }

    public void initializeStageB(){

        tooptip2 = new Tooltip("若设置了登录密码，每当您打开客户端管理程序时，必须正确输入此密码。若勾选不使用登录密码，则登录时无需输入密码。");
        tooptip2.setPrefWidth(250);
        tooptip2.setWrapText(true);

        errortip = new Tooltip("登录密码不能为空");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        HBox hbox1 = new HBox();
        Label label = new Label("设置登录密码");
        passB = new PasswordField();
        hbox1.getChildren().addAll(label, passB);
        hbox1.setMargin(label, new Insets(10,10,10,0));
        hbox1.setMargin(passB, new Insets(10,0,10,0));
        hbox1.setAlignment(Pos.CENTER);


        passB.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                errortip.hide();
                if(newValue){
                    Point2D p = passB.localToScene(0.0, 0.0);
                    tooptip2.show(passB,
                            p.getX() + passB.getScene().getX() + passB.getScene().getWindow().getX() + passB.getWidth() + 2,
                            p.getY() + passB.getScene().getY() + passB.getScene().getWindow().getY());
                }
            }
        });

        passB.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(passB.getText() == null || passB.getText().trim().equals("")){
                    Point2D p = passB.localToScene(0.0, 0.0);
                    tooptip2.show(passB,
                            p.getX() + passB.getScene().getX() + passB.getScene().getWindow().getX() + passB.getWidth() + 2,
                            p.getY() + passB.getScene().getY() + passB.getScene().getWindow().getY());
                }else{
                    tooptip2.hide();
                }
            }
        });

        HBox hbox2 = new HBox();
        Label lable = new Label("不使用登录密码");
        check = new CheckBox();
        hbox2.setAlignment(Pos.CENTER);
        hbox2.getChildren().addAll(lable, check);
        hbox2.setMargin(lable, new Insets(0,10,0,0));

        check.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                errortip.hide();
                tooptip2.hide();
                if(check.isSelected()){
                    passB.setEditable(false);
                    passB.setStyle("-fx-control-inner-background: gray;");
                }else{
                    passB.setEditable(true);
                    passB.setStyle("-fx-control-inner-background: white;");
                }
            }
        });

        HBox hbox3 = new HBox();
        Button previous = new Button("上一步");
        previous.setPrefWidth(100);
        Button finish = new Button("完成");
        finish.setPrefWidth(100);
        hbox3.getChildren().addAll(previous, finish);
        hbox3.setMargin(previous, new Insets(10,10,0,0));
        hbox3.setMargin(finish, new Insets(10,0,0,0));
        hbox3.setAlignment(Pos.CENTER);

        previous.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                encryptKey = passB.getText().trim();
                tooptip2.hide();
                errortip.hide();
                stageB.close();
                stageA.show();
                passA.setText(communicationKey);
            }
        });

        finish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!check.isSelected() && (passB.getText() == null || passB.getText().trim().equals(""))){
                    Point2D p = finish.localToScene(0.0, 0.0);
                    errortip.show(finish,
                            p.getX() + finish.getScene().getX() + finish.getScene().getWindow().getX(),
                            p.getY() + finish.getScene().getY() + finish.getScene().getWindow().getY() + finish.getHeight());
                    return;
                }
                if(check.isSelected()){
                    ConfigUtil.setEncrypt(false);
                }else{
                    ConfigUtil.setEncrypt(true);
                    PasswordUtil.password = passB.getText().trim();
                }

                InitializeTask task = new InitializeTask(passA.getText().trim() + System.currentTimeMillis(), !check.isSelected());
                task.valueProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                        Boolean newValue) {
                        if(task.getStatus() == 1){
                            stageB.close();

                            if(ConfigUtil.isEncrypt()){
                                Stage primaryStage = new Stage();
                                VBox vBox = LoginUI.getPane();
                                primaryStage.setTitle("打开JspMaster");
                                primaryStage.setScene(new Scene(vBox, 380, 150));
                                primaryStage.show();
                            }else{
                                Stage newStage = new Stage();
                                BorderPane borderPane = new ShellManagerPane().getPane();
                                newStage.setTitle("JspMaster v1.01     Written  by 飞鸿");
                                newStage.setScene(new Scene(borderPane, 1150, 500));
                                newStage.show();
                            }
                        }
                    }
                });

                PenddingUI penddingUI = new PenddingUI(task, stageB);
                penddingUI.activateProgressBar();
            }
        });

        vbox.getChildren().addAll(hbox1,hbox2,hbox3);
        vbox.setAlignment(Pos.CENTER);

        stageB = new Stage();
        stageB.setTitle("初始化");
        stageB.setScene(new Scene(vbox, 380 , 200));
    }
}