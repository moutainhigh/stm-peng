package com.mainsteam.stm.system.um.login.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.mainsteam.stm.system.um.login.web.wrapper.LoginRequestWrapper;

public class LoginUPFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new LoginRequestWrapper((HttpServletRequest)req), res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
