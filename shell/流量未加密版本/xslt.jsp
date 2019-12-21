<%@ page import="javax.xml.transform.*"%>
<%@ page import="javax.xml.transform.stream.*"%>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=GB2312" language="java" %>
<%
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
    br.close();

    if(wholeStr != null && !wholeStr.trim().equals("")) {
        wholeStr = wholeStr.substring(0, wholeStr.length() - 1);
        try {
            InputStream in = new ByteArrayInputStream(wholeStr.getBytes());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(baos);

            Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(in));
            t.transform(new StreamSource(new ByteArrayInputStream("<?xml version=\"1.0\"?><data></data>".getBytes())), result);


            response.getWriter().print(baos.toString().trim().substring(1).trim());
            out.clearBuffer();
            baos.close();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
%>
