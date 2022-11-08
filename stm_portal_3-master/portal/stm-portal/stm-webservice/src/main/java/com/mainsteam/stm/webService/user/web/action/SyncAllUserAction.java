package com.mainsteam.stm.webService.user.web.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.itsmuser.api.IItsmUserApi;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.webService.obj.ItsmUserBean;
import com.mainsteam.stm.webService.obj.UserOperatetype;
import com.mainsteam.stm.webService.user.client.UserServiceApi;
import com.mainsteam.stm.webService.user.client.UserServiceImpl;
import com.mainsteam.stm.webService.user.client.WSException;
import com.mainsteam.stm.webService.util.URLAvailability;

@Controller
@RequestMapping("/thirdSystem/itsmUser")
public class SyncAllUserAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(SyncAllUserAction.class);

	@Autowired
	private IItsmUserApi itsmUserApi;

	@Autowired
	private IUserApi userApi;

	private static final QName SERVICE_NAME = new QName("http://impl.service.webservice.stsm.ms.mainsteam.com/", "BaseInforServiceBusImplService");

	@RequestMapping("/syncAllUser")
	public JSONObject syncAllUser(long[] systemId) {
		// 查询所有的用户信息
		List<User> listUser = userApi.queryAllUserNoPage();
		List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
		for (User user : listUser) {
			ItsmUserBean userBean = new ItsmUserBean(user);
			// 操作类型 新增
			userBean.setOperatetype(UserOperatetype.INSERT.toString());
			list.add(userBean);
		}
		String syncMsg = "";
		for (int i = 0; i < systemId.length; i++) {

			ItsmSystemBo itsmSystemBo = itsmUserApi.getItsmSystemById(systemId[i]);
			// 判断是否开启
			if (1 == itsmSystemBo.getIsOpen()) {
				URL wsdlURL = null;
				try {
					// URL("http://192.168.1.215:9080/itsm/schemas/baseinfo?wsdl");
					wsdlURL = new URL(itsmSystemBo.getWsdlURL());
				} catch (MalformedURLException e) {
					logger.error(e.getMessage(), e);
					// 更新同步状态为FAIL
					itsmSystemBo.setState("FAIL");
					itsmUserApi.updateSyncState(itsmSystemBo);
				}
				UserServiceImpl userServiceImpl = null;
				try {
					/* 判断URL地址是否有效 */
					boolean availaby = URLAvailability.isConnect(wsdlURL);
					
					if (availaby) {
						userServiceImpl = new UserServiceImpl(wsdlURL, SERVICE_NAME);
					} else {
						logger.error("URL地址:" + wsdlURL + "无效");
						/* 更新同步状态为FAIL */
						itsmSystemBo.setState("FAIL");
						itsmUserApi.updateSyncState(itsmSystemBo);
						return toSuccess("同步失败,无效的URL地址!");
					}
				} catch (Exception e) {
					
					logger.error("URL地址:" + wsdlURL + "无效",e);
					/* 更新同步状态为FAIL */
					itsmSystemBo.setState("FAIL");
					itsmUserApi.updateSyncState(itsmSystemBo);
					return toSuccess("同步失败,无效的URL地址!");
				}
				
				UserServiceApi userServiceApi = userServiceImpl.getUserServiceImplPort();
				try {
					/* 开始同步 */
					userServiceApi.setUser(JSONObject.toJSONString(list));
					syncMsg = "同步成功";

					/* 更新同步状态为SUCCESS */
					itsmSystemBo.setState("SUCCESS");
					itsmUserApi.updateSyncState(itsmSystemBo);
				} catch (WSException e) {
					logger.error(e.getMessage(), e);
					syncMsg = "同步失败";
					
					/* 更新同步状态为FAIL*/
					itsmSystemBo.setState("FAIL");
					itsmUserApi.updateSyncState(itsmSystemBo);
				}
			}
		}
		return toSuccess(syncMsg);
	}
}
