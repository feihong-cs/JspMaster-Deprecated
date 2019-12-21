package com.feihong.ui;

import com.feihong.bean.Entry;
import com.feihong.bean.ShellEntry;
import com.feihong.db.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class ShellOperationMenu {
    private ShellManagerPane shellManagerPane;

    public ShellOperationMenu(ShellManagerPane shellManagerPane){
        this.shellManagerPane = shellManagerPane;
    }

    public ContextMenu getContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewOperation = new MenuItem("管理");
        MenuItem addOperation = new MenuItem("新增");
        MenuItem editOperation = new MenuItem("编辑");
        MenuItem deleteOperation = new MenuItem("删除");
        MenuItem refreshOperation = new MenuItem("刷新");

        contextMenu.getItems().addAll(viewOperation, addOperation, editOperation, deleteOperation, refreshOperation);

        viewOperation.setOnAction(event -> {
            //触发 tableview 的双击事件，具体逻辑在鼠标点击事件的事件监听器中书写
            Event.fireEvent(shellManagerPane.getTableView(), new MouseEvent(MouseEvent.MOUSE_CLICKED,
                    0, 0, 0, 0, MouseButton.PRIMARY, 2,
                    true, true, true, true, true, true, true, true, true, true, null));
        });

        addOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage();
                stage.setTitle("新增 Shell");
                ShellEntry entry = new ShellEntry();
                stage.setScene(new Scene(new ShellEditUI(entry).getPane(), 600, 540));
                stage.show();
                stage.setOnCloseRequest(event1 -> refreshShellManagerPane());
            }
        });

        editOperation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage();
                stage.setTitle("编辑 Shell");
                ShellEntry entry = shellManagerPane.getTableView().getSelectionModel().getSelectedItem();
                stage.setScene(new Scene(new ShellEditUI(entry).getPane(), 600,540));
                stage.show();
                stage.setOnCloseRequest(event1 -> refreshShellManagerPane());
            }
        });

        deleteOperation.setOnAction(event -> {
            ObservableList<ShellEntry> entries = shellManagerPane.getTableView().getSelectionModel().getSelectedItems();
            if(entries == null || entries.size() == 0){
                return;
            }

            for(ShellEntry entry : entries) {
                try {
                    DBUtil.delete(entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            refreshShellManagerPane();
        });

        refreshOperation.setOnAction(event -> refreshShellManagerPane());

        return contextMenu;
    }

    public void refreshShellManagerPane(){
        List<ShellEntry> list = null;
        try {
            list = DBUtil.queryAll();
        } catch (Exception e) {
           list = new ArrayList<>();
        }

        shellManagerPane.getTableView().setItems(FXCollections.observableArrayList(list));
    }
}