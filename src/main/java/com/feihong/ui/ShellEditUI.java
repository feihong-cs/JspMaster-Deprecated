package com.feihong.ui;

import com.feihong.bean.ShellEntry;
import com.feihong.db.DBUtil;
import com.feihong.task.TestConnectionTask;
import com.feihong.util.BasicSetting;
import com.feihong.util.ConfigUtil;
import com.feihong.util.InputValidatorUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShellEditUI {
    private ShellEntry entry;

    public ShellEditUI(ShellEntry entry){
        this.entry = entry;
    }

    public BorderPane getPane(){
        Accordion accordion = new Accordion();
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10,10,10,10));

        HBox hbox = new HBox();
        Button saveButton = new Button("保存");
        saveButton.setPrefWidth(100);
        Button clearButton = new Button("清空");
        clearButton.setPrefWidth(100);
        Button testButton = new Button("测试连接");
        testButton.setPrefWidth(100);
        hbox.getChildren().addAll(saveButton, clearButton, testButton);
        hbox.setMargin(saveButton, new Insets(0,10,10,10));
        hbox.setMargin(clearButton, new Insets(0,10, 10,0));
        hbox.setMargin(testButton, new Insets(0,10,10,0));
        borderPane.setTop(hbox);

        BasicInfoPane basicInfoPane = new BasicInfoPane(entry);
        TitledPane basic = basicInfoPane.getPane();
//        以前的写法，先留着，万一又要改回来呢
//        ParamPane headerPane = new ParamPane("Header", entry);
//        ParamPane getParamPane = new ParamPane("GET参数", entry);
//        ParamPane postParamPane = new ParamPane("POST参数", entry);
        ParamPane headerPane = new ParamPane(entry);
        accordion.getPanes().addAll(basic, headerPane.getPane());
        accordion.setExpandedPane(basic);
        borderPane.setCenter(accordion);

        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // URL和密码输入错误的时候会抛出异常，此时直接 return 表示不处理，还是停留在此页面
                try{
                    basicInfoPane.update();
                }catch(Exception e){
                    return;
                }

                headerPane.update();
//                getParamPane.update();
//                postParamPane.update();

                if(entry.getCreateTime().equalsIgnoreCase("")){
                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
                    String time = dateFormat.format(date);
                    entry.setCreateTime(time);
                }

                try {
                    DBUtil.save(entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Stage currentStage = (Stage)borderPane.getScene().getWindow();
                // 触发 stage 关闭事件。调用 stage.close() 会关闭 stage，但是不会触发关闭事件
                // 参考 https://stackoverflow.com/questions/24483686/how-to-force-javafx-application-close-request-programmatically
                currentStage.fireEvent(
                        new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST)
                );
                currentStage.close();
            }
        });

        clearButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                basicInfoPane.clear();
                headerPane.clear();
//                getParamPane.clear();
//                postParamPane.clear();
            }
        });

        testButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                    basicInfoPane.update();
                }catch(Exception e){
                    return;
                }

                headerPane.update();
