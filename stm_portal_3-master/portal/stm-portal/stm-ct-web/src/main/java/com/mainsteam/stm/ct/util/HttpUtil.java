package com.mainsteam.stm.ct.util;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	public static Logger logger=Logger.getLogger(HttpUtil.class);
    public static JSONObject httpget(String address,String method,String params){
        String  responseContent=null;
        HttpGet httget=null;
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse = null;
        try {
            String url="http://"+address+method;
            if(!StringUtils.isEmpty(params)){
                url=url+"?"+params;
            }
            httget=new HttpGet(url);
            String paramsList="{}";
            Map<String, String> headersMap=new HashMap<>();
            //headersMap.put("X-Auth-Token", token);
            //httpPost.setEntity(new StringEntity(paramsList));
            httpClient = new DefaultHttpClient();
            String charSet = "utf-8";
            if(headersMap != null){
                for(String key : headersMap.keySet()){
                    httget.addHeader(key, headersMap.get(key));
                }
            }
            httpResponse = httpClient.execute(httget);
            responseContent = EntityUtils.toString(httpResponse.getEntity(),charSet);
            System.out.println(responseContent);
            httpResponse.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	logger.error("操作指针端失败："+e.getMessage(),e);
        	
            e.printStackTrace();
           
        }finally {
            httget.abort();
            try {
                httpResponse.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                httpClient.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return JSON.parseObject(responseContent);
    }

    public static JSONObject httppost(String address,String method,String params){
        String  responseContent=null;
        HttpPost httpPost=null;
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse = null;
        try {
            String url="http://"+address+method;
            httpPost=new HttpPost(url);
            Map<String, String> headersMap=new HashMap<>();
            //headersMap.put("X-Auth-Token", token);
            httpPost.setEntity(new StringEntity(params));
            httpClient = new DefaultHttpClient();
            String charSet = "utf-8";
            if(headersMap != null){
                for(String key : headersMap.keySet()){
                    httpPost.addHeader(key, headersMap.get(key));
                }
            }
            httpResponse = httpClient.execute(httpPost);
            responseContent = EntityUtils.toString(httpResponse.getEntity(),charSet);
            System.out.println(responseContent);
            httpResponse.close();
        }catch (Exception e) {
            // TODO Auto-generated catch block
        	logger.error("操作指针端失败："+e.getMessage(),e);
            e.printStackTrace();
        }finally {
            httpPost.abort();
            try {
                httpResponse.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                httpClient.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return JSON.parseObject(responseContent);
    }
}
