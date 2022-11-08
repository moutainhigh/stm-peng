package com.mainsteam.stm.sso.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.sso.client.util.HttpRequest;
import com.mainsteam.stm.sso.client.util.PropertiesFileUtil;


/**
 * <p>
 * Title: SSOClient.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright © 2005-2015 China WISERV Technologies<／p>
 * 
 * @author WangXingHao
 * @date 2019年7月6日
 * @version 3
 */
public class SSOClient extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String SSO="sso";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username=null;

		String loginSessionID=request.getParameter("sessionid");
		if(loginSessionID==null || "".equals(loginSessionID)){
			loginSessionID=request.getSession().getId();
		}
	
		Properties prop=PropertiesFileUtil.getProperties("properties/sso_client.properties");
		
		String sso_client_code=prop.getProperty("sso_client_code");
		
		String url=prop.getProperty("oc4_portal_url");
		url=url.substring(0,url.indexOf("resource"))+SSO;
		
    	Map<String, String> map=new HashMap<String, String>();

    	map.put("code", sso_client_code);
    	map.put("sessionid", loginSessionID);
		
    	try{
    		username=HttpRequest.sendGet(url, map);
    		
        	if(username!=null && !"".equals(username)){
    			Class<?> loginImpl= Class.forName(prop.getProperty("sso_login_class"));
    			ISsoLoginApi ssoLogin=(ISsoLoginApi)loginImpl.newInstance();
    			ssoLogin.ssoLogin(request,username);
    			request.getRequestDispatcher("").forward(request, response);
        	}else{
        		url = request.getRequestURL().toString().replaceAll(SSO, "");
        		response.sendRedirect(url);
        	}
        	
    	}catch(Exception e){
    		System.out.println("Get Login User faild...");
    	}

	}

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	public static void main(String[] args) {
		String u="http://127.0.0.1:8081/itsm/sso";
		u=u.replaceAll(SSO, "");
		System.out.println(u);
	}


}
