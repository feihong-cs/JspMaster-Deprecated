package com.feihong.ui;

import com.feihong.util.BasicSetting;
import com.feihong.util.ConfigUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Start extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        System.setProperty("user.dir","C:\\Users\\41157\\Desktop\\Ligthsaber");

        if(!ConfigUtil.isInitialized()){
            InitializeUI initializeUI = new InitializeUI();
            initializeUI.show();
        }else{
            if(ConfigUtil.isEncrypt()){
                VBox vBox = LoginUI.getPane();
                primaryStage.setTitle("打开JspMaster");
                primaryStage.setScene(new Scene(vBox, 380, 150));
                primaryStage.show();
            }else{
                BorderPane borderPane = new ShellManagerPane().getPane();
                primaryStage.setTitle("JspMaster v1.0     Written  by 飞鸿");
                primaryStage.setScene(new Scene(borderPane, 1150, 500));
                primaryStage.show();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
