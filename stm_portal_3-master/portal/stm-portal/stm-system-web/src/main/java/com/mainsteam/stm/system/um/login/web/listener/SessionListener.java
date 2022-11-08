package com.mainsteam.stm.system.um.login.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.mainsteam.stm.system.um.login.web.filter.SessionContext;

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		if(arg0!=null && arg0.getSession()!=null){
			SessionContext.AddSession(arg0.getSession());
		}

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		if(arg0!=null && arg0.getSession()!=null){
			SessionContext.DelSession(arg0.getSession());
		}
	}

}
