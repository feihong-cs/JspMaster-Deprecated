package com.feihong.ui;

import com.feihong.bean.Entry;
import com.feihong.task.LinuxPlatformDownloadTask;
import com.feihong.task.LinuxPlatformUploadTask;
import com.feihong.task.WindowsPlatformDownloadTask;
import com.feihong.task.WindowsPlatformUploadTask;
import com.feihong.util.BasicSetting;
import com.feihong.util.FileOperationUtil;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.Optional;

public class FileOperationMenu {
    private FileManagerPane fileManagerPane;
    private String fileSeparator;
    private TableView<Entry> tableView;

    public FileOperationMenu(FileManagerPane fileManagerPane){
        this.fileManagerPane = fileManagerPane;
        this.fileSeparator = BasicSetting.getInstance().fileSeprator;
        this.tableView = fileManagerPane.getTableView();
    }

    public ContextMenu getContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewOperation = new MenuItem("查看");
        MenuItem renameOperation = new MenuItem("重命名");
        MenuItem deleteOperation = new MenuItem("删除");
        MenuItem downloadOperation = new MenuItem("下载");
        MenuItem uploadOperation = new MenuItem("上传");
        Menu newOperation = new Menu("新建");
        MenuItem newFileOperation = new MenuItem("文件");
        MenuItem newDirectoryOperation = new MenuItem("目录");
        MenuItem refreshOperation = new MenuItem("刷新");
        newOperation.getItems().addAll(newFileOperation, newDirectoryOperation);

        uploadOperation.setDisable(true);
        contextMenu.getItems().addAll(viewOperation, renameOperation, deleteOperation, downloadOperation, uploadOperation, newOperation, refreshOperation);

        renameOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Entry entry = tableView.getSelectionModel().getSelectedItem();
                TextInputDialog dialog = new TextInputDialog(entry.getName().getText());
                dialog.setTitle("重命名");
                dialog.setHeaderText(null);
                dialog.setContentText("新名称: ");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()){
                    String path = fileManagerPane.getCurrentPwd(entry.getParent()) + fileSeparator + entry.getName().getText();
                    FileOperationUtil.renameFile(path, path.substring(0, path.lastIndexOf(fileSeparator) + 1) + result.get());
                    fileManagerPane.refresh();
                }
            }
        });

        newFileOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<String> treeItem = (TreeItem<String>)fileManagerPane.getTreeView().getSelectionModel().getSelectedItem();
                if(treeItem == null){
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("新建文件");
                dialog.setHeaderText(null);
                dialog.setContentText("文件名称: ");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()){
                    String path = fileManagerPane.getCurrentPwd(treeItem) + fileSeparator + result.get();
                    FileOperationUtil.newFile(path);
                    fileManagerPane.refresh();
                }
            }
        });

        newDirectoryOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<String> treeItem = (TreeItem<String>)fileManagerPane.getTreeView().getSelectionModel().getSelectedItem();
                if(treeItem == null){
                    return;
                }
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("新建文件夹");
                dialog.setHeaderText(null);
                dialog.setContentText("文件夹名称: ");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()){
                    String path = fileManagerPane.getCurrentPwd(treeItem) + fileSeparator + result.get();
                    FileOperationUtil.newDirectory(path);
                    fileManagerPane.refresh();
                }
            }
        });

        uploadOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("选择要上传的文件");

                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
                File result = fileChooser.showOpenDialog(stage);

                if(result == null){
                    System.out.println("abort");
                    return;
                }

                System.out.println("本地路径: " + result.getPath());

                TreeItem<String> treeItem = (TreeItem<String>)fileManagerPane.getTreeView().getSelectionModel().getSelectedItem();
                String destination = fileManagerPane.getCurrentPwd(treeItem) + fileSeparator + result.getName();

                System.out.println("服务器端路径：" + destination);

                try {
                    Task<Boolean> task = null;
                    if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
                        task = new WindowsPlatformUploadTask(destination, result.getPath(), 10, 8000);
                    }else{
                        if(BasicSetting.getInstance().shellType.equalsIgnoreCase("XSLT Translation") ||
                                BasicSetting.getInstance().shellType.equalsIgnoreCase("冰蝎Style") ||
                                BasicSetting.getInstance().shellType.equalsIgnoreCase("Deserialization")){
                            task = new LinuxPlatformUploadTask(destination, result.getPath(), 5, 10000);
                        }else{
                            task = new LinuxPlatformUploadTask(destination, result.getPath(), 5, 100000);
                        }
                    }

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    ProgressBar progressBar = new ProgressBar(0);
                    progressBar.setPrefWidth(200);
                    Button cancelButton = new Button("取消");
                    hBox.getChildren().addAll(progressBar, cancelButton);
                    hBox.setMinHeight(35);
                    hBox.setMargin(progressBar, new Insets(0,10,0,10));
                    BorderPane bp = (BorderPane)fileManagerPane.getParent().getParent().getParent().getParent();
                    bp.setBottom(hBox);

                    progressBar.progressProperty().unbind();
                    progressBar.progressProperty().bind(task.progressProperty());


                    Task<Boolean> finalTask = task;
                    cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            finalTask.cancel(true);
                            progressBar.progressProperty().unbind();
                            Label label = new Label("下载取消");
                            label.setMinHeight(35);
                            bp.setBottom(label);
                        }
                    });

                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            Label label = new Label("上传完毕");
                            label.setMinHeight(35);
                            bp.setBottom(label);
                            PromptMessageUI.getAlert("Success", "文件上传成功！");
                            fileManagerPane.refresh();
                        }
                    });

                    new Thread(task).start();
                    fileManagerPane.refresh();
                } catch (Exception e) {
                    PromptMessageUI.getAlert("Error", "文件上传过程出错，文件上传失败！");
                    return;
                }
            }
        });

        downloadOperation.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event)
            {
                Entry entry = tableView.getSelectionModel().getSelectedItem();
                String path = fileManagerPane.getCurrentPwd(entry.getParent()) + fileSeparator + entry.getName().getText();
                contextMenu.hide();


                Stage stage = new Stage();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存文件");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Downloads"));
                fileChooser.setInitialFileName(entry.getName().getText());
                File file = fileChooser.showSaveDialog(stage);

                if(file == null){
                    System.out.println("abort");
                    return;
                }

                //这里2个值都是十万，这里修改为了五万
                Task<Boolean> task;
                if(BasicSetting.getInstance().shellPlatform.equalsIgnoreCase("windows")){
                    if(BasicSetting.getInstance().shellType.equalsIgnoreCase("XSLT Translation")){
                        //XSLT转换的时候无法使用10000，会无法得到执行结果
                        task = new WindowsPlatformDownloadTask(path,file.getPath(), 5, 10000);
                    }else{
                        task = new WindowsPlatformDownloadTask(path,file.getPath(), 5, 50000);
                    }

                }else{
                    if(BasicSetting.getInstance().shellType.equalsIgnoreCase("XSLT Translation")){
                        //XSLT转换的时候无法使用10000，会无法得到执行结果
                        task = new LinuxPlatformDownloadTask(path,file.getPath(), 5, 10000);
                    }else{
                        task = new LinuxPlatformDownloadTask(path,file.getPath(), 5, 50000);
                    }
                }


                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_LEFT);
                ProgressBar progressBar = new ProgressBar(0);
                progressBar.setPrefWidth(200);
                Button cancelButton = new Button("取消");
                hBox.getChildren().addAll(progressBar, cancelButton);
                hBox.setMinHeight(35);
                hBox.setMargin(progressBar, new Insets(0,10,0,10));
                BorderPane bp = (BorderPane)fileManagerPane.getParent().getParent().getParent().getParent();
                bp.setBottom(hBox);

                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(task.progressProperty());


                cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        task.cancel(true);
                        progressBar.progressProperty().unbind();
                        Label label = new Label("下载取消");
                        label.setMinHeight(35);
                        bp.setBottom(label);
                    }
                });

                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        Label label = new Label("下载完毕");
                        label.setMinHeight(35);
                        bp.setBottom(label);
                        PromptMessageUI.getAlert("Success", "文件下载成功！");
                    }
                });

                new Thread(task).start();
            }
        });

        viewOperation.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Entry entry = tableView.getSelectionModel().getSelectedItem();
                fileManagerPane.generateFileContentPane(entry);
            }
        });

        deleteOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Entry> entries = tableView.getSelectionModel().getSelectedItems();
                if(entries == null || entries.size() == 0){
                    return;
                }

                for(Entry entry : entries){
                    String path = fileManagerPane.getCurrentPwd(entry.getParent()) + fileSeparator + entry.getName().getText();

                    String type = entry.getType();
                    try {
                        FileOperationUtil.deleteFile(path, type);
                    } catch (Exception e) {
                        PromptMessageUI.getAlert("Error", "文件删除过程出错，文件删除失败！");
                        return;
                    }
                }

                fileManagerPane.refresh();


            }
        });

        refreshOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fileManagerPane.refresh();
            }
        });

        return contextMenu;
    }
}