package com.mainsteam.stm.profile.fault.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profile.fault.dao.ProfileFaultDao;
import com.mainsteam.stm.profile.fault.execute.ProfileFaultScriptExecuteServiceMBean;
import com.mainsteam.stm.profile.fault.execute.obj.FaultExecutPluginParameter;
import com.mainsteam.stm.profile.fault.execute.obj.FaultScriptExecuteResult;
import com.mainsteam.stm.profilelib.fault.ProfileFaultInstanceService;
import com.mainsteam.stm.profilelib.fault.ProfileFaultMetricService;
import com.mainsteam.stm.profilelib.fault.ProfileFaultService;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultInstance;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultMetric;
import com.mainsteam.stm.profilelib.fault.obj.ProfileFaultRelation;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.system.scriptmanage.api.IScriptManageApi;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.util.SecureUtil;

public class ProfilefaultServiceImpl implements ProfileFaultService {

	private static final Log logger = LogFactory.getLog(ProfilefaultServiceImpl.class);

	private static final String CUSTOM_METRIC_ID = "faultProfileCustomMetric";

	private ISequence sequence;
	private ProfileFaultDao profileFaultDao;
	private ProfileFaultInstanceService profilefaultInstanceService;
	private ProfileFaultMetricService profilefaultMetricService;
	private CapacityService capacityService;
	private OCRPCClient client;
	private LocaleNodeService localNodeService;
	private ResourceInstanceService instanceService;
	private IScriptManageApi scriptManager;
	private IFileClientApi fileClient;
	private static List<String> appCategroy;

	static {
		// 所有应用
		appCategroy = new ArrayList<String>();
		appCategroy.add(CapacityConst.DATABASE);//
		appCategroy.add(CapacityConst.MIDDLEWARE);//
		appCategroy.add(CapacityConst.J2EEAPPSERVER);//
		appCategroy.add(CapacityConst.WEBSERVER);//
		appCategroy.add(CapacityConst.STANDARDSERVICE);//
		appCategroy.add(CapacityConst.MAILSERVER);//
		appCategroy.add(CapacityConst.DIRECTORY);//
		appCategroy.add(CapacityConst.LOTUSDOMINO);//
		appCategroy.add(CapacityConst.VM);
	}

	@Override
	public void queryProfilefaults(Page<Profilefault, Profilefault> page) {

		profileFaultDao.pageSelect(page);
	}

	@Override
	public ProfileFaultRelation getProfileFaultRelationById(long profileId) {
		ProfileFaultRelation profilefault = profileFaultDao.get(profileId);
		profilefault.setProfileFaultMetrics(profilefaultMetricService.queryProfileMetricByProfileId(profileId));
		profilefault.setProfileFaultInstances(profilefaultInstanceService.queryProfileInstanceByProfileId(profileId));
		return profilefault;
	}

	@Override
	public Profilefault insertProfileFault(Profilefault profilefault) {
		Long profileId = sequence.next();
		if (profilefault != null) {
			profilefault.setProfileId(profileId);
			profilefault.setCreateTime(Calendar.getInstance().getTime());
			int result = profileFaultDao.insert(profilefault);
			if (result > 0) {
				return profilefault;
			}
		}
		return null;
	}

	@Override
	public ProfileFaultRelation insertProfileFault(ProfileFaultRelation profilefaultRelation) {
		// 保存策略基本信息
		Profilefault profile = new Profilefault();
		BeanUtils.copyProperties(profilefaultRelation, profile);
		this.insertProfileFault(profile);
		if (profile != null) {// 基本信息保存成功
			profilefaultRelation.setProfileId(profile.getProfileId());
			// 保存策略与资源、指标关系
			if (profilefaultRelation.getProfileFaultInstances() != null) {
				for (ProfileFaultInstance instance : profilefaultRelation.getProfileFaultInstances()) {
					instance.setProfileId(profile.getProfileId());
				}
				profilefaultInstanceService.insertProfileFaultInstance(profilefaultRelation.getProfileFaultInstances());
			}

			if (profilefaultRelation.getProfileFaultMetrics() != null) {
				for (ProfileFaultMetric metric : profilefaultRelation.getProfileFaultMetrics()) {
					metric.setProfileId(profile.getProfileId());
				}
				profilefaultMetricService.insertProfileFaultMetric(profilefaultRelation.getProfileFaultMetrics());
			}

			return profilefaultRelation;
		}
		return null;
	}

