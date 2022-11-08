package com.mainsteam.stm.system.um.login.web.filter;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class SessionContext {

	private static HashMap sessionMap = new HashMap();
	
	public static synchronized void AddSession(HttpSession session) {
        if (session != null) {
        	sessionMap.put(session.getId(), session);
        }
    }
	
	public static synchronized void DelSession(HttpSession session) {
        if (session != null) {
            sessionMap.remove(session.getId());
        }
    }
	
    public static synchronized HttpSession getSession(String session_id) {
        if (session_id == null)
        return null;
        return (HttpSession) sessionMap.get(session_id);
    }
}
