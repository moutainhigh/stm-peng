package com.mainsteam.stm.platform.web.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mainsteam.stm.platform.local.DefaultLocalImpl;
import com.mainsteam.stm.platform.local.ILocal;
import com.mainsteam.stm.platform.local.ILocalKey;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.util.ResponseUtil;

/**
 * <li>文件名称: BaseAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
public abstract class BaseAction extends ResponseUtil implements ILocal,
		ILocalKey {

	private ILocal local;

	protected BaseAction() {
		System.out.println(this.getClass().getName());
		// malachi in modify
		Matcher matcher = Pattern.compile(
				"com\\.mainsteam\\.stm\\.([\\w\\.]+)\\.web\\.+").matcher(
				this.getClass().getName());
		matcher.find();
		this.local = new DefaultLocalImpl(matcher.group(1));
	}

	/**
	 * 获取登陆用户信息
	 */
	protected ILoginUser getLoginUser(HttpSession session) {
		return (ILoginUser) session.getAttribute(ILoginUser.SESSION_LOGIN_USER);
	}

	public static ILoginUser getLoginUser() {
		return (ILoginUser) getHttpServletRequest().getSession().getAttribute(
				ILoginUser.SESSION_LOGIN_USER);
	}

	/**
	 * @see ILocal
	 * @param key
	 */
	public String getKey(String key) {
		return local.getKey(key);
	}

	/**
	 * @see ILocal
	 * @param key
	 */
	public String getSysKey(String key) {
		return local.getSysKey(key);
	}

	@Override
	protected void finalize() throws Throwable {
		this.local = null;
		super.finalize();
	}

	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
	}
}
