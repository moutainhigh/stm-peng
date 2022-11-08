package com.mainsteam.stm.plugin.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class TomcatCollector {

	private static final Log logger = LogFactory.getLog(TomcatPluginSession.class);

	/** 1:success other:failed */
	public String state = "5";
	
	private static final String AVAIL = "1";
	
	private static final String NOT_AVAIL = "0";
	
	private static final int HTTP_OK = 200; //HTTP响应码

	private StringBuffer statusContent = new StringBuffer();

	private Map<String, List<String>> jvmMap = new HashMap<String, List<String>>();

	public static enum TomcatVersion {
		Tomcat5x, Tomcat6x, Tomcat7x, Tomcat8x
	}

	public static TomcatCollector getInstance(String url, String username,
			String password, TomcatCollector.TomcatVersion version) {
		return new TomcatCollector(url, username, password, version);
	}

	/**
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param version
	 */
	public TomcatCollector(String url, String username, String password,
			TomcatCollector.TomcatVersion version) {
		HttpGet hg = null;
		BufferedReader reader = null;
		try {
			statusContent = new StringBuffer();
			HttpClient httpClient = HttpClients.createDefault();
			hg = new HttpGet(url);
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(username, password));
			HttpClientContext context = HttpClientContext.create();
			context.setCredentialsProvider(credsProvider);
			HttpResponse hr = httpClient.execute(hg, context);
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
			if(logger.isErrorEnabled()) {
				logger.error("Tomcat Collector Error, Url:" + url + ",username:" + username 
						+ ",version:" + version + ". Error Message :" + e.getMessage(),
						e);
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
				if(logger.isWarnEnabled()) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 服务器可用性
	 * 
	 * @return 1:OK,0:不可用
	 */
	public String getAvailability() {
		return state;
	}

	public String getStatusHtml() {
		return statusContent.toString();
	}

	/**
	 * JVM
	 * 
	 * @return
	 */
	public Map<String, List<String>> getJVmSubResource(TomcatVersion version) {
		// <H1>JVM</H1>[\S\s]+?<TABLE
		// border="0">\s*([\S\s]+)</TBODY></TABLE>\s*<H1>"ajp-apr-\d*"</H1>
		String regex = "";
		if (TomcatVersion.Tomcat8x.equals(version)) {
			regex = "&lt;H1&gt;JVM&lt;/H1&gt;[\\S\\s]+?&lt;TABLE border=\"0\"&gt;\\s*([\\s\\S]+)&lt;/TBODY&gt;&lt;/TABLE&gt;\\s*&lt;H1&gt;\"ajp-apr-\\d*\"&lt;/H1&gt;";
		} else if (TomcatVersion.Tomcat7x.equals(version)) {
			regex = "&lt;H1&gt;JVM&lt;/H1&gt;[\\S\\s]+?&lt;TABLE border=\"0\"&gt;\\s*([\\s\\S]+)&lt;/TBODY&gt;&lt;/TABLE&gt;\\s*&lt;H1&gt;\"ajp-bio-\\d*\"&lt;/H1&gt;";
		} else if (TomcatVersion.Tomcat6x.equals(version)) {
			regex = "&lt;H1&gt;JVM&lt;/H1&gt;[\\S\\s]+?&lt;TABLE border=\"0\"&gt;\\s*([\\s\\S]+)&lt;/TBODY&gt;&lt;/TABLE&gt;\\s*&lt;H1&gt;jk-\\d*&lt;/H1&gt;";
		} else if (TomcatVersion.Tomcat5x.equals(version)) {
			regex = "&lt;H1&gt;JVM&lt;/H1&gt;[\\S\\s]+?&lt;TABLE border=\"0\"&gt;\\s*([\\s\\S]+)&lt;/TBODY&gt;&lt;/TABLE&gt;\\s*&lt;H1&gt;http-\\d*&lt;/H1&gt;";
		}
		regex = regex.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(statusContent.toString());
		String temp = "";
		while (m.find()) {
			temp = m.group(1);
			regex = "&lt;TBODY&gt;([\\S\\s]*)";
			regex = regex.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			m = p.matcher(temp);
			while (m.find()) {
				temp = m.group(1);
				// Memory Pool
				regex = "&lt;TR&gt;\\s*&lt;TD&gt;(.+?)&lt;/TD&gt;";
				fillMap(jvmMap, "Memory Pool", temp, regex);
				// Type
				regex = "&lt;TR>\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;(.+?)&lt;/TD&gt;";
				fillMap(jvmMap, "Type", temp, regex);
				// Initial
				regex = "&lt;TR>\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;(.+?)&lt;/TD&gt;";
				fillMap(jvmMap, "Initial", temp, regex);
				// Total
				regex = "&lt;TR&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;(.+?)&lt;/TD&gt;";
				fillMap(jvmMap, "Total", temp, regex);
				// Maximum
				regex = "&lt;TR&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;(.+?)&lt;/TD&gt;";
				fillMap(jvmMap, "Maximum", temp, regex);
				// Used
				regex = "&lt;TR&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;[\\S\\s]+?&lt;/TD&gt;\\s*&lt;TD&gt;(.+?)&lt;/TD&gt;";
				fillMap(jvmMap, "Used", temp, regex);
			}
		}
		return jvmMap;
	}

	public String getJVMMemoryPool(TomcatVersion version) {
		if (jvmMap.isEmpty()) {
			getJVmSubResource(version);
		}
		return getDealString(jvmMap.get("Memory Pool"));
	}

	private String getDealString(List<String> list) {
		StringBuffer ret = new StringBuffer();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String str = list.get(i).toString().replaceAll(" ", "_");
				ret.append(str);
				if (i < list.size() - 1) {
					ret.append("\n");
				}
			}
		}
		return ret.toString();
	}

	private String getDealString2(List<String> keyList, List<String> list) {
		StringBuffer ret = new StringBuffer();
		if (keyList != null && keyList.size() > 0 && list != null
				&& list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String str1 = keyList.get(i).toString().replaceAll(" ", "_");
				String str2 = list.get(i).toString();// .replaceAll(" ", "_");
				ret.append(str1).append("=").append(str2);
				if (i < list.size() - 1) {
					ret.append("\n");
				}
			}
		}
		return ret.toString();
	}

	public String getJVMType(TomcatVersion version) {
		if (jvmMap.isEmpty()) {
			getJVmSubResource(version);
		}
		return getDealString2(jvmMap.get("Memory Pool"), jvmMap.get("Type"));
	}

	public String getJVMInitial(TomcatVersion version) {
		if (jvmMap.isEmpty()) {
			getJVmSubResource(version);
		}
		return getDealString2(jvmMap.get("Memory Pool"), jvmMap.get("Initial"));
	}

	public String getJVMTotal(TomcatVersion version) {
		if (jvmMap.isEmpty()) {
			getJVmSubResource(version);
		}
		return getDealString2(jvmMap.get("Memory Pool"), jvmMap.get("Total"));
	}

	public String getJVMUsed(TomcatVersion version) {
		if (jvmMap.isEmpty()) {
			getJVmSubResource(version);
		}
		return getDealString2(jvmMap.get("Memory Pool"), jvmMap.get("Used"));
	}

	public String getJVMMaximum(TomcatVersion version) {
		if (jvmMap.isEmpty()) {
			getJVmSubResource(version);
		}
		return getDealString2(jvmMap.get("Memory Pool"), jvmMap.get("Maximum"));
	}

	private void fillMap(Map<String, List<String>> commonMap,
			String propertyName, String sourceString, String regex) {
		regex = regex.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sourceString);
		List<String> list = new ArrayList<String>();
		while (m.find()) {
			list.add(m.group(1));
		}
		commonMap.put(propertyName, list);
	}
}
