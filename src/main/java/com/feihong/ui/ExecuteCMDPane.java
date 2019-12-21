package com.feihong.ui;

import com.feihong.executor.CommandExecutor;
import com.feihong.executor.CommandExecutorFactory;
import com.feihong.util.BasicSetting;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;


public class ExecuteCMDPane {
    private CommandExecutor executor;
    private boolean connected;

    public ExecuteCMDPane(){
        executor = CommandExecutorFactory.getInstance();
        connected = BasicSetting.getInstance().isConnected;
    }

    public BorderPane getPane(){
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10, 10, 0, 10));

        BorderPane innerPane = new BorderPane();
        TextField textField = new TextField();
        textField.setPrefHeight(35);

        Button button = new Button();
        button.setAlignment(Pos.CENTER);
        button.setText("执行");
        button.setPrefWidth(100);
        button.setPrefHeight(35);

        if(connected){
            button.setDisable(false);
        }else{
            button.setDisable(true);
        }

        innerPane.setCenter(textField);
        innerPane.setMargin(textField,new Insets(0,10,0,0));
        innerPane.setRight(button);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        bp.setCenter(textArea);
        bp.setMargin(textArea,new Insets(10,0,0,0));
        bp.setTop(innerPane);

        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                   button.fire();
                }else{
                    //这里可以在类中加上一个 flag 标志位，让这段代码每次只需执行一次，而不是每次按键都会触发执行，但是那样会增加代码逻辑的复杂度，这里又不影响性能，所以完全没必要
                    textArea.setPromptText("");
                    textArea.clear();
                }
            }
        });

        //如果这里使用 setOnMouseevent，上面的 button.fire() 就不能生效了，得修改成 Event.fireEvent()
        //所以这里为了方便，使用了 setOnAction
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String command = textField.getText().trim();
                if(!command.equals("")){
                    String executeResult = executor.exec(command).getResponseResult();
                    //这个或条件是为了解决执行在 Windows 下执行 ifconfig 时回显不正常的 bug
                    //在 Windows 下通过 Runtime.getRuntime.exec() 执行，在本地得到的结果为
                    //"java.io.IOException: Cannot run program "ifconfig": CreateProcess error=2, 系统找不到指定的文件。"
                    //但是这个结果如果通过 response.getWriter.print 进行回显的时候，就会变成 "　　"
                    if(executeResult.trim().equals("") || executeResult.trim().equalsIgnoreCase("　　")){
                        textArea.setPromptText("执行完毕，未获取到执行结果，请确认命令输入是否正确");
                    }else{
                        textArea.setText(executeResult);
                    }
                }
            }
        });

        return bp;
    }
}