	@Override
	public int removeProfileFault(Long[] profileId) {

		return profileFaultDao.batchDel(profileId);
	}

	@Override
	public int updateProfilefault(Profilefault profilefault) {

		return profileFaultDao.update(profilefault);
	}

	@Override
	public int updateProfilefault(ProfileFaultRelation relation) {
		Profilefault profile = new Profilefault();
		BeanUtils.copyProperties(relation, profile);
		int updateResult = this.updateProfilefault(profile);
		if (updateResult >= 0) {
			profilefaultInstanceService.updateProfilefaultInstances(profile.getProfileId(), relation.getProfileFaultInstances());
			profilefaultMetricService.updateProfileFaultMetrics(profile.getProfileId(), relation.getProfileFaultMetrics());
		}
		return updateResult;
	}

	@Override
	public int updateProfilefaultState(long profileId) {
		return profileFaultDao.updateState(profileId);
	}

	@Override
	public  FaultScriptExecuteResult checkProfileFaultIsAlarmByInstanceAndMetric(String instanceId, String metricId,InstanceStateEnum alarmLevel) {
		if (logger.isInfoEnabled()) {
			logger.info("checkProfileFaultIsAlarmByInstanceAndMetric start! instanceId:" + instanceId + ";\t\t metricId:" + metricId);
		}
		FaultScriptExecuteResult result = new FaultScriptExecuteResult();
		Profilefault profilefault = profileFaultDao.queryProfilefaultByInstanceAndMetric(instanceId, metricId);
		if (null != profilefault && profilefault.getIsUse() == Profilefault.PROFILE_IS_USE_STATE_ENABLE && profilefault.getAlarmLevel().contains(alarmLevel.name())) {
			if (logger.isInfoEnabled()) {
				logger.info("Instance:" + instanceId + "    and Metric:" + metricId + " existence fault profile!");
			}

			// 读取脚本文件内容
			if (logger.isInfoEnabled()) {
				logger.info("getSnapshotScriptContent start");
			}
			String snapshotScriptContent = this.getScriptContentByFileId(profilefault.getSnapshotScriptId());
			if (logger.isInfoEnabled()) {
				logger.info("getSnapshotScriptContent end");
			}
			if (logger.isInfoEnabled()) {
				logger.info("getRecoveryScriptContent start");
			}
			String recoveryScriptContent = this.getScriptContentByFileId(profilefault.getRecoveryScriptId());
			if (logger.isInfoEnabled()) {
				logger.info("getRecoveryScriptContent end");
			}
			ResourceInstance instance = null;
			Node node = null;
			try {
				instance = instanceService.getResourceInstance(Long.valueOf(instanceId));
				if(instance.getParentId()>0){
					instance = instanceService.getResourceInstance(instance.getParentId());
				}
				node = localNodeService.getLocalNodeTable().getNodeInGroup(Integer.parseInt(instance.getDiscoverNode()));
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("execut fault profile getReourceInstance or get Node error!", e);
				}
			}
			ProfileFaultScriptExecuteServiceMBean profileFaultScriptExecuteService;
			try {
				profileFaultScriptExecuteService = client.getRemoteSerivce(node, ProfileFaultScriptExecuteServiceMBean.class);
				if (null != profileFaultScriptExecuteService) {
					FaultExecutPluginParameter pluginParameter = assemblePluginParameter(instance);
					if (logger.isInfoEnabled()) {
						logger.info("Instance:" + instanceId + "    and Metric:" + metricId + " begin execute script!");
					}
					CategoryDef parentCategoryDef = getParentCategoryDef(instance.getCategoryId());
					if(parentCategoryDef.getId().equals(CapacityConst.NETWORK_DEVICE)){
						result  = profileFaultScriptExecuteService.networkDeviceTerminalExecute(pluginParameter, replaceScriptContent(pluginParameter, snapshotScriptContent), replaceScriptContent(pluginParameter, recoveryScriptContent));
					}else{
						result = profileFaultScriptExecuteService.executScript(pluginParameter, replaceScriptContent(pluginParameter, snapshotScriptContent), replaceScriptContent(pluginParameter, recoveryScriptContent));
					}
					if (logger.isInfoEnabled()) {
						logger.info("Instance:" + instanceId + "    and Metric:" + metricId + "  execute script success! snapshotResultContent：\n"+result.getSnapshotFileContent()+"\n\t\t\t\t recoveryResultContent:\n"+result.getRecoveryFileContent());
					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("execute Fault strategy error!", e);
				}
			}
		}else if (logger.isInfoEnabled()) {
			logger.info("checkProfileFaultIsAlarmByInstanceAndMetric end! instanceId:" + instanceId + ";\t metricId:" + metricId+"\t is not  existence fault profile");
		}
		return result;
	}