//                getParamPane.update();
//                postParamPane.update();

                //参考：https://blog.csdn.net/loongshawn/article/details/52996382
                TestConnectionTask task = new TestConnectionTask(entry);
                task.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if(task.getStatus() == 1){
                        PromptMessageUI.getAlert("",task.getConnectionStatus());
                    }
                });

                PenddingUI penddingUI = new PenddingUI(task, (Stage)borderPane.getScene().getWindow());
                penddingUI.activateProgressBar();
            }
        });

        return borderPane;
    }

    private class BasicInfoPane{
        private ShellEntry entry;
        private TextField urlTextField;
        private ComboBox<String> shellTypeComboBox;
        private TextArea remarkTextarea;
        private ToggleGroup group;
        private TextField encryptKey;
        private TextField iv;
        private RadioButton button1;
        private RadioButton button2;

        public BasicInfoPane(ShellEntry entry){
            this.entry = entry;
        }

        public TitledPane getPane(){
            TitledPane titledPane = new TitledPane();
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(10);
            gridPane.setVgap(15);
            gridPane.setPadding(new Insets(10, 5, 10, 5));

            Label url = new Label("URL");
            gridPane.add(url, 0, 0);
            urlTextField = new TextField();
            urlTextField.setPrefWidth(450);
            urlTextField.setPrefHeight(40);
            urlTextField.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            urlTextField.setText(entry.getUrl());
            gridPane.add(urlTextField, 1, 0);

            Label label = new Label("加密通信");
            gridPane.add(label, 0, 1);
            group = new ToggleGroup();
            encryptKey = new TextField();
            button1 = new RadioButton("是");
            button2 = new RadioButton("否");
            button1.setToggleGroup(group);
            button2.setToggleGroup(group);
            if(entry.getIsEncrypt() == 1){
                button1.setSelected(true);
            }else{
                button2.setSelected(true);
            }
            button1.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(button1.isSelected()){
                        encryptKey.setEditable(true);
                        encryptKey.setStyle("-fx-control-inner-background: white;");
                        iv.setEditable(true);
                        iv.setStyle("-fx-control-inner-background: white;");
                    }
                }
            });
            button2.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(button2.isSelected()){
                        encryptKey.setEditable(false);
                        encryptKey.setStyle("-fx-control-inner-background: gray;");
                        iv.setEditable(false);
                        iv.setStyle("-fx-control-inner-background: gray;");
                    }
                }
            });
            HBox hbox1 = new HBox();
            hbox1.setAlignment(Pos.CENTER_LEFT);
            hbox1.getChildren().addAll(button1, button2);
            hbox1.setMargin(button1, new Insets(10,10,10,0));
            hbox1.setMargin(button2, new Insets(10,0,10,0));
            gridPane.add(hbox1, 1,1);

            Label key = new Label("通信密钥");
            gridPane.add(key, 0, 2);
            encryptKey = new TextField();
            encryptKey.setPrefWidth(450);
            encryptKey.setPrefHeight(40);
            encryptKey.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            if(entry.getEncryptKey() == null || entry.getEncryptKey().trim().equalsIgnoreCase("")){
                encryptKey.setText(ConfigUtil.getCommunicationKey());
            }else{
                encryptKey.setText(entry.getEncryptKey());
            }
            if(button1.isSelected()){
                encryptKey.setEditable(true);
                encryptKey.setStyle("-fx-control-inner-background: white;");
            }else{
                encryptKey.setEditable(false);
                encryptKey.setStyle("-fx-control-inner-background: gray;");
            }
            gridPane.add(encryptKey, 1, 2);



            Label labelForIV = new Label("初始向量");
            gridPane.add(labelForIV, 0, 3);
            iv = new TextField();
            iv.setPrefWidth(450);
            iv.setPrefHeight(40);
            iv.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            if(entry.getIV() == null || entry.getIV().trim().equalsIgnoreCase("")){
                iv.setText(ConfigUtil.getIV());
            }else{
                iv.setText(entry.getIV());
            }
            if(button1.isSelected()){
                iv.setEditable(true);
                iv.setStyle("-fx-control-inner-background: white;");
            }else{
                iv.setEditable(false);
                iv.setStyle("-fx-control-inner-background: gray;");
            }
            gridPane.add(iv, 1, 3);



            Label shellType = new Label("类型");
            gridPane.add(shellType, 0, 4);
            shellTypeComboBox = this.getComboBox();
            shellTypeComboBox.setPrefWidth(200);
            shellTypeComboBox.setPrefHeight(40);
            if(entry.getType() == null || entry.getType().trim().equalsIgnoreCase("")){
                shellTypeComboBox.getSelectionModel().select(0);
            }else{
                shellTypeComboBox.getSelectionModel().select(entry.getType());
            }
            gridPane.add(shellTypeComboBox, 1, 4);


            Label remark = new Label("备注");
            gridPane.add(remark, 0, 5);
            remarkTextarea = new TextArea();
            remarkTextarea.setWrapText(true);
            remarkTextarea.setEditable(true);
            remarkTextarea.setPrefWidth(450);
            remarkTextarea.setMinHeight(100);
            remarkTextarea.prefHeightProperty().bind(gridPane.heightProperty().multiply(0.3));
            remarkTextarea.setText(entry.getRemarks());
            gridPane.add(remarkTextarea, 1, 5);

            titledPane.setText("基本信息设置");
            titledPane.setContent(gridPane);

            return titledPane;
        }

        public void clear(){
            urlTextField.clear();
            button1.setSelected(true);
            encryptKey.clear();
            iv.clear();
            shellTypeComboBox.getSelectionModel().select(0);
            remarkTextarea.clear();
        }

        public void update() throws Exception {
            if(!InputValidatorUtil.isValidURL(urlTextField.getText())){
                PromptMessageUI.getAlert("URL格式不正确","请输入正确格式的URL");
                throw new Exception("参数错误");
            }

            if(shellTypeComboBox.getSelectionModel().getSelectedItem() == null || shellTypeComboBox.getSelectionModel().getSelectedItem().equals("")) {
                PromptMessageUI.getAlert("类型错误","您必须选择一个类型");
                throw new Exception("参数错误");
            }

            if(button1.isSelected() && !InputValidatorUtil.isValidEncryptKey(encryptKey.getText())) {
                PromptMessageUI.getAlert("参数错误","请输入正确的加密密钥");
                throw new Exception("加密密钥输入错误");
            }

            if(button1.isSelected() && !InputValidatorUtil.isValidIV(iv.getText())) {
                PromptMessageUI.getAlert("参数错误","请输入正确的初始向量");
                throw new Exception("初始向量输入错误");
            }

            entry.setUrl(urlTextField.getText());
            if(button1.isSelected()){
                entry.setIsEncrypt(1);
                entry.setEncryptKey((encryptKey.getText() == null ? "" : encryptKey.getText()));
                entry.setIV((iv.getText() == null ? "" : iv.getText()));
            }else{
                entry.setIsEncrypt(0);
                entry.setEncryptKey("");
                entry.setIV("");
            }
            entry.setType(shellTypeComboBox.getSelectionModel().getSelectedItem());
            entry.setRemarks(remarkTextarea.getText());
        }

        public ComboBox<String> getComboBox(){
            List list=new ArrayList();

            Iterator it = BasicSetting.getInstance().shells.keySet().iterator();
            while(it.hasNext()){
                list.add(it.next().toString());
            }

            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setItems(FXCollections.observableArrayList(list));
            comboBox.getSelectionModel().select(0);

            return comboBox;
        }
    }


    private class ParamPane{
        private int count = 0;
        // 这个map用于记录增加了哪些Textfield，Header名称和值各是一个TextField，正好用map的一条记录（entry）记录，方便clear和save
        private Map<TextField, TextField> map;
        private ShellEntry entry;
        //从shellEntry中拿到的 header map
        private Map<String, String> params;

//        public ParamPane(String type, ShellEntry entry){
//            this.type = type;
//            this.entry = entry;
//            map = new HashMap<>();
//
//            if(type.toLowerCase().startsWith("header")){
//                params = entry.getHeaders();
//            }else if(type.toLowerCase().startsWith("get")){
//                params = entry.getGetParams();
//            }else{
//                params = entry.getPostParams();
//            }
//        }

        public ParamPane(ShellEntry entry){
            this.entry = entry;
            map = new HashMap<>();
            params = entry.getHeaders();
        }

        public TitledPane getPane(){
            TitledPane titledPane = new TitledPane();
//            titledPane.setText("设置 " + type);
            titledPane.setText("设置 Header");

            ScrollPane scrollPane = new ScrollPane();
            BorderPane borderPane = new BorderPane();
            borderPane.setPadding(new Insets(10,5,20,5));

//            Button button = new Button("增加 " + type);
            Button button = new Button("增加 Header");
            button.setPrefWidth(150);
            borderPane.setTop(button);
            borderPane.setAlignment(button, Pos.CENTER_LEFT);
            borderPane.setMargin(button, new Insets(0, 0,10, 0));


            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(0, 5, 10, 5));

            if(params.isEmpty()){
                this.addEntry(gridPane, "","");
            }else{
                for(Map.Entry<String, String> mapEntry: params.entrySet()){
                    this.addEntry(gridPane, mapEntry.getKey(), mapEntry.getValue());
                }
            }

            scrollPane.setContent(gridPane);
            scrollPane.setPadding(new Insets(10,0,10,0));
            scrollPane.setFitToWidth(true);
            borderPane.setCenter(scrollPane);
            titledPane.setContent(borderPane);

            button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    addEntry(gridPane, "", "");
                }
            });

            return titledPane;
        }

        public void addEntry(GridPane gridPane, String name, String value){

            count++;

//            Label nameLabel = new Label(type + "名");
            Label nameLabel = new Label("Header名");
            gridPane.add(nameLabel, 0, 2 * count);
            TextField nameTextField = new TextField();
            nameTextField.setPrefWidth(450);
            nameTextField.setPrefHeight(40);
            nameTextField.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            nameTextField.setText(name);
            gridPane.add(nameTextField, 1, 2 * count);

//            Label valueLabel = new Label(type + "值");
            Label valueLabel = new Label("Header值");
            gridPane.add(valueLabel, 0, 2 * count + 1);
            TextField valueTextField = new TextField();
            valueTextField.setPrefWidth(450);
            valueTextField.setPrefHeight(40);
            valueTextField.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            valueTextField.setText(value);
            gridPane.add(valueTextField, 1, 2 * count + 1);

            gridPane.setMargin(nameLabel, new Insets(20,0,0,0));
            gridPane.setMargin(nameTextField, new Insets(20,0,0,0));
            map.put(nameTextField, valueTextField);
        }

        public void clear(){
            count = 0;

            for(Map.Entry<TextField, TextField> entry : map.entrySet()){
                entry.getKey().clear();
                entry.getValue().clear();
            }
        }

        public void update(){
            Map<String, String> map = new HashMap<>();
            for(Map.Entry<TextField,TextField> entry : this.map.entrySet()){
                if(entry.getKey().getText().equals("") && entry.getValue().getText().equals("")){
                    continue;
                }

                map.put(entry.getKey().getText(), entry.getValue().getText());
            }

//            if(type.toLowerCase().startsWith("header")){
//                entry.setHeaders(map);
//            }else if(type.toLowerCase().startsWith("get")){
//                entry.setGetParams(map);
//            }else{
//                entry.setPostParams(map);
//            }
            entry.setHeaders(map);
        }
    }
}