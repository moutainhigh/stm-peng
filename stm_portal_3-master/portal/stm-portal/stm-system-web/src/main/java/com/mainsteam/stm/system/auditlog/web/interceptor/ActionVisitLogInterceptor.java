package com.mainsteam.stm.system.auditlog.web.interceptor;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.auditlog.api.IAuditlogApi;
import com.mainsteam.stm.auditlog.bo.AuditlogBo;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi;
import com.mainsteam.stm.system.auditlog.bo.MethodEntity;

/**
 * <li>文件名称: com.mainsteam.stm.system.auditlog.web.interceptor.ActionVisitLogInterceptor.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月13日
 */
@Component
public class ActionVisitLogInterceptor implements HandlerInterceptor{

	private Map<String, MethodEntity> methodEntityMap;
	
	@Resource
	private IAuditlogApi stm_system_AuditlogApi;
	
//	private static final String LOGIN_MAPPING_URI = "/system/login/login.htm";
	/**
	 * 注销登陆的MappingUri地址
	 */
//	private static final String LOGOUT_MAPPING_URI = "/system/login/loginOut.htm";
	
	private Logger logger = Logger.getLogger(getClass());
	
	public ActionVisitLogInterceptor(){}
	@Autowired
	public ActionVisitLogInterceptor(IModualActionMethodApi modualActionMethodApi, SequenceFactory factory){
		this.methodEntityMap = modualActionMethodApi.getMethodEntityMap();
	}
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(final HttpServletRequest request,
			HttpServletResponse response,final Object handler) throws Exception {

			final ILoginUser user = (ILoginUser) request.getSession(true).getAttribute(ILoginUser.SESSION_LOGIN_USER);
			final String ip = WebUtil.getRemoteIp(request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					ActionVisitLogInterceptor.this.saveLog(user,ip, handler, Boolean.valueOf(true));
				}
			}).start();
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(final HttpServletRequest request,
			HttpServletResponse response,final Object handler, Exception ex)throws Exception {
			final ILoginUser user = (ILoginUser) request.getSession(true).getAttribute(ILoginUser.SESSION_LOGIN_USER);
			final String ip = WebUtil.getRemoteIp(request);
			new Thread(new Runnable() {
				@Override
				public void run() {
					ActionVisitLogInterceptor.this.saveLog(user,ip, handler, Boolean.valueOf(true));
				}
			}).start();
	}
	
	private void saveLog(ILoginUser user,String rmIP, Object handler, Boolean isAfter){
		try{
			Method method = ((HandlerMethod)handler).getMethod();
			String methodName = method.getDeclaringClass().getName()+'.' + method.getName();
			MethodEntity methodEntity = methodEntityMap.get(methodName);
			if(methodEntity==null||!isAfter.equals(methodEntity.getIsAfter())){	//对象为空或者执行条件不匹配
				return ;
			}

			AuditlogBo auditlogBo = new AuditlogBo();
			auditlogBo.setOper_date(new Date());
			auditlogBo.setOper_ip(rmIP);
			auditlogBo.setOper_module(methodEntity.getModuleName());
			auditlogBo.setOper_module_id(Long.valueOf(methodEntity.getModuleId()));
			auditlogBo.setOper_type(methodEntity.getType());
			auditlogBo.setOper_user(user.getName());
			auditlogBo.setOper_object(methodEntity.getModuleName() + '-'+methodEntity.getDescription());
			auditlogBo.setDel_status("0");
//			stm_system_AuditlogApi.setLogByWebservice(JSONObject.toJSONString(auditlogBo));
			stm_system_AuditlogApi.insert(auditlogBo);
		}catch(Exception e){
			if(logger.isInfoEnabled()){
				logger.error("日志记录失败-" + e.getMessage());
			}
		}
	}
}