	/**
	 * @Title: getScriptContentByFileId
	 * @Description: 通过脚本Id读取故障策略脚本内容
	 * @param scriptId
	 *            脚本ID
	 * @return String 脚本文件内容
	 * @throws
	 */
	private String getScriptContentByFileId(String scriptId) {
		if(logger.isInfoEnabled()){
			logger.info("getScriptContentByFileId scriptId=" + scriptId);
		}
		ScriptManage script = null;
		if(null != scriptId && !scriptId.isEmpty()){
			script = scriptManager.loadByscriptId(Long.valueOf(scriptId));
			if(logger.isInfoEnabled()){
				logger.info("Query ScriptManage by method scriptManager.loadByscriptId(scriptId) scriptId=" + scriptId);
			}
		}else{
			if(logger.isInfoEnabled()){
				logger.info("getScriptContentByFileId scriptId is empty end scriptId=" + scriptId);
			}
			return "";
		}
		//读取脚本文件
		String scriptContent = null;
		if (null != script) {
			try {
				if(logger.isInfoEnabled()){
					logger.info("ScriptManage is not null,get File start param fileId=" + script.getFileId());
				}
				File f = fileClient.getFileByID(script.getFileId());
				FileInputStream stream = new FileInputStream(f);
				
				if(logger.isInfoEnabled()){
					logger.info("start read scriptContent fileId=" + script.getFileId());
				}
				byte b[] = new byte[1024];
				int len = 0;
				int temp = 0;
				while ((temp = stream.read()) != -1) {
					b[len] = (byte) temp;
					len++;
				}
				stream.close();
				scriptContent = new String(b, 0, len);
				if(logger.isInfoEnabled()){
					StringBuilder bb = new StringBuilder(100);
					bb.append("getScriptContentByFileId scriptId=").append(scriptId);
					bb.append(" end");
					bb.append(" scriptContent =").append(scriptContent);
					logger.info(bb.toString());
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("read fault profile script content by fileId error, fileId:" + script.getFileId(), e);
				}
			}
		}else{
			if(logger.isInfoEnabled()){
				StringBuilder bb = new StringBuilder(100);
				bb.append("Query ScriptManage is empty scriptId=").append(scriptId);
				bb.append(" ,scriptContent is empty");
				logger.info(bb);
				bb = new StringBuilder(100);
				bb.append("getScriptContentByFileId end scriptId=").append(scriptId);
				logger.info(bb);
			}
		}
		return scriptContent;
	}

