package com.mainsteam.stm.system.um.login.web.action;

import java.awt.image.BufferedImage;
import java.util.Date;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.auditlog.api.IAuditlogApi;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.accesscontrol.api.IAccessControlApi;
import com.mainsteam.stm.system.accesscontrol.bo.AccessControl;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;
import com.mainsteam.stm.system.um.login.api.ILoginApi;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.util.CipherUtil;
import com.mainsteam.stm.util.Util;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;

/**
 * <li>文件名称: LoginAction.java</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version  ms.stm
 * @since    2019年8月29日
 * @author   ziwenwen
 */
@Controller
@RequestMapping("/system/login")
public class LoginAction extends BaseAction {
	
	private final String ACCOUNT="account";
	private final String PASSWORD="password";
	private final String OLDPASSWORD="oldPassword";
	private final String CODE="code";
	
	@Value("${stm.VerificationCode}")
	private String verificationCode;
	
	@Autowired  
    private Producer captchaProducer = null;
	
	@Autowired
	private IAccessControlApi accessControlApi;
	
	@Resource(name="stm_system_userApi")
	private IUserApi userApi;

	@Resource(name="stm_system_login_api")
	private ILoginApi loginApi;
	
	@Resource
	private IAuditlogApi stm_system_AuditlogApi;

	@Resource
	private CustomPropService customPropService;

	@RequestMapping("/getLoginUser")
	public JSONObject getWebLoginUser(HttpSession session){
		return toSuccess(session.getAttribute(ILoginUser.SESSION_LOGIN_USER));
	}
	
	private static final String MODULE_NAME_AUDITLOG = "登录管理";
	private static final Long MODULE_NAME_AUDITLOG_ID = 13L;

	/**
	 * 根据用户账号和密码验证用户是否有效
	 * 
	 * @param account
	 * @param pass
	 * @return 
	 * 
	 * <pre>
	 * {code:code,data:data}
	 * code=200 表示认证成功
	 * code=201 表示账号或密码没有输入
	 * code=202 表示没有该账号
	 * code=203 表示密码输入失败
	 * code=204 表示用户被锁定
	 * code=205 表示ip不允许被访问
	 * code=206 表示密码过期弹出密码修改框
	 * code=207 表示验证码错误
	 * </pre>
	 * 
	 * @throws LicenseNotFoundException 
	 * @throws LicenseCheckException 
	 */
	@RequestMapping("/login")
	public JSONObject login(HttpServletRequest hreq, HttpSession session)
			throws LicenseNotFoundException, LicenseCheckException {
		String account=hreq.getParameter(ACCOUNT);
		String password=hreq.getParameter(PASSWORD);
		String vCode=hreq.getParameter("vCode");
		
		//第三方跳过验证码参数
		if("off".equals(vCode)){
			verificationCode="0";
		}
		
		//开启验证码功能
		if(verificationCode.equals("1")){
			String code=hreq.getParameter(CODE);
			String kaptchaExpected = (String) hreq.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
			if(code==null || kaptchaExpected==null || !code.equals(kaptchaExpected)){
				insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"验证码输入错误！");
				return toJsonObject(207, "验证码输入错误！");
			}
		}
		
