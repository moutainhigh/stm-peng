package com.mainsteam.stm.plugin.nginx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;



public class NginxCollector {
	
	private static final Log logger = LogFactory.getLog(NginxCollector.class);
	
	private StringBuffer statusContent = new StringBuffer();
	
	private static final String AVAIL = "1";
	
	public String state = "5";

	private static final String NOT_AVAIL = "0";
	
	String ip;
	
	public NginxCollector(String url,String pagename,long timeout){
		HttpGet hg = null;
		BufferedReader reader = null;
		ip=url.split("/")[0];
		try {
			statusContent = new StringBuffer();
			CloseableHttpClient httpClient = HttpClients.createDefault();
			hg = new HttpGet("http://"+url);
			CloseableHttpResponse response = httpClient.execute(hg);
			int status = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == status) {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String line = "";
				while ((line = br.readLine()) != null) {
					statusContent.append(line);
				}
				
				state = AVAIL;
			}else {
				logger.info("未能正常进入页面，response响应码："+response.getStatusLine().getStatusCode());
				state = NOT_AVAIL;
			}
				
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Niginx Collector Error, Url:" + url
						+ ",pagename:" + pagename + e.getMessage(), e);
			}
			state = NOT_AVAIL;
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (hg != null) {
					hg.clone();
				}
			} catch (CloneNotSupportedException | IOException e) {
				if (logger.isWarnEnabled()) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
		
	}
	public static NginxCollector getInstance(String url, String pagename,long timeout
			) {
		return new NginxCollector(url, pagename,timeout);
	}
	/**
	 * 返回状态页面全部HTML数据
	 * 
	 * @return
	 */
	public String getStatusHtml() {
		return statusContent.toString();
	}
	/**
	 * 服务器可用性
	 * 
	 * @return 1:OK,-1:用户密码错,0:Exception
	 */
	public String getAvailability() {
		return state;
	}
	/**
	 * IP地址
	 * @return
	 */
	public String getIp(){
		return ip.split(":")[0];
		
	}
}
