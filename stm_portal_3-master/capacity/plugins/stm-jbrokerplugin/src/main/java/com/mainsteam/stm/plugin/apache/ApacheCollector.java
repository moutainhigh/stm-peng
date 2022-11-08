package com.mainsteam.stm.plugin.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

public class ApacheCollector {

	private static final String UPTIME_REGEX = "Uptime\\S\\s(\\d+)";

	private static final Log logger = LogFactory.getLog(ApacheCollector.class);

	private StringBuffer statusContent = new StringBuffer();

	/** 1:success other:failed */
	public String state = "5";

	private static final String AVAIL = "1";

	private static final String NOT_AVAIL = "0";

	private static final int HTTP_OK = 200; // HTTP响应码

	enum ApacheVersion {
		Apache2
	}

	public ApacheCollector(String url, String userName, String password,
			ApacheVersion version, long timeout) {
		HttpGet hg = null;
		BufferedReader reader = null;
		try {
			statusContent = new StringBuffer();
			HttpClient httpClient = HttpClients.createDefault();
			hg = new HttpGet(url);
			HttpResponse hr;
			if (userName != null) {
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials(userName, password));
				HttpClientContext context = HttpClientContext.create();
				context.setCredentialsProvider(credsProvider);
				hr = httpClient.execute(hg, context);
			} else {
				hr = httpClient.execute(hg);
			}
			if (HTTP_OK == hr.getStatusLine().getStatusCode()) {
				reader = new BufferedReader(new InputStreamReader(hr
						.getEntity().getContent()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					statusContent.append(line);
				}
				state = AVAIL;
			} else {
				state = NOT_AVAIL;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Apache Collector Error, Url:" + url
						+ ",username:" + userName + ",version:" + version
						+ ". Error Message :" + e.getMessage(), e);
			}
			state = NOT_AVAIL;
		} finally {
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

	public static ApacheCollector getInstance(String url, String username,
			String password, ApacheVersion version, long timeout) {
		return new ApacheCollector(url, username, password, version, timeout);
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
	 * 返回状态页面全部HTML数据
	 * 
	 * @return
	 */
	public String getStatusHtml() {
		return statusContent.toString();
	}

	/**
	 * apache运行时间
	 * 
	 * @return
	 */
	public String getServerUpTime() {
		String uptime = "";
		String regex = UPTIME_REGEX;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(statusContent.toString());
		while (m.find()) {
			uptime = m.group(1);
		}
		if (uptime != null && !"".equals(uptime.trim())) {
			if (uptime.contains("day") || uptime.contains("hours")
					|| uptime.contains("minutes") || uptime.contains("seconds")) {
				return uptime;
			} else {
				uptime = formatTime(uptime);
			}
		}
		return uptime;
	}

	private String formatTime(String time) {
		try {
			return time;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return time;
		}
	}
}