		if(accessControlApi.checkIpIsAllow(WebUtil.getRemoteIp(hreq))){
			if(Util.isEmpty(account)||Util.isEmpty(password)){
				return toJsonObject(201, "请输入用户名或密码！");
			}else{
				AccessControl accessControl = accessControlApi.getAccessControlIP();
				LoginUser user = loginApi.login(account);
				if(user == null){
					insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"用户名或密码输入错误！");
					return toJsonObject(202,"用户名或密码输入错误！");
				}
				// 登陆提示信息
				String loginMsg = "登录成功！";
				// 账户已被管理员锁定，不再做任何操作即判断！
				if (user.getStatus() == UserConstants.USER_STATUS_DISABLE
						&& user.getLockType() == UserConstants.USER_LOCK_TYPE_MANUAL) {
					insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"账号已被停用，请联系管理员恢复！");
					return toJsonObject(204, "账号已被停用，请联系管理员恢复！");
				}
				// 验证密码是否正确
				if(password.equals(user.getPassword())){
					// 账户已被锁定
					if(user.getStatus()==UserConstants.USER_STATUS_DISABLE){
						switch (user.getLockType()) {
						case UserConstants.USER_LOCK_TYPE_ERRORPASS:
							// 如果自动解锁可用 则判断是否已经解锁
							if(accessControl.getLoginDeblockIsEnable()){
								long recoverTime = new Date().getTime() - user.getLockTime().getTime();
								int minutes = (int)(recoverTime / (1000 * 60));
								int gapMinutes = Integer.valueOf(accessControl.getLoginDeblockMinutes()) - minutes;
								if(gapMinutes <= 0){
									user.setStatus(UserConstants.USER_STATUS_ENABLE);
									user.setPassErrorCnt(0);
									user.setPassword("");
									userApi.update(user);
								}else{
									loginMsg = "登录失败超过" + accessControl.getLoginFailTime() + "次，" + "账户被锁定，请联系系统管理员解锁或者"
											+ gapMinutes + "分钟后登录系统。";
									insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,loginMsg);
									return toJsonObject(204, loginMsg);
								}
							}else{
								loginMsg = "登录失败超过" + accessControl.getLoginFailTime() + "次，" + "账户被锁定，请联系系统管理员解锁";
								insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,loginMsg);
								return toJsonObject(204, loginMsg);
							}
							break;
						case UserConstants.USER_LOCK_TYPE_OVERDUEPASS:
							if(accessControl.getLoginPassValidityIsEnable()){
								long upPassTimes = new Date().getTime() - user.getUpPassTime().getTime();
								int validityDays = (int)(upPassTimes / (1000 * 60 * 60 * 24));
								// 如果现在修改了全局配置，同时不满足锁定要求则解锁用户
								if(validityDays < Integer.valueOf(accessControl.getLoginPassValidityDays())){
									user.setStatus(UserConstants.USER_STATUS_ENABLE);
									user.setPassword("");
									userApi.update(user);
								}else{
									return toJsonObject(206, "密码已过期，请修改密码。");
								}
							}
							break;
						default:
							insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"账号已被停用，请联系管理员恢复！");
							return toJsonObject(204, "账号已被停用，请联系管理员恢复！");
						}
					}
					// 是否设置的账户密码有效期
					if (accessControl.getLoginPassValidityIsEnable()
							&& user.getUserType() != UserConstants.USER_TYPE_SUPER_ADMIN) {
						long upPassTimes = new Date().getTime() - user.getUpPassTime().getTime();
						int validityDays = (int)(upPassTimes / (1000 * 60 * 60 * 24));
						if(validityDays >= Integer.valueOf(accessControl.getLoginPassValidityDays())){
							user.setStatus(UserConstants.USER_STATUS_DISABLE);
							user.setLockType(UserConstants.USER_LOCK_TYPE_OVERDUEPASS);
							user.setLockTime(new Date());
							user.setPassword("");
							userApi.update(user);
							return toJsonObject(206, "密码已过期，请修改密码。");
						}
						int validityAlertDays = Integer.valueOf(accessControl.getLoginPassValidityDays())
								- validityDays;
						if(validityAlertDays <= Integer.valueOf(accessControl.getLoginPassValidityAlertDays())){
							loginMsg += "您的密码将在" + validityAlertDays + "天后过期，请及时修改密码";
						}
					}
					// 登陆成功则清除登陆失败次数
					if(user.getPassErrorCnt() > 0){
						user.setPassErrorCnt(0);
						user.setPassword("");
						userApi.update(user);
					}
					
					loginApi.setLoginUserRight(user);
					user.setUserType();
					session.setAttribute(ILoginUser.SESSION_LOGIN_USER, user);
					insertLoginSuccessAuditlog(WebUtil.getRemoteIp(hreq),account);
					return toJsonObject(200, loginMsg);
				}else{
					// 密码错误要判断是否有设置密码输入错误次数控制 不包括超级系统管理员
					String errorMsg = "用户名或密码输入错误！";
					if (accessControl.getLoginFailTimeIsEnable()
							&& user.getUserType() != UserConstants.USER_TYPE_SUPER_ADMIN) {
						user.setPassErrorCnt(user.getPassErrorCnt() + 1);
						if(Integer.valueOf(accessControl.getLoginFailTime()) - user.getPassErrorCnt() < 0){
							if(user.getStatus() == UserConstants.USER_STATUS_ENABLE){
								user.setStatus(UserConstants.USER_STATUS_DISABLE);
								user.setLockType(UserConstants.USER_LOCK_TYPE_ERRORPASS);
								user.setLockTime(new Date());
							}
							errorMsg += "登录失败超过" + accessControl.getLoginFailTime() + "次，账户被锁定，请联系系统管理员解锁";
							if(accessControl.getLoginDeblockIsEnable()){
								errorMsg += "或者" + accessControl.getLoginDeblockMinutes() + "分钟后登录系统。";
							}
						}
						user.setPassword("");
						userApi.update(user);
					}
					insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,errorMsg);
					return toJsonObject(203, errorMsg);
				}
			}
		}else{
			insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"您的ip不被允许访问！");
			return toJsonObject(205,"您的ip不被允许访问！");
		}
	}


	@RequestMapping("/loginSkipverification")
	public JSONObject loginSkipverification(HttpServletRequest hreq, HttpServletResponse response, HttpSession session)
			throws LicenseNotFoundException, LicenseCheckException {
		String account=hreq.getParameter(ACCOUNT);

		try {
			response.sendRedirect("http://" + hreq.getServerName()+ ":" + hreq.getServerPort()+"/resource/index.html");
		} catch (Exception e) {
			return null;
		}
		hreq.getSession().setAttribute(ILoginUser.SESSION_LOGIN_USER,loginApi.login(account));

		/*String password=hreq.getParameter(PASSWORD);*/
		LoginUser user = loginApi.login(account);
		String password=user.getPassword();
		String vCode=hreq.getParameter("vCode");

		//第三方跳过验证码参数
		if("off".equals(vCode)){
			verificationCode="0";
		}

		//开启验证码功能
		if(verificationCode.equals("1")){
			String code=hreq.getParameter(CODE);
			String kaptchaExpected = (String) hreq.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
			if(code==null || kaptchaExpected==null || !code.equals(kaptchaExpected)){
				insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"验证码输入错误！");
				return toJsonObject(207, "验证码输入错误！");
			}
		}

		if(accessControlApi.checkIpIsAllow(WebUtil.getRemoteIp(hreq))){
			if(Util.isEmpty(account)||Util.isEmpty(password)){
				return toJsonObject(201, "请输入用户名或密码！");
			}else{
				AccessControl accessControl = accessControlApi.getAccessControlIP();
				/*LoginUser user = loginApi.login(account);*/
				if(user == null){
					insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"用户名或密码输入错误！");
					return toJsonObject(202,"用户名或密码输入错误！");
				}
				// 登陆提示信息
				String loginMsg = "登录成功！";
				// 账户已被管理员锁定，不再做任何操作即判断！
				if (user.getStatus() == UserConstants.USER_STATUS_DISABLE
						&& user.getLockType() == UserConstants.USER_LOCK_TYPE_MANUAL) {
					insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"账号已被停用，请联系管理员恢复！");
					return toJsonObject(204, "账号已被停用，请联系管理员恢复！");
				}
				// 验证密码是否正确
				if(password.equals(user.getPassword())){
					// 账户已被锁定
					if(user.getStatus()==UserConstants.USER_STATUS_DISABLE){
						switch (user.getLockType()) {
							case UserConstants.USER_LOCK_TYPE_ERRORPASS:
								// 如果自动解锁可用 则判断是否已经解锁
								if(accessControl.getLoginDeblockIsEnable()){
									long recoverTime = new Date().getTime() - user.getLockTime().getTime();
									int minutes = (int)(recoverTime / (1000 * 60));
									int gapMinutes = Integer.valueOf(accessControl.getLoginDeblockMinutes()) - minutes;
									if(gapMinutes <= 0){
										user.setStatus(UserConstants.USER_STATUS_ENABLE);
										user.setPassErrorCnt(0);
										user.setPassword("");
										userApi.update(user);
									}else{
										loginMsg = "登录失败超过" + accessControl.getLoginFailTime() + "次，" + "账户被锁定，请联系系统管理员解锁或者"
												+ gapMinutes + "分钟后登录系统。";
										insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,loginMsg);
										return toJsonObject(204, loginMsg);
									}
								}else{
									loginMsg = "登录失败超过" + accessControl.getLoginFailTime() + "次，" + "账户被锁定，请联系系统管理员解锁";
									insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,loginMsg);
									return toJsonObject(204, loginMsg);
								}
								break;
							case UserConstants.USER_LOCK_TYPE_OVERDUEPASS:
								if(accessControl.getLoginPassValidityIsEnable()){
									long upPassTimes = new Date().getTime() - user.getUpPassTime().getTime();
									int validityDays = (int)(upPassTimes / (1000 * 60 * 60 * 24));
									// 如果现在修改了全局配置，同时不满足锁定要求则解锁用户
									if(validityDays < Integer.valueOf(accessControl.getLoginPassValidityDays())){
										user.setStatus(UserConstants.USER_STATUS_ENABLE);
										user.setPassword("");
										userApi.update(user);
									}else{
										return toJsonObject(206, "密码已过期，请修改密码。");
									}
								}
								break;
							default:
								insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"账号已被停用，请联系管理员恢复！");
								return toJsonObject(204, "账号已被停用，请联系管理员恢复！");
						}
					}
					// 是否设置的账户密码有效期
					if (accessControl.getLoginPassValidityIsEnable()
							&& user.getUserType() != UserConstants.USER_TYPE_SUPER_ADMIN) {
						long upPassTimes = new Date().getTime() - user.getUpPassTime().getTime();
						int validityDays = (int)(upPassTimes / (1000 * 60 * 60 * 24));
						if(validityDays >= Integer.valueOf(accessControl.getLoginPassValidityDays())){
							user.setStatus(UserConstants.USER_STATUS_DISABLE);
							user.setLockType(UserConstants.USER_LOCK_TYPE_OVERDUEPASS);
							user.setLockTime(new Date());
							user.setPassword("");
							userApi.update(user);
							return toJsonObject(206, "密码已过期，请修改密码。");
						}
						int validityAlertDays = Integer.valueOf(accessControl.getLoginPassValidityDays())
								- validityDays;
						if(validityAlertDays <= Integer.valueOf(accessControl.getLoginPassValidityAlertDays())){
							loginMsg += "您的密码将在" + validityAlertDays + "天后过期，请及时修改密码";
						}
					}
					// 登陆成功则清除登陆失败次数
					if(user.getPassErrorCnt() > 0){
						user.setPassErrorCnt(0);
						user.setPassword("");
						userApi.update(user);
					}

					loginApi.setLoginUserRight(user);
					user.setUserType();
					session.setAttribute(ILoginUser.SESSION_LOGIN_USER, user);
					insertLoginSuccessAuditlog(WebUtil.getRemoteIp(hreq),account);
					return toJsonObject(200, loginMsg);
				}else{
					// 密码错误要判断是否有设置密码输入错误次数控制 不包括超级系统管理员
					String errorMsg = "用户名或密码输入错误！";
					if (accessControl.getLoginFailTimeIsEnable()
							&& user.getUserType() != UserConstants.USER_TYPE_SUPER_ADMIN) {
						user.setPassErrorCnt(user.getPassErrorCnt() + 1);
						if(Integer.valueOf(accessControl.getLoginFailTime()) - user.getPassErrorCnt() < 0){
							if(user.getStatus() == UserConstants.USER_STATUS_ENABLE){
								user.setStatus(UserConstants.USER_STATUS_DISABLE);
								user.setLockType(UserConstants.USER_LOCK_TYPE_ERRORPASS);
								user.setLockTime(new Date());
							}
							errorMsg += "登录失败超过" + accessControl.getLoginFailTime() + "次，账户被锁定，请联系系统管理员解锁";
							if(accessControl.getLoginDeblockIsEnable()){
								errorMsg += "或者" + accessControl.getLoginDeblockMinutes() + "分钟后登录系统。";
							}
						}
						user.setPassword("");
						userApi.update(user);
					}
					insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,errorMsg);
					return toJsonObject(203, errorMsg);
				}
			}
		}else{
			insertLoginErrorAuditlog(WebUtil.getRemoteIp(hreq),account,"您的ip不被允许访问！");
			return toJsonObject(205,"您的ip不被允许访问！");
		}
	}
	
	private JSONObject insertLoginErrorAuditlog(String ip,String userName,String descripstion){
		AuditlogBo auditlogBo = new AuditlogBo();
		auditlogBo.setOper_date(new Date());
		auditlogBo.setOper_ip(ip);
		auditlogBo.setOper_module(MODULE_NAME_AUDITLOG);
		auditlogBo.setOper_module_id(MODULE_NAME_AUDITLOG_ID);
		auditlogBo.setOper_type("other");
		auditlogBo.setOper_user(userName);
		auditlogBo.setOper_object("登入系统失败（" + descripstion + "）");
		auditlogBo.setDel_status("0");
		stm_system_AuditlogApi.insert(auditlogBo);
		return null;
	}
	
	private JSONObject insertLoginSuccessAuditlog(String ip,String userName){
		AuditlogBo auditlogBo = new AuditlogBo();
		auditlogBo.setOper_date(new Date());
		auditlogBo.setOper_ip(ip);
		auditlogBo.setOper_module(MODULE_NAME_AUDITLOG);
		auditlogBo.setOper_module_id(MODULE_NAME_AUDITLOG_ID);
		auditlogBo.setOper_type("other");
		auditlogBo.setOper_user(userName);
		auditlogBo.setOper_object("登入系统");
		auditlogBo.setDel_status("0");
		stm_system_AuditlogApi.insert(auditlogBo);
		return null;
	}
	

	/**
	 * <pre>
	 * 重置登陆用户信息
	 * </pre>
	 * 
	 * @param session
	 * @return
	 * @throws LicenseNotFoundException 
	 * @throws LicenseCheckException 
	 */
	@RequestMapping("/reLoadLoginUser")
	public JSONObject reLoadLoginUser(HttpSession session){
		LoginUser user=(LoginUser)getLoginUser();
		user=loginApi.login(user.getAccount());
		try {
			loginApi.setLoginUserRight(user);
		} catch (LicenseNotFoundException e) {
			e.printStackTrace();
			License.init();
			return toJsonObject(207, "'请导入License文件！'");
		} catch (LicenseCheckException e) {
			e.printStackTrace();
			License.init();
			return toJsonObject(207, "'请导入License文件！'");
		}
		user.setUserType();
		session.setAttribute(ILoginUser.SESSION_LOGIN_USER,user);
		return toSuccess(user);
		
	}
	
	@RequestMapping("/loginOut")
	public JSONObject loginOut(HttpSession session){
		ILoginUser loginUser = (ILoginUser) session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
		;
		if(loginUser!=null&&loginApi.loginOut(loginUser.getAccount())){
			session.removeAttribute(ILoginUser.SESSION_LOGIN_USER);
			session.invalidate();
		}
		return toSuccess(blank_);
	}
	
	@RequestMapping("/updatePassword")
	public JSONObject updatePassword(String oldPassword,String newPassword,HttpSession session){
		ILoginUser loginUser=(ILoginUser)session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
		if(loginUser.getPassword().equals(CipherUtil.MD5Encode(oldPassword))){
			userApi.updatePassword(loginUser.getAccount(),newPassword);
		}else{	
			return toJsonObject(201,"原密码校验错误，请重新输入！");
		}
		return toSuccess(loginUser.getAccount());
	}

	@RequestMapping("/updatePasswordByUserName")
	public JSONObject updatePasswordByUserName(HttpServletRequest hreq,HttpSession session){
		String account = hreq.getParameter(ACCOUNT);
		String password = hreq.getParameter(PASSWORD);
		String oldPassword = hreq.getParameter(OLDPASSWORD);
		if(Util.isEmpty(account)||Util.isEmpty(password)){
			return toJsonObject(201, "请输入用户名或密码！");
		}
		LoginUser user=loginApi.login(account);
		if(user==null){
			return toJsonObject(202,"账号不存在！");
		}
		if(password.equals(oldPassword)){
			return toJsonObject(203, "新旧密码一致，修改不成功。");
		}
		if(user.getPassword().equals(oldPassword)){
			userApi.updatePassword(user.getAccount(), password);
		}else{
			return toJsonObject(201,"原密码校验错误，请重新输入！");
		}
		return toSuccess(user.getAccount());
	}

	@RequestMapping("/keepSession")
	public JSONObject keepSession(){
		return toSuccess(1);
	}
	
	@RequestMapping("/getSessionId")
	public JSONObject getSessionId(HttpSession session){
		return toSuccess(session.getId());
	}
	
	@RequestMapping("/getKaptchaImage")
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {  
        HttpSession session = request.getSession();  
        String code = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY); 
        
        System.out.println("验证码是: " + code );  
          
        response.setDateHeader("Expires", 0);  
        
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");  
          
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");  
          
        response.setHeader("Pragma", "no-cache");  
          
        response.setContentType("image/jpeg");  
          
        String capText = captchaProducer.createText();  
          
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);  
          
        BufferedImage bi = captchaProducer.createImage(capText);  
        ServletOutputStream out = response.getOutputStream();  
          
        ImageIO.write(bi, "jpg", out);  
        try {  
            out.flush();  
        } finally {  
            out.close();  
        }  
        return null;  
    }  
	
	/**
	 * 根据资源id获取用户
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/getUserByInstanceId")
	public JSONObject getUserByInstanceId(Long instanceId) {
		CustomProp prop=null;
		try {
			prop = customPropService.getPropByInstanceAndKey(instanceId, "liablePerson");
		} catch (InstancelibException e) {			
	
}
		User user = new User();
		if (prop != null) {
			String[] accountIds =prop.getValues();
			if (accountIds.length > 0) {
				user=userApi.get(Long.parseLong(accountIds[0]));		
			}
		}
		return toSuccess(user);

	}

}
