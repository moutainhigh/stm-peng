package com.mainsteam.stm.topo.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;

/**
 * <li>HTTP请求、响应对象封装</li>
 * @author zwx
 */
public class HttpUtilEx {
	
	/**
	 * 获取http响应对象
	 * @return HttpServletResponse
	 */
	public static HttpServletResponse getResponse() {
	    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(getRequest());
	    ServletWebRequest webRequest = (ServletWebRequest) ReflectionTestUtils.getField(asyncManager, "asyncWebRequest");
	    return webRequest.getResponse();
	}
	
	/**
	 * 获取http请求对象
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
	    ServletRequestAttributes req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
	    return req.getRequest();
	}
}
