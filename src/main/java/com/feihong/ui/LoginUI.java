package com.feihong.ui;

import com.feihong.task.CheckLoginPassword;
import com.feihong.util.BasicSetting;
import com.feihong.util.EncryptUtil;
import com.feihong.util.PasswordUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

public class LoginUI {

    public static VBox getPane(){
        VBox vbox = new VBox();

        HBox hbox1 = new HBox();
        Label label = new Label("密码");
        PasswordField passwordField = new PasswordField();
        hbox1.getChildren().addAll(label, passwordField);
        hbox1.setMargin(label, new Insets(10,10,10,0));
        hbox1.setMargin(passwordField, new Insets(10,0,10,0));
        hbox1.setAlignment(Pos.CENTER);

        HBox hbox2 = new HBox();
        Button submit = new Button("登入");
        submit.setPrefWidth(100);
        Button cancel = new Button("取消");
        cancel.setPrefWidth(100);
        hbox2.getChildren().addAll(submit, cancel);
        hbox2.setMargin(submit, new Insets(10,10,10,0));
        hbox2.setMargin(cancel, new Insets(10,0,10,0));
        hbox2.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(hbox1,hbox2);
        vbox.setAlignment(Pos.CENTER);

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                submit.fire();
            }
        });

        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //参考：https://blog.csdn.net/loongshawn/article/details/52996382
                PasswordUtil.password = passwordField.getText();
                Stage currentStage = (Stage)vbox.getScene().getWindow();

                CheckLoginPassword task = new CheckLoginPassword();
                task.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue,
                                        Integer newValue) {
                        if(task.getStatus() == -1){
                            //登录密码输入错误，删除错误的 data.db
                            File file = new File(BasicSetting.getInstance().dbFile);
                            if(file.exists()){
                                file.delete();
                            }

                            PromptMessageUI.getAlert("密码错误","您输入的密码不正确!");
                            currentStage.close();
                        }else if(task.getStatus() == 1){
                            Stage newStage = new Stage();
                            BorderPane borderPane = new ShellManagerPane().getPane();
                            newStage.setTitle("JspMaster v1.01     Written  by 飞鸿");
                            newStage.setScene(new Scene(borderPane, 1150, 500));
                            currentStage.close();
                            newStage.show();

                            //关闭时加密 sqlite 数据库文件，删除解密的临时文件, 删除密码
                            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent event) {
                                    EncryptUtil.encryptFile(BasicSetting.getInstance().dbFile, PasswordUtil.password);
                                    PasswordUtil.password = "";
                                }
                            });
                        }
                    }
                });


                PenddingUI penddingUI = new PenddingUI(task, currentStage);
                penddingUI.activateProgressBar();
            }
        });

        cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage currentStage = (Stage)vbox.getScene().getWindow();
                currentStage.close();
            }
        });

        return vbox;
    }
}