	/**
	 * @Title: assemblePluginParameter
	 * @Description: 组装自定义命令执行参数
	 * @param instance
	 *            资源实例
	 * @return CustomMetricPluginParameter
	 * @throws
	 */
	private FaultExecutPluginParameter assemblePluginParameter(ResourceInstance instance) {
		//如果是子资源，使用父资源的发现信息
		if(null!=instance && null!=instance.getParentInstance()){
			instance = instance.getParentInstance();
		}
		FaultExecutPluginParameter parameter = new FaultExecutPluginParameter();
		//获取主资源类型
		String parentCategoryId = getParentCategoryDef(instance.getCategoryId()).getId();
		PluginIdEnum pluginId = null;
		String[] propPortValues = instance.getDiscoverPropBykey("port");
		// 通过不同的资源类型组装不同的参数
		if (parentCategoryId.equals(CapacityConst.HOST) || appCategroy.contains(parentCategoryId)) {
			if (instance.getCategoryId().equals("Windows")) {
				//windows 只支持wmi发现的资源
				if(instance.getResourceId().equals("windowswmi")){
					pluginId = PluginIdEnum.WmiPlugin;
				}else{
					return null;
				}
			} else {
				//非windows主机不支持snmp方式发现的资源
				if(!instance.getResourceId().equals("Linuxsnmp")){
					//非windows主机支持telnet/ssh，telnet需要指定主机类型
					if(parentCategoryId.equals(CapacityConst.HOST)){
						parameter.setHostType(instance.getResourceId());
					}
					switch (instance.getDiscoverWay()) {
					case SSH:
						pluginId=PluginIdEnum.SshPlugin;
						break;
					case TELNET:
						pluginId=PluginIdEnum.TelnetPlugin;
						break;
					case WMI:
						pluginId=PluginIdEnum.WmiPlugin;
						break;
					default:
						break;
					}
				}else{
					return null;
				}
			}
		} else if (parentCategoryId.equals(CapacityConst.NETWORK_DEVICE)) {
			//如果是网络设备，需要从发现属性中获取登录类型[telnet/ssh]
			String[] propLoginTypeValues = instance.getDiscoverPropBykey("loginType");
			propPortValues = instance.getDiscoverPropBykey("loginPort");
			if(null!=propLoginTypeValues && propLoginTypeValues.length>0){
				String loginType = propLoginTypeValues[0];
				switch (loginType) {
				case "Telnet":
					pluginId=PluginIdEnum.TelnetPlugin;
					break;
				case "SSH":
					pluginId=PluginIdEnum.SshPlugin;
					break;
				}
			}
		}else{
			return null;
		}
		parameter.setPluginId(pluginId);
		//从资源的发现属性中获取资源发现属性
		String[] propIPValues = instance.getDiscoverPropBykey("IP");
		String[] propPasswordValues = instance.getDiscoverPropBykey("password");
		String[] propUserNameValues = instance.getDiscoverPropBykey("username");
		if (null != propIPValues && propIPValues.length > 0) {
			parameter.setIp(propIPValues[0]);
		}
		if(null!=propPasswordValues && propPasswordValues.length>0){
			parameter.setPassword(SecureUtil.pwdDecrypt(propPasswordValues[0]));
		}
		if(null!=propUserNameValues && propUserNameValues.length>0){
			parameter.setUserName(propUserNameValues[0]);
		}
		
		if(null!=propPortValues && propPortValues.length>0){
			parameter.setPort(propPortValues[0]);
		}else{
			//如果是SSH插件需要设置端口
			if(parameter.getPluginId().equals(PluginIdEnum.SshPlugin)){
				parameter.setPort("22");
			}else if(parameter.getPluginId().equals(PluginIdEnum.TelnetPlugin)){
				 parameter.setPort("23");
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("get execute parameter success:"+parameter.toString());
		}
		return parameter;
	}

	private String replaceScriptContent(FaultExecutPluginParameter parameter,String scriptContent){
		if (null != scriptContent && !scriptContent.isEmpty()) {
			scriptContent.replaceAll("IPADDR", parameter.getIp())
			.replaceAll("TELNET_USER", parameter.getUserName())
			.replaceAll("TELNET_PWD", parameter.getPassword())
			.replaceAll("SSH_USER", parameter.getUserName())
			.replaceAll("SSH_PWD", parameter.getPassword())
			.replaceAll("SSH_PORT", parameter.getPort());
//			scriptContent+="\n\n";
		}
		return scriptContent;
	}
	
	private CategoryDef getParentCategoryDef(String categoryId){
		CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
		if(categoryDef!=null && categoryDef.getParentCategory()!=null && (!capacityService.getRootCategory().getId().equals(categoryDef.getParentCategory().getId()))){
			categoryDef = getParentCategoryDef(categoryDef.getParentCategory().getId());
		}
		return categoryDef;
	}
	
	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public void setProfileFaultDao(ProfileFaultDao profileFaultDao) {
		this.profileFaultDao = profileFaultDao;
	}

	public void setProfilefaultInstanceService(ProfileFaultInstanceService profilefaultInstanceService) {
		this.profilefaultInstanceService = profilefaultInstanceService;
	}

	public void setProfilefaultMetricService(ProfileFaultMetricService profilefaultMetricService) {
		this.profilefaultMetricService = profilefaultMetricService;
	}

	public void setClient(OCRPCClient client) {
		this.client = client;
	}

	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}

	public void setScriptManager(IScriptManageApi scriptManager) {
		this.scriptManager = scriptManager;
	}

	public void setFileClient(IFileClientApi fileClient) {
		this.fileClient = fileClient;
	}

	public void setInstanceService(ResourceInstanceService instanceService) {
		this.instanceService = instanceService;
	}

	public CapacityService getCapacityService() {
		return capacityService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	

}
