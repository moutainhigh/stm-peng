package com.mainsteam.stm.webService.user.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.system.itsmuser.api.IItsmUserApi;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.webService.obj.ItsmUserBean;
import com.mainsteam.stm.webService.obj.UserOperatetype;
import com.mainsteam.stm.webService.util.URLAvailability;

@Component
@Aspect
public class UserWebserviceClient {
	
	private final Logger logger = LoggerFactory.getLogger(UserWebserviceClient.class);
	
	@Autowired
	private IItsmUserApi itsmUserApi;
	
	@Autowired
	private IUserApi userApi;
	private static final QName SERVICE_NAME = new QName("http://impl.service.webservice.stsm.ms.mainsteam.com/", "BaseInforServiceBusImplService");
	/**
	 * 添加用户信息
	 * @param joinPoint
	 * @throws Throwable
	 */
	@After("execution(* com.mainsteam.stm.system.um.user.service.impl.UserImpl.add(..))")
	public void addUser(JoinPoint joinPoint) {
		//获得方法名
//		String methodName = joinPoint.getSignature().getName();
		try {
			//拦截的方法参数
			Object[] object = joinPoint.getArgs();
			User user = (User)object[0];
			
			ItsmUserBean userBean = new ItsmUserBean(user);
			userBean.setOperatetype(UserOperatetype.INSERT.toString());
			List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
			list.add(userBean);
			
			setWebServiceUser(list);
		} catch (Exception e) {
			logger.error("同步添加用户出错", e);
		}
	}
	
	/**
	 * 更新用户信息
	 * @param joinPoint
	 * @throws Throwable
	 */
	@After("execution(* com.mainsteam.stm.system.um.user.service.impl.UserImpl.update(..))")
	public void update(JoinPoint joinPoint) {
		try {
			//拦截的方法参数
			Object[] object = joinPoint.getArgs();
			User user = (User)object[0];
			
			ItsmUserBean userBean = new ItsmUserBean(user);
			//操作类型 更新
			userBean.setOperatetype(UserOperatetype.UPDATE.toString());
			List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
			list.add(userBean);
			setWebServiceUser(list);
		} catch (Exception e) {
			logger.error("同步更新用户异常", e);
		}
	}
	
	/**
	 * 批量更新用户状态
	 * @param joinPoint
	 */
	@SuppressWarnings("unchecked")
	@After("execution(* com.mainsteam.stm.system.um.user.service.impl.UserImpl.batchUpdate(..))")
	public void batchUpdate(JoinPoint joinPoint) {
		try {
			//拦截的方法参数
			Object[] obj = joinPoint.getArgs();
			List<User> listUser = (List<User>)obj[0];
			List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
			for (User updateUser : listUser) {
				User user = userApi.get(updateUser.getId());
				/*设置要更新的账号*/
				updateUser.setAccount(user.getAccount());
				updateUser.setSex(user.getSex());
				ItsmUserBean userBean = new ItsmUserBean(updateUser);
				userBean.setOperatetype(UserOperatetype.UPDATE.toString());
				list.add(userBean);
			}
			setWebServiceUser(list);
		} catch (Exception e) {
			logger.error("批量更新用户异常", e);
		}
	}
	/**
	 * 批量删除用户
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Before("execution(* com.mainsteam.stm.system.um.user.service.impl.UserImpl.batchDel(..))")
	public void batchDel(JoinPoint joinPoint) {
		try {
			//拦截的方法参数
			Object[] object = joinPoint.getArgs();
			Long[] ids = (Long[])object[0];
			List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
			User user = new User();
			for (Long userId : ids) {
				user = userApi.get(userId);
				if (user != null) {
					ItsmUserBean userBean = new ItsmUserBean(user);
					userBean.setOperatetype(UserOperatetype.DELETE.toString());
					list.add(userBean);
				}
			}
			setWebServiceUser(list);
		} catch (Exception e) {
			logger.error("同步批量删除用户异常", e);
		}
	}
	
	/**
	 * 调用webservice接口同步用户信息
	 * @param user
	 */
	private void setWebServiceUser(List<ItsmUserBean> list) {
		if (list.size() > 0) {
			List<ItsmSystemBo> listSystemBos = itsmUserApi.queryAllItsmSystem();
			for (ItsmSystemBo itsmSystemBo : listSystemBos) {
				//判断是否开启
				if (1 == itsmSystemBo.getIsOpen()) {
					URL wsdlURL = null;
					try {
//						wsdlURL = new URL("http://192.168.1.215:9080/itsm/schemas/baseinfo?wsdl");
						wsdlURL = new URL(itsmSystemBo.getWsdlURL());
					} catch (MalformedURLException e) {
						logger.error(e.getMessage(), e);
						continue;
					}
					/* 判断URL地址是否有效 */
					boolean availaby = URLAvailability.isConnect(wsdlURL);
					if(availaby) {
						UserServiceImpl userServiceImpl = new UserServiceImpl(wsdlURL, SERVICE_NAME);
				        UserServiceApi userServiceApi = userServiceImpl.getUserServiceImplPort();
				        try {
							userServiceApi.setUser(JSONObject.toJSONString(list));
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							continue;
						}
					}
				
				}
			}
		}
	}
//	public static void main(String[] args) {
//		URL wsdlURL = null;
//		try {
//			wsdlURL = new URL("http://192.168.1.215:9080/itsm/schemas/baseinfo?wsdl");
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//	      
//        UserServiceImpl userServiceImpl = new UserServiceImpl(wsdlURL, SERVICE_NAME);
//        UserServiceApi userServiceApi = userServiceImpl.getUserServiceImplPort();
//        List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
//        ItsmUserBean u = new ItsmUserBean();
//        u.setAvailable("1");
//        u.setId("5000051");
//        u.setLoginid("xiaolei1");
//        u.setName("xiaolei1");
//        u.setOperatetype("INSERT");
//        u.setSex("1");
//        u.setPassword("96e79218965eb72c92a549dd5a330112");
//        list.add(u);
//        try {
//			userServiceApi.setUser(JSONObject.toJSONString(list));
//		} catch (WSException e) {
//			System.out.println(e);
//		}
//	}
}
