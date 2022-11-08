package com.mainsteam.stm.camera.web.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.camera.api.ICameraService;
import com.mainsteam.stm.camera.bo.CameraDcListPageVo;
import com.mainsteam.stm.camera.web.vo.CameraMonitorVo;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.node.NodeGroupService;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IResourceMonitorApi;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorBo;
import com.mainsteam.stm.portal.resource.bo.ResourceMonitorPageBo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorPageVo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorVo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.SecureUtil;

@Controller
@RequestMapping({ "/portal/resource/cameradiscover" })
public class CameraDiscoverAction extends BaseAction {
	private Logger logger = Logger.getLogger(CameraDiscoverAction.class);

	@Resource
	private ICameraService cameraService;

	@Resource
	private NodeGroupService nodeGroupService;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private IResourceMonitorApi resourceMonitorApi;

	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;

	@Resource
	private IDomainApi domainApi;

	@Resource
	private IUserApi userApi;

	@Resource
	private IDomainApi stm_system_DomainApi;

	@Resource
	private CustomPropService customPropService;

	@Resource
	private DiscoverPropService discoverPropService;
	
	@RequestMapping({ "/autodiscover" })
	public JSONObject discoverResource(String jsonData, HttpSession session) {
		Map paramter = (Map) JSONObject.parseObject(jsonData, HashMap.class);
		Map result = cameraService.discovery(paramter);
		return toSuccess(result);
	}

	@RequestMapping({ "/addCamera" })
	public JSONObject addCamera(String jsonData, HttpSession session) {
		Map paramter = (Map) JSONObject.parseObject(jsonData, HashMap.class);
		Map result = cameraService.addCameraInstances(paramter);
		return toSuccess(result);

	}

	@RequestMapping({ "/getHaveMonitor" })
	public JSONObject getHaveMonitor(ResourceMonitorPageVo pageVo, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		ResourceMonitorPageBo pageBo = null;
		try {
			ResourceMonitorVo rmv = pageVo.getCondition();
			pageBo = resourceMonitorApi.getMonitored(user, pageVo.getStartRow(), pageVo.getRowCount(),
					rmv.getInstanceStatus(), rmv.getiPorName(), rmv.getDomainId(), rmv.getCategoryId(),
					rmv.getCategoryIds(), rmv.getResourceId(), rmv.getIsCustomResGroup(), pageVo.getSort(),
					pageVo.getOrder());

			pageVo.setTotalRecord(pageBo.getTotalRecord());
			List<ResourceMonitorVo> resourceVoList = new ArrayList();
			List<ResourceMonitorBo> monitorBoList = pageBo.getResourceMonitorBosExtends();
			for (int i = 0; (monitorBoList != null) && (i < monitorBoList.size()); i++) {
				resourceVoList.add(instanceBoToResourceVo((ResourceMonitorBo) monitorBoList.get(i)));
			}
			pageVo.setResourceMonitors(resourceVoList);
			pageVo.setResourceCategoryBos(pageBo.getResourceCategoryBos());
		} catch (Exception e) {
			logger.error("getHaveMonitor:", e);
		}
		return toSuccess(pageVo);
	}

