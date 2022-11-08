package com.mainsteam.stm.system.um.login.web.filter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.login.api.ILoginApi;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.util.ClassPathUtil;
import com.mainsteam.stm.util.ResponseUtil;
import com.mainsteam.stm.util.Util;
import com.jghong.sso.token.client.web.SSOHelper;

/**
 * <li>文件名称: LoginFilter.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月28日
 * @author   ziwenwen
 */
public class LoginFilter implements Filter{
	
	private String loginPath;
	private Pattern[] ignorePath;
	private int ignorePathLen;
	private final String isServer="isServer";
	

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq=(HttpServletRequest)req;
		HttpSession session=hreq.getSession();
		String session_id = req.getParameter("jsessionid");
		HttpSession sessionForId = SessionContext.getSession(session_id);
		
		ILoginUser loginUser=(ILoginUser)session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
		ILoginUser sessionIdLoginUser = sessionForId==null?null:(ILoginUser) sessionForId.getAttribute(ILoginUser.SESSION_LOGIN_USER);
		
		IMemcache<Boolean> icache=MemCacheFactory.getLocalMemCache(Boolean.class);

		//是否已经登录
		if(loginUser==null && sessionIdLoginUser==null){
			String uri=hreq.getRequestURI();

			if(isIgnorePath(uri)){//是过滤地址处理
				chain.doFilter(req, res);
				return;
			}else if(hreq.getRemoteHost().equals(hreq.getLocalAddr())
					&&!Util.isEmpty(hreq.getParameter(isServer))){//服务器自己的请求 并且不需要在服务器登录的
				chain.doFilter(req, res);
				return;
			}else if(icache.get("STM_REGISER")){//是否注册到门户
				
				ServletContext context = hreq.getSession().getServletContext();
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
				
				ILoginApi loginApi = (ILoginApi) ctx.getBean("stm_system_login_api");

				String account = SSOHelper.getAccount(hreq)==null ? null : SSOHelper.getAccount(hreq);
				
				if(account==null){
					HttpServletResponse hres=((HttpServletResponse)res);
					PrintWriter writer=hres.getWriter();
					if(uri.split("\\?")[0].endsWith("htm")){
						writer.print(ResponseUtil.toJsonObject(400,hreq.getContextPath()+loginPath));
					}else{
						writer.print("<script>top.location.href='"+hreq.getContextPath()+loginPath+"'</script>");
					}
					writer.close();
					return;
				}
				
				LoginUser user = loginApi.login(account);
				user.setUserType();
				session.setAttribute(ILoginUser.SESSION_LOGIN_USER, user);

			}else{
				HttpServletResponse hres=((HttpServletResponse)res);
				PrintWriter writer=hres.getWriter();
				if(uri.split("\\?")[0].endsWith("htm")){
					writer.print(ResponseUtil.toJsonObject(400,hreq.getContextPath()+loginPath));
				}else{
					writer.print("<script>top.location.href='"+hreq.getContextPath()+loginPath+"'</script>");
				}
				writer.close();
				return;
			}
		}
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig conf) throws ServletException {
		String temp = conf.getInitParameter("login.ignore");
		String[] ts = (temp==null)?(new String[0]):temp.split(";");
		this.ignorePath=new Pattern[ignorePathLen=ts.length];
		for(int i=0;i<ignorePathLen;i++){
			this.ignorePath[i]=Pattern.compile(ts[i]);
		}
		
		temp = conf.getInitParameter("login.path");
		this.loginPath=(temp==null)?"/resource/login.html":temp;
//		this.loginPath="http://localhost:8085/cas/login";

		IMemcache<Boolean> icache=MemCacheFactory.getLocalMemCache(Boolean.class);
		//门户是否注册
		File regFile=new File(ClassPathUtil.getTomcatHome()+"STM_REGISER.CODE");
		if(regFile.exists() && regFile.isFile()){
			icache.set("STM_REGISER", true);
		}else{
			icache.set("STM_REGISER", false);
		}
		
	}
	
	private boolean isIgnorePath(String uri){
		for(int i=0;i<ignorePathLen;i++){
			if(this.ignorePath[i].matcher(uri).find())return true;
		}
		return false;
	}
	

}


