package com.mainsteam.stm.camera.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import com.mainsteam.stm.camera.bo.CameraBo;
import com.mainsteam.stm.camera.bo.CameraVo;
import com.mainsteam.stm.camera.impl.dao.ICameraNewDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.camera.api.ICameraMonitorService;
import com.mainsteam.stm.camera.api.ICameraService;
import com.mainsteam.stm.camera.bo.CameraDcListPageVo;
import com.mainsteam.stm.camera.bo.CameraDcListVo;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceInstedDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.BatchResourceInstanceResult;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IDiscoverResourceApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.api.IResourceMonitorApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.profilelib.ProfileAutoRefreshService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.util.MetricDataUtil;
import com.mainsteam.stm.util.SecureUtil;

public class CameraServiceImpl implements ICameraService {
	private Logger logger = Logger.getLogger(CameraServiceImpl.class);

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private DiscoverPropService discoverPropService;

	@Resource
	private ModulePropService modulePropService;

	@Resource
	private CustomPropService customPropService;

	@Resource
	private ResourceInstanceDiscoveryService discoveryService;

	@Resource
	private MetricDataService metricDataService;

	@Resource
	private ProfileService profileService;

	@Resource
	private ILicenseCalcService licenseCalcService;

	@Resource
	private NodeService nodeService;

	@Resource
	private ISearchApi searchApi;

	@Resource
	private IDomainApi domainApi;

	@Resource
	private ProfileAutoRefreshService profileAutoRefreshService;

	@Resource
	private IResourceMonitorApi resourceMonitorApi;

	@Resource
	private IDiscoverResourceApi discoverResourceApi;

	@Autowired
	private ICustomResourceGroupApi customResourceGroupApi;

	List<ResourceInstance> allCameras = null;

	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;

    @Resource
    private ICameraMonitorService cameraMonitorService;

    @Resource
    private ICameraNewDao cameraNewDao;

	@Override
	public List<List<List<Object>>> getCameraDetial(Long instanceId,ILoginUser user) {
		List<List<List<Object>>> cameraDetail = resourceDetailInfoApi.getMetricFromXML(instanceId,user);
		//打印从xml获取的ui及其值 用于追踪错误
		if(logger.isInfoEnabled()){
			logger.info("Get Table from resourceDetailLayout.xml:" + JSONObject.toJSONString(cameraDetail));
		}
        //设置名称
        //Map<Long, CaremaMonitorBo> metricQueryMap = cameraMonitorService.getMetricQueryMap();
        ResourceInstance resourceInstance = null;
        try {
            resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
            if(resourceInstance == null){
                throw new InstancelibException(500,"获取资源实力失败:",instanceId);
            }
        } catch (InstancelibException e) {
           logger.error("获取资源实力失败:" + instanceId);
        }
        //CaremaMonitorBo caremaMonitorBo = metricQueryMap.get(instanceId);
        Map<String, Object> o = (Map<String, Object>)cameraDetail.get(0).get(0).get(1);
        o.put("value",resourceInstance.getShowName());
		// 处理诊断结果
		StringBuffer result = new StringBuffer();
		//result.append("诊断结果:");
		List<String> tempResult = new ArrayList<String>();
		Map<String, Object> resultList = (Map<String, Object>) cameraDetail.get(2).get(0).get(0);
		for (Object obj : cameraDetail.get(2).get(1)) {
			Map<String, Object> objMap = (Map<String, Object>) obj;
			if (objMap.containsKey("status") && !objMap.get("status").toString().equalsIgnoreCase("NORMAL")) {
				tempResult.add((objMap.get("title").toString()));
			}
		}
		for (Object obj : cameraDetail.get(2).get(2)) {
			Map<String, Object> objMap = (Map<String, Object>) obj;
			if (objMap.containsKey("status") && !objMap.get("status").toString().equalsIgnoreCase("NORMAL")) {
				tempResult.add((objMap.get("title").toString()));
			}
		}
		if (tempResult.size() == 0) {
			result.append("正常");
		} else {
			for (int i = 0; i < tempResult.size(); i++) {
				result.append(tempResult.get(i));
				if (i != (tempResult.size() - 1)) {
					result.append(",");
				}
			}
			result.append("异常!");
		}

		resultList.put("value", result.toString());
		if(logger.isInfoEnabled()){
			logger.info("诊断结果: ID " + instanceId + " " + result.toString());
		}
		
		//处理联通状态
		Map<String, Object> connStatus = (Map<String, Object>) cameraDetail.get(0).get(1).get(1);
		List<Map<String, Object>> availableList = resourceDetailInfoApi.getMetricByType(instanceId,	"AvailabilityMetric",true);
		if(availableList.size()>0 && availableList.get(0).containsKey("currentVal") && availableList.get(0).get("currentVal") != null){
			connStatus.put("value", availableList.get(0).get("currentVal"));
		}
        //添加视频类型
        Map<String, Object> stringObjectMap = (Map<String, Object>) cameraDetail.get(0).get(1).get(3);
        ModuleProp cameraType = null;
        try {
            cameraType = modulePropService.getPropByInstanceAndKey(instanceId, "cameraType");
            if(cameraType != null){
                String[] values = cameraType.getValues();
                if(values.length > 0){
                    String va = values[0] == null ? "--" : values[0];
                    stringObjectMap.put("value",va);
                }
            }
        } catch (InstancelibException e) {
            logger.error("查询摄像头类型错误",e);
        }

		// 添加视频连接信息 用来播放视频
		Map<String, Object> videoMap = (Map<String, Object>) cameraDetail.get(1).get(0).get(0);
		try {
			ResourceInstance camera = resourceInstanceService.getResourceInstance(instanceId);
			String[] metricS = {"devIP","devPort","devUser","devPwd","chnNo","channelID","keep"};
            List<MetricData> metricInfoDatas = metricDataService.getMetricInfoDatas(camera.getId(), metricS);
            for(MetricData me : metricInfoDatas){
                String metricId = me.getMetricId();
                String[] data = me.getData();
                String dataInfo = "";
                if(data != null && data.length > 0){
                    dataInfo = data[0];
                }
                if(metricS[0].equals(metricId)){
                    videoMap.put("ip", dataInfo);
                }else if(metricS[1].equals(metricId)){
                    videoMap.put("port", dataInfo);
                }else if(metricS[2].equals(metricId)){
                    videoMap.put("usr", dataInfo);
                }else if(metricS[3].equals(metricId)){
                    videoMap.put("pwd", dataInfo);
                }else if(metricS[4].equals(metricId)){
                    videoMap.put("chnno", dataInfo);
                }else if(metricS[5].equals(metricId)){
                    videoMap.put("chnid", dataInfo);
                }else if(metricS[6].equals(metricId)){
                    videoMap.put("systemtype", dataInfo);
                }
            }
            /*videoMap.put("ip", devIP.getData()[0]);
			if (camera.getDiscoverPropBykey("cameraPort") != null) {
				videoMap.put("port", camera.getDiscoverPropBykey("cameraPort")[0]);
			}
			if (camera.getDiscoverPropBykey("loginuser") != null) {
				videoMap.put("usr", camera.getDiscoverPropBykey("loginuser")[0]);
			}
			if (camera.getDiscoverPropBykey("loginpassword") != null) {
				videoMap.put("pwd", SecureUtil.pwdDecrypt(camera.getDiscoverPropBykey("loginpassword")[0]));
			}
			if (camera.getDiscoverPropBykey("chnno") != null) {
				videoMap.put("chnno", camera.getDiscoverPropBykey("chnno")[0]);
			}
			if (camera.getDiscoverPropBykey("channelId") != null) {
				videoMap.put("chnid", camera.getDiscoverPropBykey("channelId")[0]);
			}
			if (camera.getDiscoverPropBykey("keep") != null) {
				videoMap.put("systemtype", camera.getDiscoverPropBykey("keep")[0]);
			}*/
			if(logger.isInfoEnabled()){
				logger.info("视频设备信息 用于播放视频: ID " + instanceId + " " + JSONObject.toJSONString(videoMap));
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage());
		}
		return cameraDetail;
	}



