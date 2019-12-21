package com.feihong.ui;

import com.feihong.bean.Entry;
import com.feihong.bean.FileModel;
import com.feihong.executor.CommandExecutor;
import com.feihong.executor.CommandExecutorFactory;
import com.feihong.util.BasicSetting;
import com.feihong.util.FileOperationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FileManagerPane {
    private CommandExecutor executor;
    private String shellPlatform;
    private String fileSeparator;
    private BorderPane parent;
    private BorderPane right;
    private TreeView<String> treeView;
    private TableView<Entry> tableView;
    private FileOperationMenu menu;

    public FileManagerPane(){
        executor = CommandExecutorFactory.getInstance();
        shellPlatform = BasicSetting.getInstance().shellPlatform;
        fileSeparator = BasicSetting.getInstance().fileSeprator;
        parent = new BorderPane();
        treeView = new TreeView<>();
        tableView = new TableView<>();
        menu = new FileOperationMenu(this);
    }

    public BorderPane getFileManagerPane(){

        parent.setPadding(new Insets(10,10,0,10));
        this.initializeLeft();

        // windows第一次显示盘符，所以右侧无内容显示，在 initializeLeft 方法中未对右侧内容进行初始化，这里需要显式调用一下
        // linux第一次显示"/"目录，右侧有内容，所以在 initializeLeft 中调用了 initializeRight 方法，以根目录中的内容，这里就不需要再调用了，重复调用会导致覆盖之前的结果，导致初始化是"/"目录内容显示为空
        if(this.shellPlatform.equalsIgnoreCase("windows")){
            initializeRight(FXCollections.observableArrayList());
        }

        return parent;
    }

    public void initializeRight(ObservableList<Entry> data){
        right = new BorderPane();
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER_LEFT);
        Button previousButton = new Button("上一级");
        flowPane.setMargin(previousButton, new Insets(0,10,10,0));
        previousButton.setPrefWidth(100);
        Button refreshButton = new Button("刷新");
        flowPane.setMargin(refreshButton, new Insets(0,10,10,0));
        refreshButton.setPrefWidth(100);
        MenuButton createButton = new MenuButton("新建");
        flowPane.setMargin(createButton, new Insets(0,10,10,0));
        createButton.setPrefWidth(100);

        flowPane.getChildren().addAll(previousButton, refreshButton, createButton);
        flowPane.setPadding(new Insets(0,0,0,10));
        right.setTop(flowPane);


        TableColumn nameCol = new TableColumn("名称");
        nameCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        TableColumn dateCol = new TableColumn("修改日期");
        dateCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        TableColumn typeCol = new TableColumn("文件类型");
        typeCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        TableColumn sizeCol = new TableColumn("大小");
        sizeCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));

        tableView.getColumns().addAll(nameCol, dateCol, typeCol, sizeCol);
        right.setCenter(tableView);
        parent.setCenter(right);
        tableView.setPlaceholder(new Label("暂无数据"));

        nameCol.setCellValueFactory(
                new PropertyValueFactory<Entry, Label>("name")
        );
        dateCol.setCellValueFactory(
                new PropertyValueFactory<Entry,String>("date")
        );
        typeCol.setCellValueFactory(
                new PropertyValueFactory<Entry,String>("type")
        );
        sizeCol.setCellValueFactory(
                new PropertyValueFactory<Entry,String>("size")
        );

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setItems(data);

        // 设置鼠标右键菜单
        ContextMenu contextMenu = menu.getContextMenu();
        this.showContextMenuForTableView(contextMenu);

        refreshButton.setOnAction(event -> this.refresh());
        previousButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
                if(item == null){
                    return;
                }
                if(!isRootDir(item)){
                    item = item.getParent();
                    MultipleSelectionModel msm = treeView.getSelectionModel();
                    msm.select(item);
                    refresh();
                }
            }
        });

        MenuItem createFile = new MenuItem("文件");
        MenuItem createDirecotry = new MenuItem("文件夹");
        //参考 https://stackoverflow.com/questions/43633706/javafx-how-to-create-a-thin-menubar
        createFile.setStyle("-fx-padding: 2 40 2 2");
        createDirecotry.setStyle("-fx-padding: 2 40 2 2");
        createButton.getItems().addAll(createFile, createDirecotry);

        createFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<String> treeItem = (TreeItem<String>)treeView.getSelectionModel().getSelectedItem();
                if(treeItem == null){
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("新建文件");
                dialog.setHeaderText(null);
                dialog.setContentText("文件名称: ");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()){
                    String path = getCurrentPwd(treeItem) + fileSeparator + result.get();
                    FileOperationUtil.newFile(path);
                    refresh();
                }
            }
        });

        createDirecotry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<String> treeItem = (TreeItem<String>)treeView.getSelectionModel().getSelectedItem();
                if(treeItem == null){
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("新建文件夹");
                dialog.setHeaderText(null);
                dialog.setContentText("文件夹名称: ");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()){
                    String path = getCurrentPwd(treeItem) + fileSeparator + result.get();
                    FileOperationUtil.newDirectory(path);
                    refresh();
                }
            }
        });

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 其他 Entry 有单击事件时，隐藏鼠标右键菜单
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 1){
                            contextMenu.hide();
                       }
                }

                // 处理鼠标双击事件
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 2){

                        Entry entry = tableView.getSelectionModel().getSelectedItem();
                        if(entry.getType().equals("文件")){
                            generateFileContentPane(entry);
                        }else{
                            TreeItem item = treeView.getSelectionModel().getSelectedItem();
                            ObservableList<TreeItem<String>> childs = item.getChildren();
                            TreeItem target = null;
                            for(TreeItem<String> item1 : childs){
                                if(item1.getValue().equals(entry.getName().getText())){
                                    target = item1;
                                    break;
                                }
                            }

                            // 在 TreeView 中选中目标
                            MultipleSelectionModel msm = treeView.getSelectionModel();
                            int row = treeView.getRow(target);
                            msm.select(row);
                            refresh();
                        };
                    }

                }
            }
        });
    }


    public void initializeLeft(){
        TreeItem<String> temp = null;
        if(this.shellPlatform.equalsIgnoreCase("windows")){
            temp = new TreeItem<String>("FileSystem", new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/folder.png"))));
        }else{
            temp = new TreeItem<String>("/", new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/folder.png"))));
        }

        TreeItem<String> rootItem = temp;
        rootItem.setExpanded(true);

        treeView = new TreeView<>(rootItem);
        parent.setLeft(treeView);
        treeView.setMinWidth(250);
        treeView.prefWidthProperty().bind(parent.widthProperty().multiply(0.15));

        if(this.shellPlatform.equalsIgnoreCase("windows")){
            List<String> drivers = FileOperationUtil.getWindowsDrivers();
            for(String str : drivers){
                TreeItem<String> item = new TreeItem<>(str.substring(0,2));
                item.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/drive.png"))));
                rootItem.getChildren().add(item);
            }
        }else if(this.shellPlatform.equalsIgnoreCase("linux")){
            List<FileModel> files = FileOperationUtil.getFileEntry("/");
            //先 initialize 后面才能 refresh
            initializeRight(FXCollections.observableArrayList());
            // 选中根目录
            MultipleSelectionModel msm = treeView.getSelectionModel();
            int row = treeView.getRow(rootItem);
            msm.select(row);
            refresh();


//            for(FileModel file : files){
//                if(file.getType().equalsIgnoreCase("文件夹")){
//                    TreeItem<String> item = new TreeItem<>(file.getName(), new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/folder.png"))));
//                    rootItem.getChildren().add(item);
//                }
//            }
        }


        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // 处理鼠标双击事件
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 2){
                        // 获取被选中的 Item
                        TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
                        if(shellPlatform.equalsIgnoreCase("windows") && item == rootItem){
                            return;
                        }
                        refresh();
                    }
                }
            }
        });
    }

    public void refresh(){
        TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
        if(item == null){
            return;
        }
        item.getChildren().clear();
        String currentPwd = getCurrentPwd(item);

        List<FileModel> files = FileOperationUtil.getFileEntry(currentPwd);

        List<Entry> entries = new ArrayList<>();
        for(FileModel file : files){

            if(file.getType().equalsIgnoreCase("文件夹")) {
                // 如果是文件夹，添加到左边树形结构中
                TreeItem<String> newItem = new TreeItem<>(file.getName(), new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/folder.png"))));
                item.getChildren().add(newItem);

                // 保存，稍后在右边窗口展示
                Label label = new Label();
                label.setText(file.getName());
                label.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/folder.png"))));
                entries.add(new Entry(label,file.getDate(),"文件夹","", item));
            }else{
                // 保存，稍后在右边窗口展示
                Label label = new Label();
                label.setText(file.getName());
                label.setGraphic(new ImageView(new Image(ClassLoader.getSystemResourceAsStream("icon/file.png"))));
                entries.add(new Entry(label,file.getDate(),"文件",file.getSize(), item));
            }
        }

        item.setExpanded(true);

        ObservableList<Entry> data = FXCollections.observableArrayList(entries);
        tableView.setItems(data);
    }

    public void showContextMenuForTableView(ContextMenu contextMenu){
        tableView.setOnContextMenuRequested(contextMenuEvent -> {

            if(tableView.getSelectionModel().getSelectedItem() == null){
                for(MenuItem menuItem : contextMenu.getItems()){
                    if(!menuItem.getText().equals("上传") && !menuItem.getText().equals("刷新") && !menuItem.getText().equals("新建")){
                        menuItem.setDisable(true);
                    }else{
                        menuItem.setDisable(false);
                    }
                }

                contextMenu.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                return;
            }

            if(tableView.getSelectionModel().getSelectedItems().size() > 1){
                for(MenuItem menuItem : contextMenu.getItems()){
                    if(!menuItem.getText().equals("删除")){
                        menuItem.setDisable(true);
                    }else{
                        menuItem.setDisable(false);
                    }
                }

                contextMenu.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                return;
            }

            if(tableView.getSelectionModel().getSelectedItem().getType() == "文件") {
                for(MenuItem menuItem : contextMenu.getItems()){
                    menuItem.setDisable(false);
                }
            }else{
                for(MenuItem menuItem : contextMenu.getItems()){
                    if(menuItem.getText().equals("下载") || menuItem.getText().equals("查看")){
                        menuItem.setDisable(true);
                    }else{
                        menuItem.setDisable(false);
                    }
                }
            }

            contextMenu.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });
    }


    public void generateFileContentPane(Entry entry){
        if(Integer.parseInt(entry.getSize().replaceAll(",","")) > 512 * 1024){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("提示框");
            alert.setHeaderText(null);
            alert.setContentText("您正在尝试打开一个大于 500 KB 的文件，可能会产生较长时间的等待，确定继续？");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
                    PromptMessageUI.getAlert("Sorry", "出于用户体验的角度，暂不支持在 Windows 平台下对超过 500 KB 的文件进行在线预览");
                    return;
                }
            }else{
                return;
            }
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10,10,0,10));

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        Button closeButton = new Button("Close");
        closeButton.setAlignment(Pos.CENTER);
        closeButton.setPrefWidth(100);

        Button saveButton = new Button("Save");
        saveButton.setAlignment(Pos.CENTER);
        saveButton.setPrefWidth(100);

        hBox.getChildren().addAll(closeButton, saveButton);
        hBox.setMargin(closeButton, new Insets(0,10,0,0));
        borderPane.setBottom(hBox);
        borderPane.setMargin(hBox, new Insets(10,0,0,0));

        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        textArea.setWrapText(true);

        String path = getCurrentPwd(entry.getParent()) + this.fileSeparator + entry.getName().getText();
        String content = "";
        if(this.shellPlatform.equalsIgnoreCase("windows")){
            content = executor.exec("type \"" + path + "\"").getResponseResult();
        }else{
            content = executor.exec("cat \"" + path + "\"").getResponseResult();
        }

        textArea.setText(content);

        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                parent.setCenter(right);
                refresh();
            }
        });

        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(Integer.parseInt(entry.getSize().replaceAll(",","")) > 1024 * 1024){
                    PromptMessageUI.getAlert("Sorry","文件大于 1 MB, 暂不支持在线修改");
                }else{
                    FileOperationUtil.ModifyFile(path, textArea.getText());
                }

                parent.setCenter(right);
                refresh();
            }
        });

        borderPane.setCenter(textArea);
        parent.setCenter(borderPane);
    }

    public boolean isRootDir(TreeItem<String> treeItem){
        boolean condition = false;

        if(this.shellPlatform.equalsIgnoreCase("windows")){
            condition = treeItem.getValue().contains(":");
        }else{
            condition = treeItem.getValue().equals("/");
        }

        return condition;
    }

    public String getCurrentPwd(TreeItem<String> item){
        List<String> list = new ArrayList<String>();
        list.add(item.getValue());

        while(!isRootDir(item)){
            item = item.getParent();
            list.add(item.getValue());
        }

        Collections.reverse(list);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(this.fileSeparator);
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    public BorderPane getParent() {
        return parent;
    }

    public TreeView<String> getTreeView() {
        return treeView;
    }

    public TableView<Entry> getTableView() {
        return tableView;
    }
}