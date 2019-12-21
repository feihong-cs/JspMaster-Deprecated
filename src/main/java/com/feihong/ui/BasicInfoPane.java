package com.feihong.ui;

import com.feihong.bean.BasicInfo;
import com.feihong.executor.CommandExecutor;
import com.feihong.executor.CommandExecutorFactory;
import com.feihong.util.BasicSetting;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BasicInfoPane {
    private String shellPlatform;
    private CommandExecutor executor;

    public BasicInfoPane(){
        shellPlatform = BasicSetting.getInstance().shellPlatform;
        executor = CommandExecutorFactory.getInstance();
    }

    public StackPane getPane(){
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.setPadding(new Insets(10,10,0,10));

        TableView<BasicInfo> table = new TableView<BasicInfo>();

        TableColumn typeColumn = new TableColumn("类型");
        typeColumn.setCellValueFactory(new PropertyValueFactory<BasicInfo, String>("type"));

        TableColumn valueColumn = new TableColumn("值");
        valueColumn.setCellValueFactory(new PropertyValueFactory<BasicInfo, String>("value"));

        // 使用百分比分配 typeColumn 和 valueColumn 占用的宽度
        typeColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        valueColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.7));

        // 为了能让表格中的文字自动换行
        // 从 https://stackoverflow.com/questions/22732013/javafx-tablecolumn-text-wrapping 这里抄的
//        valueColumn.setCellFactory(
//                tc -> {
//                    TableCell<BasicInfo, String> cell = new TableCell<>();
//                    Text text = new Text();
//                    cell.setGraphic(text);
//                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    text.wrappingWidthProperty().bind(valueColumn.widthProperty());
//                    text.textProperty().bind(cell.itemProperty());
//                    return cell ;
//                }
//        );

        table.getColumns().addAll(typeColumn, valueColumn);

        if(this.shellPlatform.equalsIgnoreCase("windows")){
            table.setItems(this.generateWindowsPlatfomBaiscInfo());
        }else if(this.shellPlatform.equalsIgnoreCase("linux")){
            table.setItems(this.generateLinuxPlatformBasicInfo());
        }else{
            // 设置一个空值，不然在 shell 未连接的时候，基本信息界面显示一片空白，啥都没有
            // 设置一个空值后，tableview 可以显示出来
            table.setItems(FXCollections.observableArrayList());
        }

        sp.getChildren().add(table);
        return sp;
    }

    public ObservableList<BasicInfo> generateWindowsPlatfomBaiscInfo(){
        List<BasicInfo> list = new ArrayList<>();

        String paltformAndArch = executor.exec("wmic os get osarchitecture /value").getResponseResult();
        if(paltformAndArch.indexOf("64") > 0){
            list.add(new BasicInfo("操作系统类型", "Windows x64"));
        }else{
            list.add(new BasicInfo("操作系统类型","Windows x86"));
        }


        String version = executor.exec("wmic os get Caption /value").getResponseResult();
        version = version.substring(version.indexOf("=") + 1);
        list.add(new BasicInfo("操作系统版本", version));


        String hostname = executor.exec("hostname").getResponseResult();
        list.add(new BasicInfo("主机名", hostname));


        String user = executor.exec("whoami").getResponseResult();
        list.add(new BasicInfo("当前用户",user));


        String dir = executor.exec("powershell -nop -ep bypass -c \"echo \\\"$PWD\\\"\"").getResponseResult();
        list.add(new BasicInfo("当前目录", dir));


        list.add(new BasicInfo("IP地址", this.getIPForWindows()));

//        这种方式拿不到 java -version的执行结果
//        String jdkVersion = executor.exec("java -version").getResponseResult();
//        list.add(new BasicInfo("JDK版本", jdkVersion));
//
//        久松提供代码，这种方式可以拿到java -version的结果，但是得修改jsp webshell的代码
//        @Test
//        public void Test3() throws Exception{
//            String[] cmd = new String[]{"cmd", "/c","java -version"};
//            Process process = Runtime.getRuntime().exec(cmd);
//            printMessage(process.getInputStream());
//            printMessage(process.getErrorStream());
//            Thread.sleep(1000);
//        }
//
//        private static void printMessage(InputStream input) {
//            new Thread(() -> {
//                Reader reader = new InputStreamReader(input);
//                BufferedReader bf = new BufferedReader(reader);
//                String line = null;
//                try {
//                    while((line=bf.readLine())!=null) {
//                        System.out.println(line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }


        String path = executor.exec("powershell -nop -ep bypass -c \"echo $Env:PATH\"").getResponseResult();
        list.add(new BasicInfo("环境变量PATH", this.myTrim(path)));

        return FXCollections.observableArrayList(list);
    }


    public ObservableList<BasicInfo> generateLinuxPlatformBasicInfo(){

        List<BasicInfo> list = new ArrayList<>();

        String paltformAndArch = executor.exec("uname -a").getResponseResult();
        if(paltformAndArch.indexOf("x86_64") > 0){
            list.add(new BasicInfo("操作系统类型", "Linux x64"));
        }else{
            list.add(new BasicInfo("操作系统类型","Linux x86"));
        }


        list.add(new BasicInfo("内核版本", paltformAndArch));


        String hostname = executor.exec("hostname").getResponseResult();
        list.add(new BasicInfo("主机名", hostname));


        String user = executor.exec("whoami").getResponseResult();
        list.add(new BasicInfo("当前用户",user));


        String dir = executor.exec("pwd").getResponseResult();
        list.add(new BasicInfo("当前目录", dir));


        list.add(new BasicInfo("IP地址", this.getIPForLinux()));


        String path = executor.exec("echo $PATH").getResponseResult();
        list.add(new BasicInfo("环境变量PATH", this.myTrim(path)));

        return FXCollections.observableArrayList(list);
    }

    public String getIPForWindows(){
        String ipAddress = executor.exec("powershell -nop -ep bypass -c \"[System.Net.Dns]::GetHostAddresses($ComputerName) | " +
                "Where-Object {$_.AddressFamily -eq 'InterNetwork'} | Select-Object -ExpandProperty IPAddressToString\"").getResponseResult();
        Set<String> set = new HashSet();
        set.addAll(Arrays.asList(ipAddress.split("\r\n|\n")));
        String result = "";
        for(String str : set){
            result += str.trim() + "\r\n";
        }
        result = result.substring(0, result.length() - 2);

        return result;
    }

    public String getIPForLinux(){
        String ipAddress = executor.exec("ifconfig").getResponseResult();

        String regString = "inet (([0-9]{1,3}\\.){3}[0-9]{1,3})";
        Pattern pattern = Pattern.compile(regString);
        Matcher matcher = pattern.matcher(ipAddress);
        String result = "";
        while(matcher.find()){
            String temp = matcher.group(1);
            //去掉 loopback 的结果
            if(temp.equalsIgnoreCase("127.0.0.1")){
                continue;
            }
            result += temp + "\r\n";
        }

        return result.substring(0, result.length() - 2);
    }

    public String myTrim(String origin){
        List<String> list;
        if(shellPlatform.equalsIgnoreCase("windows")){
            list = Arrays.asList(origin.split(";"));
        }else{
            list = Arrays.asList(origin.split(":"));
        }

        String result = "";
        for(String str : list){
            if(str.trim().equalsIgnoreCase("")){
                continue;
            }

            result += str + "\r\n";
        }

        return result.substring(0, result.length() - 2);
    }
}