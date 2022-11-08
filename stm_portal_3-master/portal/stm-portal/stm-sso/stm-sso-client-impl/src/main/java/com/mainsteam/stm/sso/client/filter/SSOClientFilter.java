package com.mainsteam.stm.sso.client.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * <p>
 * Title: SSOClientFilter.java
 * </p>
 * <p>
 * Description: 用户拦截当前请求，验证是否登录
 * </p>
 * <p>
 * Copyright: Copyright © 2005-2015 China WISERV Technologies<／p>
 * 
 * @author WangXingHao
 * @date 2019年7月7日
 * @version 3
 */
public class SSOClientFilter implements Filter {
	
	//SSO 通用session key,不可更改
	private final String SSO_SESSION_LOGIN_USER = "SSO_SESSION_LOGIN_USER";
	
	//SSO 服务器地址
	private final String SSO_SERVER_URL="";
	//SSO 客户端编号
	private final String SSO_CLIENT_CODE="";
	//SSO 客户端地址
	private final String SSO_CLIENT_URL="";
	
	@Override
	public void doFilter(ServletRequest servletRequest,ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		//转换请求
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		//获取当前请求的 ticket 信息
		String ticket = request.getParameter("ticket");
		
		//获取当前请求的session信息
		HttpSession session = request.getSession();
		
		//获取sessionId
		String sessionId = session.getId();
		
		//当前请求URL
//		String url = URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
		
		//获取客户端系统，当前已经登录的用户信息
        String username = (String)session.getAttribute(SSO_SESSION_LOGIN_USER);
		
		//无用户登录，去SSO服务端请求
        if (null == username) {
//        	PostMethod postMethod = new PostMethod(SSO_SERVER_URL);
//        	postMethod.addParameter("sessionID", sessionId);
//        	
//        	HttpClient httpClient = new HttpClient();
//	    	try {
//	            httpClient.executeMethod(postMethod);
//	            username = postMethod.getResponseBodyAsString();
//	            postMethod.releaseConnection();
//	        } catch (Exception e) {
//	            //log
//	        }
//	    	if (null != username && !"".equals(username)) {
//	    		//创建用户登录 session,客户端提供接口
//                session.setAttribute(SSO_SESSION_LOGIN_USER, username);
//                filterChain.doFilter(request, response);
//            } else {
//                response.sendRedirect("http://localhost:8081/sso/index.jsp?code=" + SSO_CLIENT_CODE);
//            }
        	
        }else{
        	filterChain.doFilter(request, response);
        }
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	@Override
	public void destroy() {
		
	}

}