	@RequestMapping({ "/getNotMonitor" })
	public JSONObject getNotMonitor(ResourceMonitorPageVo pageVo, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		ResourceMonitorPageBo pageBo = null;
		try {
			ResourceMonitorVo rmv = pageVo.getCondition();
			pageBo = resourceMonitorApi.getUnMonitored(user, pageVo.getStartRow(), pageVo.getRowCount(),
					rmv.getiPorName(), rmv.getDomainId(), rmv.getCategoryId(), rmv.getCategoryIds(),
					rmv.getResourceId(), rmv.getIsCustomResGroup(), pageVo.getSort(), pageVo.getOrder());

			pageVo.setTotalRecord(pageBo.getTotalRecord());
			List<ResourceMonitorVo> resourceVoList = new ArrayList();
			List<ResourceMonitorBo> monitorBoList = pageBo.getResourceMonitorBosExtends();
			for (int i = 0; (monitorBoList != null) && (i < monitorBoList.size()); i++) {
				resourceVoList.add(instanceBoToResourceVo((ResourceMonitorBo) monitorBoList.get(i)));
			}
			pageVo.setResourceMonitors(resourceVoList);
			pageVo.setResourceCategoryBos(pageBo.getResourceCategoryBos());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return toSuccess(pageVo);
	}

	private ResourceMonitorVo instanceBoToResourceVo(ResourceMonitorBo resourceMonitorBo) {
		CameraMonitorVo resourceVo = new CameraMonitorVo();
		resourceVo.setId(resourceMonitorBo.getInstanceId());
		resourceVo.setSourceName(resourceMonitorBo.getShowName());
		resourceVo.setIpAddress(resourceMonitorBo.getIp());
		resourceVo.setDomainName(null == domainApi.get(resourceMonitorBo.getDomainId().longValue()) ? ""
				: domainApi.get(resourceMonitorBo.getDomainId().longValue()).getName());

		resourceVo.setDomainId(resourceMonitorBo.getDomainId());

		resourceVo.setMonitorType(resourceMonitorBo.getResourceName());
		resourceVo.setInstanceState(resourceMonitorBo.getLifeState().toString());
		resourceVo.setCpuStatus(resourceMonitorBo.getCpuStatus());
		resourceVo.setCpuAvailability(resourceMonitorBo.getCpuAvailability());
		resourceVo.setMemoryStatus(resourceMonitorBo.getMemoryStatus());
		resourceVo.setMemoryAvailability(resourceMonitorBo.getMemoryAvailability());
		resourceVo.setResponseTime(resourceMonitorBo.getResponseTime());
		resourceVo.setInstanceStatus(resourceMonitorBo.getInstanceStatus());
		resourceVo.setResourceId(resourceMonitorBo.getResourceId());
		resourceVo.setHasRight(resourceMonitorBo.getHasRight());
		resourceVo.setCpuIsAlarm(resourceMonitorBo.getCpuIsAlarm());
		resourceVo.setMemoryIsAlarm(resourceMonitorBo.getMemoryIsAlarm());
		resourceVo.setDcsGroupName(resourceMonitorBo.getDcsGroupName());
		resourceVo.setHasTelSSHParams(resourceMonitorBo.getHasTelSSHParams());
		if (resourceMonitorBo.getResourceId().equalsIgnoreCase("CameraRes")) {
			try {
				DiscoverProp typeProp = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"cameraType");
				if (typeProp != null) {
					resourceVo.setCameraType(typeProp.getValues()[0]);
				}
				DiscoverProp groupProp = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"groupName");
				if (groupProp != null) {
					resourceVo.setCameraGroup(groupProp.getValues()[0]);
				}
				DiscoverProp addressProp = discoverPropService
						.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "address");
				if (addressProp != null) {
					resourceVo.setCameraAddress(addressProp.getValues()[0]);
				}
				DiscoverProp loginUser = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"loginuser");
				if (loginUser != null) {
					resourceVo.setLoginUser(loginUser.getValues()[0]);
				}
				DiscoverProp loginPassword = discoverPropService
						.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "loginpassword");
				if (loginPassword != null) {
					resourceVo.setLoginPassword(SecureUtil.pwdDecrypt(loginPassword.getValues()[0]));
				}
				DiscoverProp cameraPort = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"cameraPort");
				if (addressProp != null) {
					resourceVo.setCameraPort(Integer.parseInt(cameraPort.getValues()[0]));
				}
			} catch (InstancelibException e) {
				logger.error("获取摄像头属性失败：" + e.getMessage());
			}

		}

		CustomProp prop = null;
		try {
			prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
					"liablePerson");
		} catch (InstancelibException e) {
		}

		User user = new User();
		if (prop != null) {
			String[] accountIds = prop.getValues();
			if (accountIds.length > 0) {
				user = userApi.get(Long.valueOf(Long.parseLong(accountIds[0])));
				if (user != null) {
					resourceVo.setLiablePerson(user.getName());
				}
			}
		}
		try {
			prop = null;
			prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
					"maintainStaus");
		} catch (InstancelibException e) {
		}

		if (prop != null) {
			resourceVo.setMaintainStaus(prop.getValues()[0]);
			prop = null;
			try {
				prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
						"maintainStartTime");
			} catch (InstancelibException e) {
			}

			if (prop != null) {
				resourceVo.setMaintainStartTime(prop.getValues()[0]);
			}
			prop = null;
			try {
				prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
						"maintainEndTime");
			} catch (InstancelibException e) {
			}

			if (prop != null) {
				resourceVo.setMaintainEndTime(prop.getValues()[0]);
			}
		}
		return resourceVo;
	}

	@RequestMapping({ "/updateCameraInfo" })
	public JSONObject updateCameraInfo(String jsonData, HttpSession session) {
		Map<String,String> paramter = (Map<String,String>) JSONObject.parseObject(jsonData, HashMap.class);
		boolean result = cameraService.updateCameraInfo(paramter);
		return toSuccess(result);
	}
	
	//##################### zhanghb新增 #####################// 
	@SuppressWarnings("unchecked")
	@RequestMapping({ "/discoverCameraResource" })
	public JSONObject discoverCameraResource(String paramJson, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		Map<String, String> paramMap = JSONObject.parseObject(paramJson, HashMap.class);
		Map<String, Object> result = cameraService.discoverCameraResource(paramMap, user);
		return toSuccess(result);
	}

	@RequestMapping({ "/addCameraMonitor" })
	public JSONObject addCameraMonitor(String instanceIds, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		List<Long> instanceIdList = new ArrayList<Long>();
		if (instanceIds != null && !"".equals(instanceIds)) {
			String[] instanceIdArray = instanceIds.split(",");
			for (int i = 0; i < instanceIdArray.length; i++) {
				if (!"".equals(instanceIdArray[i])) {
					instanceIdList.add(Long.valueOf(instanceIdArray[i]));
				}
			}
		}
		return toSuccess(cameraService.addCameraMonitor(user, instanceIdList));
	}

	@RequestMapping({ "/getDiscoveryList" })
	public JSONObject getDiscoveryList(CameraDcListPageVo pageVo, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		pageVo = cameraService.getDiscoveryList(pageVo, user);
		return toSuccess(pageVo);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping({ "/testDiscover" })
	public JSONObject testDiscover(String paramData, long instanceId) {
		Map<String, String> paramMap = JSONObject.parseObject(paramData, HashMap.class);
		int result = cameraService.testDiscover(paramMap, instanceId);
		return toSuccess(result);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping({ "/updateDiscoverParam" })
	public JSONObject updateDiscoverParam(String paramData, long instanceId) {
		Map<String, String> paramMap = JSONObject.parseObject(paramData, HashMap.class);
		int result = cameraService.updateDiscoverParam(paramMap, instanceId);
		return toSuccess(result);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping({ "/reDiscover" })
	public JSONObject reDiscover(String paramData, long instanceId, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		Map<String, String> paramMap = JSONObject.parseObject(paramData, HashMap.class);
		Map<String, Object> resultMap = cameraService.reDiscover(paramMap, instanceId, user);
		return toSuccess(resultMap);
	}
	
	/** 检查是否发现完毕以保持session连接 */
	@RequestMapping({ "/checkFinishDiscover" })
	public JSONObject checkFinishDiscover(HttpSession session) {
		ILoginUser user = getLoginUser(session);
		Map<String, Object> userCache = user.getCache();
		Object result = userCache.get("isCameraDiscovering");
		return toSuccess(result);
	}
	
	public static void main(String arges[]) {
		Connection con;
		Statement stmt;
		try {
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");

			String url = "jdbc:microsoft:sqlserver://127.0.0.1:1433;databaseName=IMSDB";
			con = DriverManager.getConnection(url, "sa", "Abcd1234");
			stmt = con.createStatement();
			File file = new java.io.File("c:\\1230.jpg"); // 不要覆盖原来的文件，以便比较
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			ResultSet rs = stmt.executeQuery("select vqdImage from v_vqd_result where channelId = '34020000001320000016';");// 返回SQL语句查询结果集(集合)
			while (rs.next()) {
				InputStream fis = rs.getBinaryStream(1);
				byte[] b = new byte[10 * 1024];
				while (fis.read(b, 0, 10240) != -1) {
					fos.write(b, 0, 10240);
				}
				fos.flush();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
