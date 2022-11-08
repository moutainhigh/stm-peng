package com.mainsteam.stm.sso.client;

import javax.servlet.http.HttpServletRequest;

public interface ISsoLoginApi {

	public void ssoLogin(HttpServletRequest request,String username);
	
}
