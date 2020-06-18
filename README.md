## UPDATE: 这个项目在测试环境中表现正常，但是在实际环境中有各种各样的问题，由于本来就是一个练手项目，所以不会再维护了

# 项目背景
  最早读到《利用 Java 反射和类加载机制绕过JSP后门检测》这篇文章时，很感兴趣，仔细研究了作者的文章，受益颇多。之后也产生了寻找其他方式实现命令执行的想法，在经过一段时间的寻找与实践后，也确实找到了实现命令执行的其他方式。但是，由于某些 JSP 执行命令时的方式较为繁琐，无法通过调用 JavaScript 的方式产生 payload，于是决定采用 JavaFx 编写一个配套的 GUI 管理工具，于是便有了 JspMaster 的诞生。  
  
  与其他 Webshell 管理工具不同，由于一开始的想法是寻找其他命令执行的方式，所以 JspMaster 的底层逻辑是通过命令执行，而不是通过代码执行来实现的。这增强了工具的可扩展性，但同时也引入了一些困难，比如文件的上传与下载，数据库连接（暂未实现）等问题。目前所有的文件上传下载操作，在 Linux 平台下需要借助 python，在 Windows 平台下需要借助 powershell，如果服务器不支持 python 或者 powershell，文件管理功能会无法正常使用。另外由于执行命令时对命令行长度的限制，导致在 Windows 平台下上传和下载的速率较慢，虽然增加了多线程，但是由于 Windows IO 的问题，过多的线程并不会显著提升上传和下载的速率，反而会导致上传失败，这个也是使用命令执行较难克服的缺点之一。    
  
 JspMaster 中内置了 **10** 种不同方式执行命令的 **JSP** 文件，这是整个项目的精华部分，其中部分参考了网络上已有的 Jsp Webshell 的实现思路，另外一些则来自自己的思考与实践。JspMaster 可以作为为开发人员进行安全培训的辅助工具，有效的向 Java 开发人员阐述某些不安全的编码的方式可能造成的危害。
  
# 内置的 10 种 Jsp
|名称|实现方式|备注|
|-------------|-------------------------|----------------------|
|basic.jsp|Runtime.getRuntime.exec()|Java中执行命令最基本的方式|
|reflection.jsp|借助反射的方式实现命令执行|参考自http://nxw.so/3x4Ie|
|URLClassLoader.jsp|借助自定义类加载器从远端加载自定义jar包方式实现命令执行|参考自互联网（出处不可考）|
|deserialization.jsp|借助反序列化的方式实现命令执行|需要借助动态字节码技术|
|xslt.jsp|借助XSLT转换实现命令执行|不建议在Windows平台中使用此种方式|
|ELProcessor.jsp|借助EL表达式实现命令执行|容器需要支持J2EE 7标准（如Tomcat 8），参考https://www.freebuf.com/column/207439.html|
|ScriptManager.jsp|借助Nashorn脚本引擎实现命令执行|未来JDK可能会移除对Nashorn脚本引擎的支持|
|behind.jsp|借助自定义类加载器加载远端发送的字节码流实现命令执行|参考自冰蝎|
|rmi.jsp|借助RMI实现命令执行||
|jndi.jsp|借助JNDI实现命令执行|某些高版本JDK即使设置了trustURLCodebase，trustURLCodebase选项，也不会远程加载Factory类，如JDK 11.0.5-ea|

# 使用说明
+ URLClassLoader.jsp
  使用时需要将远端Jar包的URL地址指向您VPS的地址，远端Jar包可以使用 tool 文件夹中的debug.jar
+ rmi.jsp
  使用时需要将tool文件夹中的rmi.zip上传至您的VPS并解压，在目录中执行    
```
  java Server [ServerIP] [RMI监听端口] [HTTP监听端口]      
```
&nbsp;&nbsp;&nbsp;并将JSP文件中的rmi地址修改为您VPS的IP地址  
![rmi](https://raw.githubusercontent.com/feihong-cs/JspMaster/master/imgForReadme/rmi.png)
 + jndi.jsp
  使用时需要将tool文件夹中的jnid.zip上传至您的VPS并解压，在目录中执行   
```
  java Server [ServerIP] [RMI监听端口] [HTTP监听端口]      
```
&nbsp;&nbsp;&nbsp;并将JSP文件中的rmi地址修改为您VPS的IP地址
![jndi](https://raw.githubusercontent.com/feihong-cs/JspMaster/master/imgForReadme/jndi.png)
 + 界面截图
 ![start](https://raw.githubusercontent.com/feihong-cs/JspMaster/master/imgForReadme/start.png)
 ![filemanage](https://raw.githubusercontent.com/feihong-cs/JspMaster/master/imgForReadme/filemanage.png)

# FAQ
+ Q:JspMaster流量是否加密？  
  A:JspMaster提供了流量加密和流量未加密的两种类型的shell，并在控制面板中提供了是否启用加密的选项，使用者可以根据自己的喜好选择是否使用加密。对于启用流量加密的shell，JspMaster会使用AES加密算法对流量进行全加密，且密钥和IV为初始化时根据用户输入值和当前时间戳拼接并哈希后的值得到的，保证key和IV的唯一性。

+ Q:JspMaster和冰蝎有什么不同？  
  A:底层实现不同，冰蝎功能的底层实现是借助于代码执行实现的，JspMaster的底层实现是借助于命令执行实现的。代码执行的方式比较优雅，在各种平台都可以通用。命令执行扩展性较好，支持增加不同方式的新的shell，但是需要针对不同的平台进行适配。

+ Q:JspMaster不支持数据库管理是因为底层是命令执行从而技术上无法实现吗？  
  A：理论上，在Linux平台下借助python，在Windows平台下借助powershell，可以实现数据库管理功能，但是实现起来较耗时间，目前决定暂不支持数据库管理功能，后期如果JspMaster的使用者较多，可以考虑增加数据库管理功能。

+ Q:JspMaster只支持Jsp吗？  
  A:是的，JspMaster目前只支持Jsp，但是提供了扩展功能。您可以将参考plugin目录中的CommandExecutor.java文件，为您的php/aspx/asp shell实现getName(显示在JspMaster中类型下拉选项框中的名称)和exec(您的shell执行命令时命令的格式，可参考已有的实现)方法，从而实现对php/aspx/asp shell的支持。  （新增了一个PHPDemo，演示对php的支持）
  
# 特别鸣谢
感谢 Pine.Lin 在此项目编写过程中给予的大量技术支持。