	@Override
	public Map<String, Object> addCameraInstances(Map paramter) {
		Map<String, Object> result = new HashMap<String, Object>();
		Connection con = getConnection(paramter);
		Statement stmt = null;
		final List<ResourceInstance> cameras = new ArrayList<ResourceInstance>();
		final Object isOverwrite = paramter.get("overwrite");
		final boolean isAddMonitor = paramter.get("addDefaultMonitor") != null
				&& paramter.get("addDefaultMonitor").equals("checked");
		try {
			if (con != null) {
				String userName = paramter.get("dbUsername").toString();
				String password = paramter.get("dbPassword").toString();
				String ipAddress = paramter.get("IP").toString();
				String jdbcPort = paramter.get("jdbcPort").toString();
				String dbName = paramter.get("dbName").toString();
				String nodeGroupId = paramter.get("nodeGroupId").toString();
				long domainId = Long.parseLong(paramter.get("domainId").toString());
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(
						"select d.id,g.name,d.name,d.platfrom,d.channelId,d.address,d.gisx, d.gixy,d.devip,d.devport,d.devuser,d.devpwd,d.chnno,d.keep,g.id from v_device d join v_group g on d.groupid=g.id");
				int addSuccessNo = 0;
				int updateSuccessNo = 0;
				while (rs.next()) {
					ResourceInstance camera = new ResourceInstance();
					int platformId = rs.getInt(1);
					String groupName = rs.getString(2) == null ? "" : rs.getString(2);
					String cameraName = rs.getString(3) == null ? "" : rs.getString(3);
					String platformName = rs.getString(4) == null ? "" : rs.getString(4);
					String channelId = rs.getString(5);
					String address = rs.getString(6) == null ? "" : rs.getString(6);
					String gisx = rs.getString(7) == null ? "" : rs.getString(7);
					String gisy = rs.getString(8) == null ? "" : rs.getString(8);
					String cameraIP = rs.getString(9) == null ? "" : rs.getString(9);
					int cameraPort = rs.getInt(10);
					String loginuser = rs.getString(11) == null ? "" : rs.getString(11);
					String loginpwd = rs.getString(12) == null ? "" : rs.getString(12);
					int chnno = rs.getInt(13);
					int keep = rs.getInt(14);
					int groupId = rs.getInt(15);

					List<DiscoverProp> discoverProps = new ArrayList<DiscoverProp>();
					// 设备平台组名
					DiscoverProp groupProp = new DiscoverProp();
					groupProp.setKey("groupName");
					groupProp.setValues(new String[] { groupName });
					discoverProps.add(groupProp);
					// 设备平台ID
					DiscoverProp platFormId = new DiscoverProp();
					platFormId.setKey("platFormId");
					platFormId.setValues(new String[] { String.valueOf(platformId) });
					discoverProps.add(platFormId);
					// 设备平台名称
					DiscoverProp platFormName = new DiscoverProp();
					platFormName.setKey("platFormName");
					platFormName.setValues(new String[] { platformName });
					discoverProps.add(platFormName);
					// 摄像头地址
					DiscoverProp addressProp = new DiscoverProp();
					addressProp.setKey("address");
					addressProp.setValues(new String[] { address });
					discoverProps.add(addressProp);
					// 经纬度
					DiscoverProp gisxProp = new DiscoverProp();
					gisxProp.setKey("gisx");
					gisxProp.setValues(new String[] { gisx });
					discoverProps.add(gisxProp);
					DiscoverProp gisyProp = new DiscoverProp();
					gisyProp.setKey("gisy");
					gisyProp.setValues(new String[] { gisy });
					discoverProps.add(gisyProp);
					// 摄像头端口
					DiscoverProp portProp = new DiscoverProp();
					portProp.setKey("cameraPort");
					portProp.setValues(new String[] { String.valueOf(cameraPort) });
					discoverProps.add(portProp);
					// 摄像头登录用户名 密码
					DiscoverProp userProp = new DiscoverProp();
					userProp.setKey("loginuser");
					userProp.setValues(new String[] { loginuser });
					discoverProps.add(userProp);
					DiscoverProp passProp = new DiscoverProp();
					passProp.setKey("loginpassword");
					passProp.setValues(new String[] { SecureUtil.pwdEncrypt(loginpwd) });
					discoverProps.add(passProp);
					// 通道NO
					DiscoverProp chnnoProp = new DiscoverProp();
					chnnoProp.setKey("chnno");
					chnnoProp.setValues(new String[] { String.valueOf(chnno) });
					discoverProps.add(chnnoProp);
					// 设备品牌
					DiscoverProp keepProp = new DiscoverProp();
					chnnoProp.setKey("keep");
					chnnoProp.setValues(new String[] { String.valueOf(keep) });
					discoverProps.add(keepProp);

					// channelId, 设备平台唯一标识
					DiscoverProp channelIdprop = new DiscoverProp();
					channelIdprop.setKey("channelId");
					channelIdprop.setValues(new String[] { channelId });
					discoverProps.add(channelIdprop);
					// db username
					DiscoverProp userNameprop = new DiscoverProp();
					userNameprop.setKey("dbUsername");
					userNameprop.setValues(new String[] { userName });
					discoverProps.add(userNameprop);
					// db password
					DiscoverProp dbPasswordprop = new DiscoverProp();
					dbPasswordprop.setKey("dbPassword");
					dbPasswordprop.setValues(new String[] { SecureUtil.pwdEncrypt(password) });
					discoverProps.add(dbPasswordprop);
					// db IP
					DiscoverProp IPprop = new DiscoverProp();
					IPprop.setKey("IP");
					IPprop.setValues(new String[] { ipAddress });
					discoverProps.add(IPprop);
					// jdbc port
					DiscoverProp jdbcPortprop = new DiscoverProp();
					jdbcPortprop.setKey("jdbcPort");
					jdbcPortprop.setValues(new String[] { jdbcPort });
					discoverProps.add(jdbcPortprop);
					// db name
					DiscoverProp dbNameprop = new DiscoverProp();
					dbNameprop.setKey("dbName");
					dbNameprop.setValues(new String[] { dbName });
					discoverProps.add(dbNameprop);
					// nodeGroupId
					DiscoverProp nodeGroupIdprop = new DiscoverProp();
					nodeGroupIdprop.setKey("nodeGroupId");
					nodeGroupIdprop.setValues(new String[] { nodeGroupId });
					discoverProps.add(nodeGroupIdprop);
					// db type
					DiscoverProp dbTypeprop = new DiscoverProp();
					dbTypeprop.setKey("dbType");
					dbTypeprop.setValues(new String[] { "SQLServer" });
					discoverProps.add(dbTypeprop);
					// resource id
					DiscoverProp resouceIDprop = new DiscoverProp();
					resouceIDprop.setKey("resourceId");
					resouceIDprop.setValues(new String[] { "CameraRes" });
					discoverProps.add(resouceIDprop);

					// groupId
					DiscoverProp groupIdprop = new DiscoverProp();
					groupIdprop.setKey("groupId");
					groupIdprop.setValues(new String[] { String.valueOf(groupId) });
					discoverProps.add(groupIdprop);

					camera.setCategoryId("Cameras");
					camera.setDiscoverProps(discoverProps);
					camera.setDiscoverNode(nodeGroupId);
					camera.setDomainId(domainId);
					camera.setGeneratePrimaryKey(true);
					camera.setDiscoverWay(DiscoverWayEnum.NONE);
					camera.setResourceId("CameraRes");
					camera.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
					camera.setShowIP(cameraIP);
					camera.setName(cameraName);
					camera.setShowName(cameraName);
					if(logger.isInfoEnabled()){
						logger.info("从视频平台查询到的设备:" + JSONObject.toJSONString(camera));
					}
					cameras.add(camera);
				}
				// 判断是否重复资源

				// 查询成功后启用新线程添加设备 避免前台长时间卡着
				new Thread(new Runnable() {
					public void run() {
						validateRepeat(cameras);
						for (ResourceInstance camera : cameras) {
							boolean isRepeat = camera.isRepeatValidate();
							Long instanceID = 0L;
							if (isRepeat && isOverwrite != null) {
								updateCamera(camera);
								if(logger.isInfoEnabled()){
									logger.info("重复设备，选择更新:" + JSONObject.toJSONString(camera));
								}
							} else if (!isRepeat) {
								instanceID = addNewCameras(camera);
								if(logger.isInfoEnabled()){
									logger.info("添加成功 " + JSONObject.toJSONString(camera));
								}
							}
							if (isAddMonitor && instanceID != 0L) {
								joinMonitor(instanceID);
							}
						}
						allCameras = null;
					}
				}).start();

				result.put("status", "1");
				result.put("comment", "共添加摄像头" + addSuccessNo + "个，更新" + updateSuccessNo + "个。");

			} else {
				result.put("status", "0");
				result.put("comment", "发现失败,请检查参数");
			}

		} catch (SQLException e) {
			logger.error(e);
			result.put("status", "0");
			result.put("comment", e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}

	// 自动发现时更新
	private void updateCamera(ResourceInstance ca) {
		try {
			String channelId = ca.getDiscoverPropBykey("channelId")[0];
			ResourceInstance instance = getCameraResourceByChannelId(channelId);
			Long instanceId = instance.getId();
			for (DiscoverProp prop : ca.getDiscoverProps()) {
				prop.setInstanceId(instanceId);
			}
			discoverPropService.updateProps(ca.getDiscoverProps());
			resourceInstanceService.updateResourceInstanceName(instanceId, ca.getShowName());
			resourceInstanceService.updateResourceInstanceShowIP(instanceId, ca.getShowIP());
		} catch (InstancelibException e) {
			logger.error("更新摄像头失败  error message:" + e.getMessage());
		}
	}

	private Map covertProp2Map(List<DiscoverProp> discoverProps) {
		Map parameter = new HashMap<String, Object>();
		if (discoverProps != null) {
			for (DiscoverProp prop : discoverProps) {
				parameter.put(prop.getKey(), prop.getValues()[0]);
			}
		}
		return parameter;
	}

	private void validateRepeat(List<ResourceInstance> cameras) {
		for (ResourceInstance ca : cameras) {
			String channelId = ca.getDiscoverPropBykey("channelId")[0];
			ResourceInstance instance = getCameraResourceByChannelId(channelId);
			if (instance == null) {
				ca.setRepeatValidate(false);
			} else {
				ca.setRepeatValidate(true);
			}
		}
	}

	private ResourceInstance getCameraResourceByChannelId(String channelId) {
		ResourceInstance instance = null;
		try {
			if (allCameras == null) {
				allCameras = resourceInstanceService.getParentInstanceByCategoryId("Cameras");
			}
			for (ResourceInstance ca : allCameras) {
				if (ca.getDiscoverPropBykey("channelId")[0].equalsIgnoreCase(channelId)) {
					instance = ca;
				}
			}
		} catch (InstancelibException e) {
			logger.error("通过channelId获取摄像头失败, channelId:" + channelId + " error message:" + e.getMessage());
		}
		return instance;
	}

	// 保存发现的设备到数据库
	private long addNewCameras(ResourceInstance camera) {
		ResourceInstanceResult result;
		try {
			result = resourceInstanceService.addResourceInstance(camera);
			return result.getResourceInstanceId();
		} catch (InstancelibException e) {
			logger.error("添加摄像头失败" + e.getMessage() + camera.toString());
			return 0L;
		}
	}

	// 添加设备到监控
	private int joinMonitor(long id) {
		try {
			return resourceMonitorApi.openMonitor(id);
		} catch (Exception e) {
			logger.error("添加摄像头监控失败,id:" + id + " error message: " + e.getMessage());
			return 0;
		}
	}

	@Override
	public Map<String, Object> discovery(Map paramter) {
		Connection con = getConnection(paramter);
		Statement stmt = null;
		int deviceCount = 0;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (con != null) {
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select count(*) from v_device");// 返回SQL语句查询结果集(集合)
				while (rs.next()) {
					deviceCount = rs.getInt(1);
				}
				result.put("status", "1");
				result.put("comment", "共发现摄像头" + deviceCount + "个");
			} else {
				result.put("status", "0");
				result.put("comment", "发现失败,请检查参数");
			}

		} catch (SQLException e) {
			logger.error("Discovery Camera Device failed " + e);
			result.put("status", "0");
			result.put("comment", e.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}

	public Connection getConnection(Map paramter) {
		Connection con = null;
		try {
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			String userName = paramter.get("dbUsername").toString();
			String password = paramter.get("dbPassword").toString();
			String ipAddress = paramter.get("IP").toString();
			String jdbcPort = paramter.get("jdbcPort").toString();
			String dbName = paramter.get("dbName").toString();
			String url = "jdbc:microsoft:sqlserver://" + ipAddress + ":" + jdbcPort + ";databaseName=" + dbName;
			con = DriverManager.getConnection(url, userName, password);
		} catch (ClassNotFoundException e) {
			logger.error("Load JDBC driver failed " + e.getMessage());

		} catch (SQLException e) {
			logger.error("Get SQL Server connection failed " + e.getMessage()  + " DB information: " + JSONObject.toJSONString(paramter));
		}
		return con;
	}

	@Override
	public boolean updateCameraInfo(Map paramter) {
		try {
			Long instanceId = Long.parseLong(paramter.get("id").toString());
			//List<DiscoverProp> props = covertParameterToProp(paramter, instanceId);
			//discoverPropService.updateProps(props);
			resourceInstanceService.updateResourceInstanceName(instanceId, paramter.get("sourceName").toString().trim());
			ModuleProp moduleProp = new ModuleProp();
			moduleProp.setInstanceId(instanceId);
			moduleProp.setKey("cameraType");
            String cameraType = paramter.get("cameraType").toString();
            moduleProp.setValues(new String[]{cameraType});
            modulePropService.updateProp(moduleProp);
			//resourceInstanceService.updateResourceInstanceShowIP(instanceId, paramter.get("ipAddress").toString());
			return true;
		} catch (InstancelibException e) {
			logger.error("更新摄像头失败  error message:" + e.getMessage());
			return false;
		}
	}

	private List<DiscoverProp> covertParameterToProp(Map para, long id) {
		List<DiscoverProp> props = new ArrayList<DiscoverProp>();
		if (para.get("loginUser") != null) {
			DiscoverProp p = new DiscoverProp();
			p.setInstanceId(id);
			p.setKey("loginuser");
			p.setValues(new String[] { para.get("loginUser").toString() });
			props.add(p);
		}
		if (para.get("loginPassword") != null) {
			DiscoverProp p = new DiscoverProp();
			p.setInstanceId(id);
			p.setKey("loginpassword");
			p.setValues(new String[] { SecureUtil.pwdEncrypt(para.get("loginPassword").toString()) });
			props.add(p);
		}
		if (para.get("cameraPort") != null) {
			DiscoverProp p = new DiscoverProp();
			p.setInstanceId(id);
			p.setKey("cameraPort");
			p.setValues(new String[] { para.get("cameraPort").toString() });
			props.add(p);
		}
		if (para.get("cameraType") != null) {
			DiscoverProp p = new DiscoverProp();
			p.setInstanceId(id);
			p.setKey("cameraType");
			p.setValues(new String[] { para.get("cameraType").toString() });
			props.add(p);
		}
		if (para.get("cameraAddress") != null) {
			DiscoverProp p = new DiscoverProp();
			p.setInstanceId(id);
			p.setKey("address");
			p.setValues(new String[] { para.get("cameraAddress").toString() });
			props.add(p);
		}
		return props;
	}

	@Override
	public Map getCameraConInfo(Long instanceId) {
		Map<String, String> con = new HashMap<String, String>();
		try {
		    String[] mets = new String[]{"devIP","devPort","devUser","devPwd","chnNo","channelID","keep"};
            List<MetricData> metricInfoDatas = metricDataService.getMetricInfoDatas(instanceId, mets);
            for(MetricData mt : metricInfoDatas){
                String[] data = mt.getData();
                String metricId = mt.getMetricId();
                switch (metricId){
                    case "devIP":
                        con.put(metricId,data[0]);
                        break;
                    case "devPort":
                        con.put(metricId,data[0]);
                        break;
                    case "devUser":
                        con.put(metricId,data[0]);
                        break;
                    case "devPwd":
                        con.put(metricId,data[0]);
                        break;
                    case "chnNo":
                        con.put(metricId,data[0]);
                        break;
                    case "channelID":
                        con.put(metricId,data[0]);
                        break;
                    case "keep":
                        con.put(metricId,data[0]);
                        break;
                }
            }
           /* ResourceInstance camera = resourceInstanceService.getResourceInstance(instanceId);
			con.put("ip", camera.getShowIP());
			if (camera.getDiscoverPropBykey("cameraPort") != null) {
				con.put("port", camera.getDiscoverPropBykey("cameraPort")[0]);
			}
			if (camera.getDiscoverPropBykey("loginuser") != null) {
				con.put("usr", camera.getDiscoverPropBykey("loginuser")[0]);
			}
			if (camera.getDiscoverPropBykey("loginpassword") != null) {
				con.put("pwd", SecureUtil.pwdDecrypt(camera.getDiscoverPropBykey("loginpassword")[0]));
			}
			if (camera.getDiscoverPropBykey("chnno") != null) {
				con.put("chnno", camera.getDiscoverPropBykey("chnno")[0]);
			}
			if (camera.getDiscoverPropBykey("channelId") != null) {
				con.put("chnid", camera.getDiscoverPropBykey("channelId")[0]);
			}
			if (camera.getDiscoverPropBykey("keep") != null) {
				con.put("systemtype", camera.getDiscoverPropBykey("keep")[0]);
			}*/
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		//logger.info("Get Camera Connection info   ID: " + instanceId + "  Details:" + JSONObject.toJSONString(con));
		return con;
	}

	@Override
	public List<CustomGroupBo> getList(long userId) {
		List<CustomGroupBo> list = customResourceGroupApi.getList(userId);
		// 获取admin下分组信息 并把视频设备分组添加进去 让所有用户都能看到
		if (userId != 1L) {
			List<CustomGroupBo> adminList = customResourceGroupApi.getList(1L);
			Map<String, CustomGroupBo> allGroups = new HashMap<String, CustomGroupBo>();
			for (CustomGroupBo bo : adminList) {
				allGroups.put(String.valueOf(bo.getId()), bo);
			}
			for (CustomGroupBo bo : adminList) {
				if (isCameraGroup(bo.getId(), allGroups)) {
					list.add(bo);
				}
			}
		}
		return list;
	}

	public boolean isCameraGroup(long id, Map<String, CustomGroupBo> allGroups) {
		boolean result = false;
		if (id == 999999L) {
			result = true;
		} else {
			CustomGroupBo cameraGroup = allGroups.get(String.valueOf(id));
			if (cameraGroup.getPid() != null) {
				result = isCameraGroup(cameraGroup.getPid(), allGroups);
			} else {
				result = false;
			}
		}
		return result;
	}

	//##################### zhanghb新增 #####################// 	
	@Override
	public Map<String, Object> discoverCameraResource(
			Map<String, String> paramMap, ILoginUser user) {
		logger.info("//--------------- 视频设备资源发现START ---------------//");
		Long startTime = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 是否正在发现
		Map<String, Object> userCache = user.getCache();
		Object flag = userCache.get("isCameraDiscovering");
		
		if (flag != null && (boolean)flag ) {
			resultMap.put("isSuccess", false);
			resultMap.put("failMessage", "当前用户正在发现视频资源，请稍候重试.");
		} else {
			userCache.put("isCameraDiscovering", true);
			// 用于存放发现获得的监控摄像头的name→instanceId的键值对
			List<Map<String, Object>> tableDatas = new ArrayList<Map<String, Object>>();
			ResourceInstanceDiscoveryParameter dcParam = createDiscoverParameter(paramMap);
			DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(dcParam);
			if(logger.isDebugEnabled()) {
				logger.debug("视频监控平台发现参数：" + JSON.toJSONString(dcParam));
				logger.debug("视频监控平台发现结果：" + drir.isSuccess() + "-" + drir.getCode());
			}
			if (drir.isSuccess()) {
				try {
					long domainId = Long.valueOf(paramMap.get("domainId"));
					String discoverNode = paramMap.get("DCS");
					ResourceInstance ri = drir.getResourceIntance();
					long instanceId = ri.getId();
					MetricData cameraList = metricDataService.catchRealtimeMetricData(instanceId, "cameraList");
					if(logger.isDebugEnabled()) {
						logger.debug("通过平台得到摄像头信息列表：");
						logger.debug(JSON.toJSONString(cameraList.getData()));
					}
					List<DiscoverProp> discoverProps = ri.getDiscoverProps();
					List<Map<String, String>> mapList = convertMetricData2MapList(cameraList.getData());
					List<Long> discoveryIds = createCameraResourceInstance(mapList, tableDatas, discoverProps, domainId, discoverNode, instanceId);
					List<Long> cameraIds = getCameraIdsByPlatformId(instanceId);
					logger.info("cameraIds:" + cameraIds);
					logger.info("discoveryIds:" + discoveryIds);
					cameraIds.removeAll(discoveryIds);
					logger.info("cameraIds:" + cameraIds);
					if(cameraIds.size() > 0) {
						resourceInstanceService.removeResourceInstances(cameraIds);
					}
					resourceInstanceService.refreshResourceInstance(ri);
					resultMap.put("isSuccess", true);
					resultMap.put("rows", tableDatas);
				} catch (MetricExecutorException e) {
					e.printStackTrace();
					logger.error("即时采集信息指标出错: " + e.getMessage());
				} catch (InstancelibException e) {
					e.printStackTrace();
					logger.error("资源实例入库出错: " + e.getMessage());
				}
			} else {
				resultMap.put("isSuccess", false);
				resultMap.put("errorCode", drir.getCode());
			}
			userCache.put("isCameraDiscovering", false);
		}
		Long endTime = System.currentTimeMillis();
		resultMap.put("discoverTime", calcTime(endTime - startTime));
		logger.info("//--------------- 视频设备资源发现END ---------------//");
		logger.info("发现资源用时：" + calcTime(endTime - startTime));
		return resultMap;
	}

	@Override
	public boolean addCameraMonitor(ILoginUser user, List<Long> instanceIds) {
		boolean result = false;
		logger.info("开启监控资源id列表");
		logger.info(instanceIds);
		if (instanceIds != null && instanceIds.size() > 0) {
			try {
//				List<Long> list = new ArrayList<Long>();
//				for (int i = 0; i < instanceIds.size(); i++) {
//					list.add(instanceIds.get(i));
//					saveSearchRel(instanceIds.get(i));
//				}
//				logger.info(list);
//				profileService.enableMonitor(list);
				profileService.addMonitorUseDefault(instanceIds);
				result = true;
			} catch (Exception e) {
				result = false;
				logger.error("batchOpenMonitor", e);
			}
		}
		return result;
	}

	@Override
	public CameraDcListPageVo getDiscoveryList(CameraDcListPageVo pageVo,
			ILoginUser user) {
		// 采集服务器节点信息
		Map<String, String> dcsNodeGroupMap = new HashMap<String, String>();
		try {
			NodeTable nodeTable = nodeService.getNodeTable();
			if (nodeTable != null) {
				List<NodeGroup> nodeGroups = nodeTable.getGroups();
				for (int i = 0; nodeGroups != null && i < nodeGroups.size(); i++) {
					NodeGroup nodeGroup = nodeGroups.get(i);
					dcsNodeGroupMap.put(String.valueOf(nodeGroup.getId()),
							nodeGroup.getName());
				}
			}
		} catch (NodeException e) {
			logger.error("Get NodeTable Error: ", e);
		}
		List<CameraDcListVo> dcVoList = new ArrayList<CameraDcListVo>();
		try {
			List<ResourceInstance> ress = resourceInstanceService
					.getResourceInstanceByResourceId("EastWitPlatform");
			for (int i = 0; i < ress.size(); i++) {
				ResourceInstance ri = ress.get(i);
				CameraDcListVo vo = new CameraDcListVo();
				vo.setId(ri.getId());
				vo.setResourceId(ri.getResourceId());
				vo.setDomainId(ri.getDomainId());
				vo.setDiscoverNode(ri.getDiscoverNode());
				vo.setDiscoverNodeName(dcsNodeGroupMap.get(ri.getDiscoverNode()));
				vo.setDomainName(null == domainApi.get(ri.getDomainId()) ? ""
						: domainApi.get(ri.getDomainId()).getName());

				vo.setIP(ri.getShowIP());
				vo.setNodeGroupId(ri.getDiscoverNode());

				String[] dbNameArr = ri.getDiscoverPropBykey("dbName");
				vo.setDbName(dbNameArr != null && dbNameArr.length > 0 ? dbNameArr[0] : "");
				String[] dbTypeArr = ri.getDiscoverPropBykey("dbType");
				vo.setDbType(dbTypeArr != null && dbTypeArr.length > 0 ? dbTypeArr[0] : "");
				String[] dbUsernameArr = ri.getDiscoverPropBykey("dbUsername");
				vo.setDbUsername(dbUsernameArr != null && dbUsernameArr.length > 0 ? dbUsernameArr[0] : "");
				String[] dbPasswordArr = ri.getDiscoverPropBykey("dbPassword");
				vo.setDbPassword(dbPasswordArr != null && dbPasswordArr.length > 0 ? SecureUtil
						.pwdDecrypt(dbPasswordArr[0]) : "");
				String[] jdbcPortArr = ri.getDiscoverPropBykey("jdbcPort");
				vo.setJdbcPort(jdbcPortArr != null && jdbcPortArr.length > 0 ? jdbcPortArr[0] : "");
				String[] otherParamsArr = ri.getDiscoverPropBykey("otherParams");
				vo.setOtherParams(otherParamsArr != null && otherParamsArr.length > 0 ? otherParamsArr[0] : "");
				dcVoList.add(vo);
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		pageVo.setTotalRecord(dcVoList.size());

		int startIndex = (int) Math.min(pageVo.getStartRow(), dcVoList.size());
		int endIndex = (int) Math.min(pageVo.getStartRow() + pageVo.getRowCount(), dcVoList.size());
		dcVoList = dcVoList.subList(startIndex, endIndex);

		setAutoRefreshInfo(dcVoList);
		pageVo.setCameraDcList(dcVoList);
		return pageVo;
	}

	@Override
	public int testDiscover(Map paramMap, long instanceId) {
		int result = 0;
		DiscoverResourceIntanceResult drir = discoveryTest(paramMap);
		if (drir.isSuccess()) {
			result = 1;
		} else {
			result = drir.getCode();
		}
		return result;
	}

	@Override
	public int updateDiscoverParam(Map paramMap, long instanceId) {
		int result = 1;
		Map<Long, Long> domainArgMap = new HashMap<Long, Long>();
		String domainidKey = "domainId";
		// 避免多余信息插入资源的发现参数
		Map mapForAutoRefreshInfo = new HashMap();
		if (paramMap.containsKey("ifAutoRefresh")) {
			mapForAutoRefreshInfo.put("ifAutoRefresh", paramMap.get("ifAutoRefresh"));
			paramMap.remove("ifAutoRefresh");
			if (paramMap.containsKey("autoRefreshCycleDay")) {
				mapForAutoRefreshInfo.put("autoRefreshCycleDay", paramMap.get("autoRefreshCycleDay"));
				paramMap.remove("autoRefreshCycleDay");
			}
		}
		List<DiscoverProp> discoverProps = new ArrayList<DiscoverProp>();
		try {
			DiscoverResourceIntanceResult dris = discoveryTest(paramMap);
			if (dris.isSuccess()) {
				ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
				List<Long> instanceIds = getCameraIdsByPlatformId(ri.getId());				
				for (int i = 0; i < instanceIds.size(); i++) {
					Long tmpId = instanceIds.get(i);
					// 添加属性
					discoverProps.addAll(getDiscoverProps(paramMap, tmpId));
					if (paramMap.get(domainidKey) != null
							&& !"".equals(paramMap.get(domainidKey))) {
						domainArgMap.put(tmpId, Long.valueOf((String) paramMap
								.get(domainidKey)));
					}
				}
				discoverProps.addAll(getDiscoverProps(paramMap, instanceId));
				if (!discoverProps.isEmpty()) {
					discoverPropService.updateProps(discoverProps);
				}
				// 判断是否要更新域
				if (paramMap.get(domainidKey) != null
						&& !"".equals(paramMap.get(domainidKey))) {
					domainArgMap.put(instanceId,
							Long.valueOf((String) paramMap.get(domainidKey)));
				}
				if (!String.valueOf(ri.getDomainId()).equals(
						paramMap.get(domainidKey))
						&& paramMap.get(domainidKey) != null
						&& !domainArgMap.isEmpty()) {
					resourceInstanceService.updateResourceInstanceDomain(domainArgMap);
				}
			} else {
				result = 2;
			}
		} catch (InstancelibException e) {
			result = 0;
			logger.error("vm updateDiscoverParamter", e);
		}
		// 更新自动刷新参数
		if (result == 1) {
			updateAutoRefreshInfo(mapForAutoRefreshInfo, instanceId);
		}
		return result;
	}

	@Override
	public Map<String, Object> reDiscover(Map<String, String> paramMap, long platformId, ILoginUser user) {
		logger.info("//--------------- 视频设备资源重新发现START ---------------//");
		Long startTime = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 是否正在发现
		Map<String, Object> userCache = user.getCache();
		Object flag = userCache.get("isCameraDiscovering");
		
		if (flag != null && (boolean)flag ) {
			resultMap.put("isSuccess", false);
			resultMap.put("failMessage", "当前用户正在发现视频资源，请稍候重试.");
		} else {
			userCache.put("isCameraDiscovering", true);
			// 用于存放发现获得的监控摄像头的name→instanceId的键值对
			List<Map<String, Object>> tableDatas = new ArrayList<Map<String, Object>>();
			
			ResourceInstanceDiscoveryParameter dcParam = createDiscoverParameter(paramMap);
			DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(dcParam);
			if(logger.isDebugEnabled()) {
				logger.debug("视频监控平台发现参数：" + JSON.toJSONString(dcParam));
				logger.debug("视频监控平台发现结果：" + drir.isSuccess() + "-" + drir.getCode());
			}
			if (drir.isSuccess()) {
				try {
					long domainId = Long.valueOf(paramMap.get("domainId"));
					String discoverNode = paramMap.get("DCS");
					ResourceInstance ri = drir.getResourceIntance();
					long instanceId = ri.getId();
					MetricData cameraList = metricDataService.catchRealtimeMetricData(instanceId, "cameraList");
					if(logger.isDebugEnabled()) {
						logger.debug("通过平台得到摄像头信息列表：");
						logger.debug(JSON.toJSONString(cameraList.getData()));
					}
					List<DiscoverProp> discoverProps = ri.getDiscoverProps();
					List<Map<String, String>> mapList = convertMetricData2MapList(cameraList.getData());
					List<Long> discoveryIds = createCameraResourceInstance(mapList, tableDatas, discoverProps, domainId, discoverNode, instanceId);
					
					List<Long> cameraIds = getCameraIdsByPlatformId(instanceId);
					cameraIds.removeAll(discoveryIds);
					if(cameraIds.size() > 0) {
						resourceInstanceService.removeResourceInstances(cameraIds);
					}
					resourceInstanceService.refreshResourceInstance(ri);
					
					resultMap.put("isSuccess", true);
					resultMap.put("rows", tableDatas);
				} catch (MetricExecutorException e) {
					e.printStackTrace();
					logger.error("即时采集信息指标出错: " + e.getMessage());
				} catch (InstancelibException e) {
					e.printStackTrace();
					logger.error("资源实例入库出错: " + e.getMessage());
				}
			} else {
				resultMap.put("isSuccess", false);
				resultMap.put("errorCode", drir.getCode());
			}
			userCache.put("isCameraDiscovering", false);
		}
		Long endTime = System.currentTimeMillis();
		resultMap.put("discoverTime", calcTime(endTime - startTime));
		logger.info("//--------------- 视频设备资源重新发现END ---------------//");
		return resultMap;
	}

    public DiscoverResourceIntanceResult discoveryTest(Map paramMap) {
		ResourceInstanceDiscoveryParameter paramObj = createTestDiscoverParameter(paramMap);
		DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(paramObj);
		return drir;
	}

	/** 获取自定义属性中存储的摄像头实例Id，并解析为Long的集合 */
	private List<Long> getCameraIdsByPlatformId(Long platformId) {
		List<Long> cameraIds = new ArrayList<Long>();
		try {
			List<ResourceInstance> ris = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");
			for (int i = 0; i < ris.size(); i++) {
				ResourceInstance ri = ris.get(i);
				CustomProp prop = customPropService.getPropByInstanceAndKey(ri.getId(), "platformId");
				if(prop != null && prop.getValues().length > 0) {
					if(String.valueOf(platformId).equals(prop.getValues()[0])) {
						cameraIds.add(ri.getId());
					}
				}
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
			logger.error("获取EastWitCamera类型实例对象集合失败");
		}
		return cameraIds;
	}

	/** 
	 * 获取自动刷新策略并设置到视图对象中
	 * 注：自动刷新策略由Server组实现，其实现代码只支持少数几个类型的资源，对视频资源不适用
	 */
	private void setAutoRefreshInfo(List<CameraDcListVo> vos) {
		for (CameraDcListVo vo : vos) {
			ProfileAutoRefresh par = profileAutoRefreshService.getAutoRefreshProfileByInstance(vo.getId());
			if (null != par && par.getIsUse() == 1) {
				vo.setIfAutoRefresh(true);
				vo.setAutoRefreshCycleDay(par.getExecutRepeat());
			} else {
				vo.setIfAutoRefresh(false);
			}
		}
	}

	private void updateAutoRefreshInfo(Map paramter, long instanceId) {
		ProfileAutoRefresh par = profileAutoRefreshService
				.getAutoRefreshProfileByInstance(instanceId);
		if (paramter.containsKey("ifAutoRefresh")
				&& "on".equals(paramter.get("ifAutoRefresh"))) {
			if (null == par) {
				par = new ProfileAutoRefresh();
				par.setInstanceId(instanceId);
				par.setIsUse(1);
				par.setExecutRepeat(Integer.valueOf((String) paramter.get("autoRefreshCycleDay")));
				profileAutoRefreshService.addAutoRefreshProfile(par);
			} else {
				par.setExecutRepeat(Integer.valueOf((String) paramter.get("autoRefreshCycleDay")));
				par.setIsUse(1);
				profileAutoRefreshService.updateAutoRefreshProfile(par);
			}
		} else {
			if (null != par) {
				par.setIsUse(0);
				profileAutoRefreshService.updateAutoRefreshProfile(par);
			}
		}
	}
	
	/** 添加相应的搜索信息 */
//	private void saveSearchRel(long instanceId) {
//		ResourceBizRel rbr = new ResourceBizRel(instanceId, instanceId, "摄像头-发现");
//		searchApi.saveSearchResource(rbr);
//	}

	private List<Long> createCameraResourceInstance(
			List<Map<String, String>> mapList,
			List<Map<String, Object>> tableDatas, 
			List<DiscoverProp> props,
			long domainId, String discoverNode, long platformId)
			throws InstancelibException {
		List<ResourceInstance> riList = new ArrayList<ResourceInstance>();
		logger.info("依据得到监控平台指标值解析为资源实例START");
		List<Long> discoveryIds = new ArrayList<Long>();
		// ---------------------- 获取模型属性及唯一标识的Id的key ----------------------//
		ResourceDef resourceDef = capacityService.getResourceDefById("EastWitCamera");
		ResourceInstedDef instedDef = resourceDef.getInstantiationDef();
		String[] instedNames = instedDef.getInstanceId();
		ResourcePropertyDef[] propDef = resourceDef.getPropertyDefs();
		String[] mpropNames = new String[propDef.length];
		for (int i = 0; i < propDef.length; i++) {
			mpropNames[i] = propDef[i].getId();
		}
		CustomProp platformProp = new CustomProp();
		platformProp.setKey("platformId");
		platformProp.setValues(new String[]{ String.valueOf(platformId) });
		
		// ---------------------- 转换为带有模型属性和发现树形的资源实例 ----------------------//
		for (int i = 0; i < mapList.size(); i++) {
			Map<String, String> map = mapList.get(i);
			ResourceInstance ri = new ResourceInstance();
			ri.setName(map.get("name"));
			ri.setShowName(map.get("name"));
			ri.setResourceId("EastWitCamera");
			ri.setCategoryId("SurveillanceCamera");
			ri.setDomainId(domainId);
			ri.setDiscoverNode(discoverNode);
			ri.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
			ri.setShowIP(map.get("devIP"));
			//---------------------- 创建发现属性 ----------------------//
			List<DiscoverProp> disocoverProps = new ArrayList<DiscoverProp>(props);
			DiscoverProp prop = new DiscoverProp();
			prop.setKey("channelID");
			prop.setValues(new String[] { map.get("channelID") });
			disocoverProps.add(prop);
			ri.setDiscoverProps(disocoverProps);
			//---------------------- 创建模型属性 ----------------------//
			List<ModuleProp> moduleProps = getModuleProps(map, instedNames, mpropNames);
			ri.setModuleProps(moduleProps);
			//---------------------- 创建自定义属性 --------------------//
			List<CustomProp> customProps = new ArrayList<CustomProp>();
			customProps.add(platformProp);
			ri.setCustomProps(customProps);
			
			riList.add(ri);
		}
		// 批量入库
		BatchResourceInstanceResult batchRir = resourceInstanceService.addResourceInstances(riList);
		List<ResourceInstance> addInstances = batchRir.getAddResourceInstances();
		for (int i = 0; i < addInstances.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();			
			map.put("id", addInstances.get(i).getId());
			map.put("name", addInstances.get(i).getName());
			discoveryIds.add(addInstances.get(i).getId());
			tableDatas.add(map);
		}
		logger.info("addInstances");
		logger.info(discoveryIds);
		List<ResourceInstance> updateInstances = batchRir.getUpdateResourceInstances();
		for (int i = 0; i < updateInstances.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();			
			map.put("id", updateInstances.get(i).getId());
			map.put("name", updateInstances.get(i).getName());
			discoveryIds.add(updateInstances.get(i).getId());
			tableDatas.add(map);
		}
		logger.info("updateInstances");
		logger.info(discoveryIds);
		
		logger.info("依据得到监控平台指标值解析为资源实例END");
		return discoveryIds;		
	}

	private List<ModuleProp> getModuleProps(Map<String, String> map,
			String[] instedNames, String[] mpropNames) {
		List<ModuleProp> moduleProps = new ArrayList<ModuleProp>();
		StringBuffer instantiation = new StringBuffer(500);
		for (int i = 0; i < mpropNames.length; i++) {
			ModuleProp prop = new ModuleProp();
			prop.setKey(mpropNames[i]);
			prop.setValues(new String[] { map.get(mpropNames[i]) });
			moduleProps.add(prop);
			if (Arrays.binarySearch(instedNames, mpropNames[i]) >= 0) {
				instantiation.append(map.get(mpropNames[i]));
			}
		}
		if (instantiation.length() > 0) {
			ModuleProp prop = new ModuleProp();
			prop.setKey("INST_IDENTY_KEY");
			prop.setValues(new String[] { SecureUtil.md5Encryp(instantiation
					.toString()) });
			moduleProps.add(prop);
		}
		if(logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer(100);
			sb.append("生成ResourceInstance对象:\n");
			sb.append("唯一标识依据属性值:" + instantiation.toString() + "\n");
			sb.append("加密后唯一标识值:" + SecureUtil.md5Encryp(instantiation.toString()));
			logger.debug(sb);
		}
		return moduleProps;
	}

	private List<DiscoverProp> getDiscoverProps(Map<String, String> map, Long instanceId) {
		List<DiscoverProp> discoverProps = new ArrayList<DiscoverProp>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			DiscoverProp prop = new DiscoverProp();
			prop.setInstanceId(instanceId);
			if ("DCS".equals(key)) {
				prop.setKey("nodeGroupId");
			} else {
				prop.setKey(key);
			}
			String value = (String) map.get(key);
			if (SecureUtil.isPassswordKey(key)) {
				value = SecureUtil.pwdEncrypt(value);
			}
			prop.setValues(new String[] { value });
			discoverProps.add(prop);
		}
		return discoverProps;
	}

	/** 将获取的cameraList和orgInfo指标的值转换为Map集合 */
	private List<Map<String, String>> convertMetricData2MapList(String[] metricData) {
		List<Map<String, String>> mapList = MetricDataUtil.parseTableResultSet(metricData);
		logger.debug("解析平台数据得到摄像头个数为：" + mapList.size());
		return mapList;
	}

	/** 根据前台传参生成发现参数对象 */
	private ResourceInstanceDiscoveryParameter createDiscoverParameter(
			Map<String, String> paramMap) {
		ResourceInstanceDiscoveryParameter ridp = new ResourceInstanceDiscoveryParameter();
		ridp.setResourceId(paramMap.get("resourceId"));
		ridp.setDomainId(Long.valueOf(paramMap.get("domainId")));
		ridp.setNodeGroupId(Integer.valueOf(paramMap.get("DCS")));
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		for (String key : paramMap.keySet()) {
			if ("resourceId".equals(key) || "domainId".equals(key)
					|| "DCS".equals(key)) {
				continue;
			}
			if (SecureUtil.isPassswordKey(key)) {
				discoveryInfos.put(key, SecureUtil.pwdEncrypt(paramMap.get(key)));
			} else {
				discoveryInfos.put(key, paramMap.get(key));
			}
		}
		ridp.setDiscoveryInfos(discoveryInfos);
		ridp.setOnlyDiscover(false);
		ridp.setDiscoveryWay(null);
		return ridp;
	}

	/** 创建测试发现的发现参数对象 */
	private ResourceInstanceDiscoveryParameter createTestDiscoverParameter(
			Map<String, String> paramMap) {
		ResourceInstanceDiscoveryParameter ridp = new ResourceInstanceDiscoveryParameter();
		ridp.setResourceId(paramMap.get("resourceId"));
		ridp.setDomainId(Long.valueOf(paramMap.get("domainId")));
		ridp.setNodeGroupId(Integer.valueOf(paramMap.get("DCS")));
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		for (String key : paramMap.keySet()) {
			if ("resourceId".equals(key) || "domainId".equals(key)
					|| "DCS".equals(key)) {
				continue;
			}
			if (SecureUtil.isPassswordKey(key)) {
				discoveryInfos.put(key,
						SecureUtil.pwdEncrypt(paramMap.get(key)));
			} else {
				discoveryInfos.put(key, paramMap.get(key));
			}
		}
		ridp.setDiscoveryInfos(discoveryInfos);
		ridp.setOnlyDiscover(false);
		ridp.setDiscoveryWay(null);
		return ridp;
	}

	/** 转换long时间为常见格式字符串 */
	private String calcTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String hms = sdf.format(time - TimeZone.getDefault().getRawOffset());
		return hms;
	}


    @Override
    public void selectPageForCamera(Page<CameraBo, CameraVo> page) {
        this.cameraNewDao.selectPageForCamera(page);
    }

    @Override
    public void getOfflineNumber(Page<CameraBo, CameraVo> page) {
        this.cameraNewDao.getOfflineNumber(page);
    }
}
