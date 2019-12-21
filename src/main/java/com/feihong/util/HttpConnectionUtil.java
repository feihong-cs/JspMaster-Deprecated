package com.feihong.util;

import com.feihong.bean.CommandExecutionResult;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpConnectionUtil {

    public static CommandExecutionResult get(Map<String, String> params){
        return get(BasicSetting.getInstance().shellUrl, params, BasicSetting.getInstance().headers);
    }

    public static CommandExecutionResult get(String url, Map<String,String> params, Map<String,String> headers){
        // 存储结果
        CommandExecutionResult commandExecutionResult = new CommandExecutionResult();

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        URIBuilder uriBuilder = null;
        // 响应模型
        CloseableHttpResponse response = null;

        try{
            //设置参数
            uriBuilder = new URIBuilder(url);
            if(params != null) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for(Map.Entry<String,String> entry : params.entrySet()){
                    // 将参数放入键值对类NameValuePair中,再放入集合中
                    BasicNameValuePair param1 = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    list.add(param1);
                }
                uriBuilder.setParameters(list);
            }

            // 创建Get请求
            HttpGet httpGet = new HttpGet(uriBuilder.build());

            // 设置Header
            if(headers != null){
                for(Map.Entry<String,String> entry : headers.entrySet()){
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }


            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            // 将上面的配置信息 运用到这个Get请求里
            httpGet.setConfig(requestConfig);

            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            commandExecutionResult.setResponseStatusCode(response.getStatusLine().getStatusCode());

            if (responseEntity != null) {
                commandExecutionResult.setResponseResult(EntityUtils.toString(responseEntity).trim());
            }
        } catch(ConnectException | SocketTimeoutException e){
            commandExecutionResult.setException(e.toString());
        } catch(Exception e) {
            commandExecutionResult.setException(e.toString());
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return commandExecutionResult;
        }
    }

    public static CommandExecutionResult post(Map<String, String> params){
        return post(BasicSetting.getInstance().shellUrl, params, BasicSetting.getInstance().headers);
    }

    public static CommandExecutionResult post(String url, Map<String,String> params, Map<String, String> headers){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 存储结果
        CommandExecutionResult commandExecutionResult = new CommandExecutionResult();

        // 创建POST请求对象
        HttpPost httpPost = new HttpPost(url);

        // 设置Header
        if(headers != null){
            for(Map.Entry<String,String> entry : headers.entrySet()){
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 响应模型
        CloseableHttpResponse response = null;
        try{

            // 设置请求参数
            if(params != null){
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry : params.entrySet()){
                    BasicNameValuePair param1 = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    list.add(param1);
                }

                UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entityParam);
            }

            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            // 将上面的配置信息 运用到这个Post请求里
            httpPost.setConfig(requestConfig);

            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            commandExecutionResult.setResponseStatusCode(response.getStatusLine().getStatusCode());

            if (responseEntity != null) {
                commandExecutionResult.setResponseResult(EntityUtils.toString(responseEntity).trim());
            }
        } catch (ConnectException | SocketTimeoutException e) {
            commandExecutionResult.setException(e.toString());
        } catch (Exception e) {
            commandExecutionResult.setException(e.toString());
        }finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return commandExecutionResult;
    }

    public static CommandExecutionResult post(String postBody){
        return post(BasicSetting.getInstance().shellUrl, postBody, BasicSetting.getInstance().headers);
    }

    public static CommandExecutionResult post(String url, String postBody){
        return post(url, postBody, BasicSetting.getInstance().headers);
    }

    public static CommandExecutionResult post(String url, String postBody, Map<String, String> headers){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 存储结果
        CommandExecutionResult commandExecutionResult = new CommandExecutionResult();

        // 创建POST请求对象
        HttpPost httpPost = new HttpPost(url);

        // 设置Header
        if(headers != null){
            for(Map.Entry<String,String> entry : headers.entrySet()){
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 响应模型
        CloseableHttpResponse response = null;
        try{

            // 设置请求参数
            if(postBody != null && !postBody.trim().equals("")){
                httpPost.setEntity(new StringEntity(postBody, "UTF-8"));
            }

            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();

            // 将上面的配置信息 运用到这个Post请求里
            httpPost.setConfig(requestConfig);

            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            commandExecutionResult.setResponseStatusCode(response.getStatusLine().getStatusCode());

            if (responseEntity != null) {
                String s = EntityUtils.toString(responseEntity).trim();
                commandExecutionResult.setResponseResult(s);
            }
        } catch (ConnectException | SocketTimeoutException e) {
            commandExecutionResult.setException(e.toString());
        } catch (Exception e) {
            commandExecutionResult.setException(e.toString());
        }finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return commandExecutionResult;
    }
}
