package com.mainsteam.stm.plugin.sharepoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class SharePoint2013Collector {

	public static Map<String, String> RESLUTS_MAP = null;

	/**
	 * 服务器可用性
	 * 
	 * @return 1:OK,-1:用户密码错,0:Exception
	 */
	public String getAvailability(JBrokerParameter parameter) {
		String status = "0";
		Map<String, String> map = null;
		if (RESLUTS_MAP != null) {
			map = RESLUTS_MAP;
		} else {
			map = this.getFarmServers(parameter.getIp(), parameter.getPort(),
					parameter.getUsername(), parameter.getPassword(), parameter
							.getApacheBo().getUrlParam(), parameter
							.getTimeout(), parameter.getApacheBo().isSSL());
		}
		if (map != null) {
			String code = map.get("code");
			if ("200".equals(code)) {
				status = "1";
			} else if ("401".equals(code)) {
				status = "-1";
			}
		}
		return status;
	}

	public String getVersion(JBrokerParameter parameter) {
		String version = "Microsoft SharePoint Server 2013";
		return version;
	}

	public String getConfigurationDatabaseVersion(JBrokerParameter parameter) {
		if (RESLUTS_MAP != null) {
			return RESLUTS_MAP.get("configurationDatabaseVersion");
		}
		Map<String, String> map = this.getFarmServers(parameter.getIp(),
				parameter.getPort(), parameter.getUsername(),
				parameter.getPassword(), parameter.getApacheBo().getUrlParam(),
				parameter.getTimeout(), parameter.getApacheBo().isSSL());
		if (map != null) {
			return map.get("configurationDatabaseVersion");
		}
		return null;
	}

	public String getConfigurationDatabaseServerName(JBrokerParameter parameter) {
		if (RESLUTS_MAP != null) {
			return RESLUTS_MAP.get("configurationDatabaseServerName");
		}
		Map<String, String> map = this.getFarmServers(parameter.getIp(),
				parameter.getPort(), parameter.getUsername(),
				parameter.getPassword(), parameter.getApacheBo().getUrlParam(),
				parameter.getTimeout(), parameter.getApacheBo().isSSL());
		if (map != null) {
			return map.get("configurationDatabaseServerName");
		}
		return null;
	}

	public String getConfigurationDatabaseName(JBrokerParameter parameter) {
		if (RESLUTS_MAP != null) {
			return RESLUTS_MAP.get("configurationDatabaseName");
		}
		Map<String, String> map = this.getFarmServers(parameter.getIp(),
				parameter.getPort(), parameter.getUsername(),
				parameter.getPassword(), parameter.getApacheBo().getUrlParam(),
				parameter.getTimeout(), parameter.getApacheBo().isSSL());
		if (map != null) {
			return map.get("configurationDatabaseName");
		}
		return null;
	}

	public String getServerPort(JBrokerParameter parameter) {
		return parameter.getPort() + "";
	}

	public Map<String, String> getFarmServers(String ip, int port,
			String username, String password, String projectName, int timeout,
			boolean isHttps) {
		String protocol = isHttps == true ? "https" : "http";
		Map<String, String> map = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpHost target = new HttpHost(ip, port, protocol);
		StringBuilder s = new StringBuilder();
		if (projectName != null) {
			if (!projectName.startsWith("/")) {
				s.append("/");
			}
			s.append(projectName);
			if (projectName.endsWith("/")) {
				s.deleteCharAt(s.length() - 1);
			}
		}
		HttpGet httpget = new HttpGet(s + "/_admin/FarmServers.aspx");
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).build();
			httpget.setConfig(requestConfig);
			response = httpclient.execute(target, httpget,
					this.login(ip, username, password));
			map = new HashMap<String, String>();
			int code = response.getStatusLine().getStatusCode();
			map.put("code", code + "");
			if (200 == code) {
				map.put("availability", "1");
				Element table = Jsoup.parse(response.getEntity().getContent(),
						"UTF-8",
						protocol + "://" + ip + (port == 80 ? "" : port) + "/")
						.getElementById("idItemHoverTable");
				Elements tds = table.select("tr>td:eq(1)");
				map.put("configurationDatabaseVersion", tds.get(0).text()
						.trim());
				map.put("configurationDatabaseServerName", tds.get(1).text()
						.trim());
				map.put("configurationDatabaseName", tds.get(2).text().trim());
				return map;
			}
		} catch (IOException e) {
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private HttpClientContext login(String ip, String username, String password) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(username,
				password, "realm", ip));
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		return context;
	}

}
