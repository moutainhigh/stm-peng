package com.mainsteam.stm.plugin.TX9200;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;


public class Tx9200PluginSession implements PluginSession{

	
	private static HttpClient client = WebClientDevWrapper
			.wrapClient(new DefaultHttpClient());
	// private static DecompressingHttpClient client = new
	// DecompressingHttpClient(new DefaultHttpClient());
	private static final String TX9200IP="IP";
	
	public static final String TX9200PASSWORD = "PASSWORD";

	public static final String TX9200USERNAME = "USERNAME";
	
	private static final int FIRST_ROW = 0;
	
	private static final Log logger = LogFactory.getLog(Tx9200PluginSession.class);

	private  String posturl = "";
	private  String CSRFToken = "";
	
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * IP地址
	 */
	
	private String ip;
	
	public Tx9200PluginSession(){
		
	}
	public Tx9200PluginSession(String ip,String username,String password){
		this.ip=ip;
		this.username=username;
		this.password=password;
		
	}
	public List<BasicNameValuePair> getParams(String url, String name,
			String word) {
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		
		HttpGet get = new HttpGet(url);
		System.out.println(url);
		get.setHeader("Accept",
				"	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("User-Agent",
				"	Mozilla/5.0 (Windows NT 5.1; rv:23.0) Gecko/20100101 Firefox/23.0");
		try {
			HttpResponse response = client.execute(get);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			
			Document doc = Jsoup.parse(result.toString());// ����HTML�ı�ת���ɾ��нṹ��Document����
			Elements formEls = doc.getElementsByTag("form");// ��ȡ��¼form
			Element formEl = formEls.first();
			String action = formEl.attr("action");
			posturl = "https://"+ip + action;
			System.out.println(posturl);
			
			
			list.add(new BasicNameValuePair("TB_USERNAME", name));
			list.add(new BasicNameValuePair("TB_PASS", word));

			

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("连接失败，用户名密码可能错误");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			get.abort();
			get.releaseConnection();
			
		}

		return list;
	}
	
	public String postLogin(String url, List<BasicNameValuePair> params) {

		StringBuffer result = new StringBuffer();

		String redirectUrl = "";
		HttpPost post = new HttpPost(url);
		//System.out.println(url);
		HttpContext context = null;
		post.setHeader("Accept",
				"	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("User-Agent",
				"	Mozilla/5.0 (Windows NT 5.1; rv:23.0) Gecko/20100101 Firefox/23.0");
		post.setHeader("CSRFToken", CSRFToken);
		try {
			post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			HttpResponse response = client.execute(post, context);// context ��
																	// HttpContext�Ķ���
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "utf-8"));

