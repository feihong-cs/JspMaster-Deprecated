package com.feihong.ui;

import com.feihong.bean.ShellEntry;
import com.feihong.db.DBUtil;
import com.feihong.task.OpenMainUITask;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ShellManagerPane {

    private BorderPane borderPane;
    private TableView<ShellEntry> tableView;

    public TableView<ShellEntry> getTableView() {
        return tableView;
    }

    public ShellManagerPane(){
    }

    public BorderPane getPane(){
        borderPane = new BorderPane();
        this.initializeTableView();
        borderPane.setCenter(tableView);
        return borderPane;
    }

    public void initializeTableView(){
        tableView = new TableView();

        TableColumn urlColumn = new TableColumn("URL");
        urlColumn.setMinWidth(400);
        urlColumn.setCellValueFactory(
                new PropertyValueFactory<ShellEntry, String>("url")
        );
        urlColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.35));

        TableColumn shellTypeColumn = new TableColumn("Shell类型");
        shellTypeColumn.setPrefWidth(170);
        shellTypeColumn.setCellValueFactory(
                new PropertyValueFactory<ShellEntry, String>("type")
        );

        TableColumn createTimeColumn = new TableColumn("创建时间");
        createTimeColumn.setMinWidth(170);
        createTimeColumn.setCellValueFactory(
                new PropertyValueFactory<ShellEntry, String>("createTime")
        );
        createTimeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));

        TableColumn updateTimeColumn = new TableColumn("最后访问时间");
        updateTimeColumn.setMinWidth(170);
        updateTimeColumn.setCellValueFactory(
                new PropertyValueFactory<ShellEntry, String>("lastvisitTime")
        );
        updateTimeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));

        TableColumn remarkColumn = new TableColumn("备注");
        remarkColumn.setMinWidth(200);
        remarkColumn.setCellValueFactory(
                new PropertyValueFactory<ShellEntry, String>("remarks")
        );

        //当备注过长时，无法完整显示，通过为其添加 Tooltip 来缓解此问题。此处综合参考了2处的结果
        //第一处： BasicInfoPane 原本用于使 tableColumn 中换行的代码（被注释）
        //第二处：https://cloud.tencent.com/developer/ask/190492
        remarkColumn.setCellFactory(tc -> {
            TableCell<ShellEntry, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            text.textProperty().bind(cell.itemProperty());

            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(cell.itemProperty());
            tooltip.setWrapText(true);
            tooltip.setPrefWidth(400);
            cell.setTooltip(tooltip);
            return cell ;
        });

        // remarkColumn 获取所有剩余的宽度
        remarkColumn.prefWidthProperty().bind(tableView.widthProperty()
                .subtract(urlColumn.widthProperty())
                .subtract(shellTypeColumn.widthProperty())
                .subtract(createTimeColumn.widthProperty())
                .subtract(updateTimeColumn.widthProperty())
        );

        tableView.getColumns().addAll(urlColumn,shellTypeColumn, createTimeColumn, updateTimeColumn, remarkColumn);

        List<ShellEntry> list;
        try{
            list = DBUtil.queryAll();
        }catch(Exception e){
            list = new ArrayList<>();
        }

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setItems(FXCollections.observableArrayList(list));

        ShellOperationMenu menu = new ShellOperationMenu(this);
        ContextMenu contextMenu = menu.getContextMenu();
        tableView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            @Override
            public void handle(ContextMenuEvent event) {

                if(tableView.getSelectionModel().getSelectedItems().size() > 1){
                    for(MenuItem menuItem : contextMenu.getItems()){
                        if(!menuItem.getText().equals("删除")){
                            menuItem.setDisable(true);
                        }else{
                            menuItem.setDisable(false);
                        }
                    }

                    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                    return;
                }


                if(tableView.getSelectionModel().getSelectedItem() == null){
                    for(MenuItem menuItem : contextMenu.getItems()){
                        if(menuItem.getText().equals("新增") || menuItem.getText().equals("刷新")){
                            menuItem.setDisable(false);
                        }else{
                            menuItem.setDisable(true);
                        }
                    }
                }else{
                    for(MenuItem menuItem : contextMenu.getItems()){
                        menuItem.setDisable(false);
                    }
                }

                contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
            }
        });

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contextMenu.hide();

                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    //参考：https://blog.csdn.net/loongshawn/article/details/52996382
                    Stage stage = (Stage)borderPane.getScene().getWindow();
                    ShellEntry entry = tableView.getSelectionModel().getSelectedItem();

                    //修复双击空白一直等待卡死的 bug
                    if(entry == null){
                        return;
                    }

                    MainUI mainUI = new MainUI(entry);

                    OpenMainUITask openTask = new OpenMainUITask(mainUI, entry);
                    openTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if(openTask.getStatus() == 1){
                            Stage newStage = new Stage();
                            newStage.setTitle("JspMaster v1.01      Written  by 飞鸿");
                            newStage.setScene(new Scene(mainUI.getPane(), 1000, 600));
                            newStage.show();

                            stage.hide();

                            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent event) {
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
                                    String time = dateFormat.format(date);
                                    entry.setLastvisitTime(time);
                                    try {
                                        DBUtil.save(entry);

                                    } catch (SQLException | ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    stage.show();

                                    //刷新最后访问日期
                                    List<ShellEntry> list = null;
                                    try {
                                        list = DBUtil.queryAll();
                                    } catch (Exception e) {
                                        list = new ArrayList<>();
                                    }

                                    tableView.setItems(FXCollections.observableArrayList(list));
                                }
                            });
                        }else if(openTask.getStatus() == -1){
                            String result = openTask.getResult();
                            if(result.trim().equals("")){
                                result = "Something is wrong";
                            }
                            System.out.println("Info: openTask.getStatus() == -1");
                            PromptMessageUI.getAlert("连接失败", result);
                        }
                    });

                    PenddingUI penddingUI = new PenddingUI(openTask, stage);
                    penddingUI.activateProgressBar();

                }
            }
        });
    }
}
