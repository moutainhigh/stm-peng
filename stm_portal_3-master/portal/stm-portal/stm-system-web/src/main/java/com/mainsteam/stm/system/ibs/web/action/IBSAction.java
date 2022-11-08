package com.mainsteam.stm.system.ibs.web.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;
import com.mainsteam.stm.system.um.login.api.ILoginApi;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.util.PropertiesFileUtil;

@Controller
@RequestMapping("/system/IBSLogin")
public class IBSAction extends BaseAction {

	private final String USERNAME="userName";
	
	private final String INNERTOKEN="token";
	
	@Resource(name="stm_system_login_api")
	private ILoginApi loginApi;
	
	@Resource(name="stm_system_userApi")
	private IUserApi userApi;
	
	@RequestMapping("/fromIBSLogin")
	public JSONObject fromIBSLogin(HttpServletRequest hreq,HttpSession session) throws LicenseNotFoundException, LicenseCheckException{
		Properties prop=PropertiesFileUtil.getProperties("properties/ibs_default.properties");
		
		String getUrl = prop.getProperty("GET_URL");
		
		String ibsToken = prop.getProperty("IBS_TOKEN");
		
		String userName=hreq.getParameter(USERNAME);
		String innerToken=hreq.getParameter(INNERTOKEN);
		LoginUser user=loginApi.login(userName);
		String boolStr = "";
		try {
			boolStr = httpGet(getUrl + userName, innerToken, ibsToken);
		} catch (Exception e) {
			return toJsonObject(206, "URL验证失败!");
		}
		/**判断账号是否存在*/
		if(user==null){
			return toJsonObject(202,"账号不存在！");
		} else if("\"true\"".equals(boolStr)){
			if(user.getStatus()==UserConstants.USER_STATUS_DISABLE){
				return toJsonObject(204, "账号已被停用，请联系管理员恢复！");
			}
			loginApi.setLoginUserRight(user);
			user.setUserType();
			session.setAttribute(ILoginUser.SESSION_LOGIN_USER,user);
			return toJsonObject(200, "登录成功！");
		} else {
			return toJsonObject(205, "IBS验证不通过！");
		}
	}
	
	/**
	 * get请求
	 * @param url
	 * @return
	 */
	public static String httpGet(String url,String innerToken,String ibsToken) throws Exception{
		URL getUrl = new URL(url);
		// 根据拼凑的URL，打开连接，URL.openConnection()函数会根据
		// URL的类型，返回不同的URLConnection子类的对象，在这里我们的URL是一个http，因此它实际上返回的是HttpURLConnection
		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
		
		BufferedReader reader = null;
		StringBuffer reaultBuffer = new StringBuffer();
		try {
			
			/**设置连接的头信息*/
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("IBS-Token", ibsToken);
			connection.setRequestProperty("Inner-Token", innerToken);
			//建立与服务器的连接，并发送数据
			connection.connect();
			
			// 发送数据到服务器并使用Reader读取返回的数据
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			
			String lines = "";
			while ((lines = reader.readLine()) != null) {
				reaultBuffer.append(lines);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			/**断开连接*/
			if (connection != null) {
				connection.disconnect();
			}
		}
		return reaultBuffer.toString();
	}
}
