package com.feihong.ui;

import com.feihong.bean.ShellEntry;
import com.feihong.util.BasicSetting;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class MainUI {
    private ShellEntry entry;
    private BorderPane borderPane;

    public BorderPane getPane(){
        return this.borderPane;
    }

    public MainUI(ShellEntry entry){
        this.entry = entry;
        this.borderPane = new BorderPane();
    }

    public void initialize(String connectionStatus){

        BasicSetting basicSetting = BasicSetting.getInstance();

//        public String encryptKey = "";
        FlowPane flowPane = new FlowPane();
        Label statusLabel = new Label();
        statusLabel.setMinHeight(35);
        statusLabel.setText(connectionStatus);
        flowPane.getChildren().addAll(statusLabel);
        flowPane.setMargin(statusLabel,new Insets(0,0,0,10));
        borderPane.setBottom(flowPane);

        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab(" 基本信息 ");
        Tab tab2 = new Tab(" 执行命令 ");
        Tab tab3 = new Tab(" 文件管理 ");
        tabPane.getTabs().addAll(tab1,tab2,tab3);
        // 如果不加下面这个语句，那么默认情况下，tab 上存在 x 按钮，点击 x，即可关闭 tab，我不想要这样。下面这句的作用就是去除 tab 标签的 x 按钮
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        if(basicSetting.isConnected == true){
            tab1.setContent(new BasicInfoPane().getPane());
            tab2.setContent(new ExecuteCMDPane().getPane());
            tab3.setContent(new FileManagerPane().getFileManagerPane());
        }

        borderPane.setCenter(tabPane);
    }
}
