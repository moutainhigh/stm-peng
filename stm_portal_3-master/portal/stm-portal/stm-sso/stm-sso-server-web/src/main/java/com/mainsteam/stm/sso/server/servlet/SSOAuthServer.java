package com.mainsteam.stm.sso.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.system.um.login.web.filter.SessionContext;
import com.mainsteam.stm.util.PropertiesFileUtil;


public class SSOAuthServer extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 存储用户信息键
	 */
	public static final String SSO_SESSION_LOGIN_USER = "SSO_SESSION_LOGIN_USER";

	/**
	 * 已经注册的客户端 列表
	 */
	private Map<String, String> registeredClient = new HashMap<String, String>();

	@Override
	public void init() throws ServletException {
		Properties prop=PropertiesFileUtil.getProperties("properties/sso_server.properties");
		
		String codes = prop.getProperty("sso_registered_code");
		if (codes != null && !"".equals(codes)) {
			for (String code : codes.split(",")) {
				String client = prop.getProperty("client_" + code);
				registeredClient.put(code, client);
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		JSONObject jsonObj=new JSONObject();
		
        PrintWriter writer = response.getWriter();
        //客户端sessionID
        String clientSessionID = request.getParameter("sessionid");
        
        if(clientSessionID==null || "".equals(clientSessionID)){
        	clientSessionID=request.getSession().getId();
        }
        
        //客户端 身份编号
        String clientCode = request.getParameter("code");
        String clientURL=registeredClient.get(clientCode);

        
    	//验证当前 session 是否在服务端登录
        HttpSession session = SessionContext.getSession(clientSessionID);
        
        //未注册的客户端请求，未登录的session请求
        if(clientURL==null || "".equals(clientURL) || session==null){
        	jsonObj.put("resultCode", "0010");
        	jsonObj.put("errorMsg", "用户无法识别");
        	jsonObj.put("data", "");
        	writer.write(jsonObj.toJSONString());
        }else{
        	jsonObj.put("resultCode", "0000");
        	jsonObj.put("errorMsg", "");
        	JSONObject jobj=new JSONObject();
        	LoginUser user=(LoginUser)session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
        	String account = user==null ? "" : user.getAccount();

        	jobj.put("name", user.getName());
        	jobj.put("account", account);
        	jobj.put("password", user.getPassword());
        	jobj.put("mobile", user.getMobile());
        	jobj.put("email", user.getEmail());
          	
        	jsonObj.put("data", jobj.toString());
        	if(account!=null && !"".equals(account)){
        		//已登录用户,告知client登录名称
        		writer.write(jsonObj.toJSONString());
        	}
        	
        }
	}
	
}