			String line = "";
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 302) {
				Header header = response.getFirstHeader("Location");
				redirectUrl = header.getValue();
			}
			logger.info("reponse:" + result.toString());
																									
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("postLogin连接失败");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			post.abort();
			post.releaseConnection();
		}
		return result.toString();
	}
	public ArrayList<String> redirect(String redirectUrl) {
		ArrayList<String> arrylist = new ArrayList<String>();
		HttpGet get = new HttpGet(redirectUrl);
		get.setHeader("Accept",
				"	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader(
				"User-Agent",
				"	Mozilla/5.0 (Windows; U; Windows NT 5.1; nl; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13");
		try {
			HttpResponse response = client.execute(get);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "utf-8"));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			//System.out.println("result:" + result.toString());
			Document doc = Jsoup.parse(result.toString());
			Elements formEls = doc.getElementsByTag("script");
			String js = formEls.toString();
			if(js!=null&&!"".equals(js)){
			int index = js.indexOf("parent.lines='");
			
			int last = js.indexOf("';");
			
			String lines = js.substring(index + 2, last);
			//System.out.println(lines);
			logger.info("lines=> "+lines);
			
		
			arrylist = getPeripheralStatus(lines);
			}
			if(arrylist.isEmpty()){
				arrylist.add("Center Display");
				arrylist.add("Center Camera");
				arrylist.add("timeout : 1");
			}
		

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			get.abort();
			get.releaseConnection();
		}
		return arrylist;
	}
	@Override
	public boolean check(PluginInitParameter arg0)
			throws PluginSessionRunException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destory() {
	}

	@Override
	public PluginResultSet execute(PluginExecutorParameter<?> executorParam,
			PluginSessionContext sessionContext) throws PluginSessionRunException {
		String loginurl = "https://"+ip + "/admin/general/system/tbmainpane";
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BEST_MATCH);
		client.getParams().setParameter(
				org.apache.http.params.CoreProtocolPNames.HTTP_CONTENT_CHARSET,
				"UTF8");
		List<BasicNameValuePair> params = this.getParams(loginurl, username,
				password);
		String redirectUrl = this.postLogin(posturl, params);
		Document doc = Jsoup.parse(redirectUrl.toString());
		Elements formEls = doc.getElementsByTag("form");
		Element formEl = formEls.first();
		String action = formEl.attr("action");
		String strUrl = "https://"+ip + "/admin/status/manager/update/CSRFToken/"+action.split("CSRFToken/")[1];
		ArrayList<String> list = this.redirect(strUrl);
		PluginResultSet resultSet = new PluginResultSet();
		if(list.isEmpty()){
			logger.info("js解析失败");
		}
		StringBuffer buff = new StringBuffer();
		Iterator<String> iterator = list.iterator();
		int index = FIRST_ROW;
		while(iterator.hasNext()) {
			   buff.append(iterator.next());
			   buff.append("\r\n");
			   }
		resultSet.putValue(FIRST_ROW, index++, buff.toString());
		logger.info(resultSet.toString());
		if (logger.isDebugEnabled()) {
			logger.warn("The final resultSet is " + resultSet
					);
		}
		return resultSet;
	}

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {
		// TODO Auto-generated method stub
		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			String initValue = initParameters[i].getValue();
			switch (initParameters[i].getKey()) {
			case TX9200IP:
				this.ip = initValue;
				break;
			case TX9200USERNAME:
				this.username = initValue;
				break;
			case TX9200PASSWORD:
				this.password = initValue;
				break;
			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter "
							+ initParameters[i].getKey() + "=" + initValue);
				}
				break;
			
			}
		}
		
	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}
	public ArrayList<String> PatterStatus(String patterstauts,String lines){
		Pattern pattern = Pattern.compile(patterstauts);
		ArrayList<String> list = new ArrayList<String>();
		Matcher m = pattern.matcher(lines);
		String[] strArry = null;
		StringBuffer buffer = new StringBuffer();
		while(m.find()){
			String str = m.group();
			if(str.startsWith("Display")||str.startsWith("Camera")){
				str="Center "+str;
			}
				
			buffer.append(str);
			buffer.append("\r\n");
		}
		strArry = buffer.toString().split("\r\n");
		for(String str1:strArry){
			list.add(str1);
		}
		
		
		return list;
	}
	public ArrayList<String> getPeripheralStatus(String lines){
		
	ArrayList<String> list = new ArrayList<String>();
	lines = lines.replace("&++&", "=====");
	//System.out.println(lines);
	String index = null;
	String patterString = "='(\\d+)";
	Pattern pattern = Pattern.compile(patterString);
	Matcher m = pattern.matcher(lines);
	while (m.find()) {
		index = m.group(1);
	}
	System.out.println(index);
	System.out.println("".equals(index));
	if(index!=null&&!index.isEmpty()&&Integer.parseInt(index)==1){
		patterString="((Display Status: [^&]*&__&/admin/pages/images/[^.]*.gif)|(Camera Status:  [^&]*&__&/admin/pages/images/[^.]*.gif)|(Presentation Device Status: [^&]*&__&/admin/pages/images/[^.]*.gif)|(gif=====[^=]*=====[^=]*=====[^=]*=====[^=]*=====Unified))";
		list=PatterStatus(patterString,lines);
		list.add("timeout : 0");		
	};
	if(index!=null&&!index.isEmpty()&&Integer.parseInt(index)==3||index!=null&&!index.isEmpty()&&Integer.parseInt(index)==2){
		patterString="((Center Display Status: [^&]*&__&/admin/pages/images/[^.]*.gif)|(Center Camera Status:  [^&]*&__&/admin/pages/images/[^.]*.gif)|(Left Display Status: [^&]*&__&/admin/pages/images/[^.]*.gif)|(Left Camera Status:  [^&]*&__&/admin/pages/images/[^.]*.gif)|(Right Display Status: [^&]*&__&/admin/pages/images/[^.]*.gif)|(Right Camera Status:  [^&]*&__&/admin/pages/images/[^.]*.gif)|(Presentation Device Status: [^&]*&__&/admin/pages/images/[^.]*.gif)|(gif=====[^=]*=====[^=]*=====[^=]*=====[^=]*=====Unified))";
		list=PatterStatus(patterString,lines);
		list.add("timeout : 0");
	}
	if(index==null){
		list.add("Center Display");
		list.add("Center Camera");
		list.add("timeout : 1");
		System.out.println(list.size());
	}
	
	return list;
	
}
}
