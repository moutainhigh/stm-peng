package com.mainsteam.stm.common.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.common.exception.ResourceInstanceDiscoveryException;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.discovery.InstanceDiscoverMBean;
import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.util.LicenseUtil;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.rpc.client.OCRPCClient;

public class ResourceInstanceDiscoveryServiceImpl implements
		ResourceInstanceDiscoveryService {

	private static final Log logger = LogFactory
			.getLog(ResourceInstanceDiscoveryService.class);

	private OCRPCClient client;
	private LocaleNodeService localNodeService;
	private ResourceInstanceService resourceInstanceService;
	private CapacityService capacityService;
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setClient(OCRPCClient client) {
		this.client = client;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}
	
	@Override
	public DiscoverResourceIntanceResult discoveryResourceInstance(
			ResourceInstanceDiscoveryParameter parameter) {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("nodeGropuId=").append(parameter.getNodeGroupId());
			b.append(" resourceId=").append(parameter.getResourceId());
			b.append(" discoverInfo:").append(parameter.getDiscoveryInfos());
			logger.info("discoveryResourceInstance start:" + b);
		}
		if(parameter.getNodeGroupId() <= 0){
			if (logger.isErrorEnabled()) {
				logger.error("nodeGroupId ?????????!= discoverInfo=" + parameter.getDiscoveryInfos());
			}
		}
		// malachi ???resourceMap????????????????????????resourceMap?????????????????????????????????xml????????????
		ResourceDef resourceDef = capacityService.getResourceDefById(parameter
				.getResourceId());
		if(resourceDef == null){
			logger.error("capacityService.getResourceDefById is null,resourceId="+ parameter.getResourceId());
		}
		// ????????????????????????
		DiscoverResourceIntanceResult resourceInstanceResult = new DiscoverResourceIntanceResult();
		try {
			// malachi ????????????
			ResourceInstance resourceInstance = discover(parameter, resourceDef);
			resourceInstanceResult.setResourceIntance(resourceInstance);
		} catch (ResourceInstanceDiscoveryException e) {
			resourceInstanceResult.setCode(e.getCode());
			resourceInstanceResult.setSuccess(false);
			return resourceInstanceResult;
		}catch (Throwable e) {
			resourceInstanceResult.setCode(ServerErrorCodeConstant.ERR_SERVER_UNKOWN_ERROR);
			resourceInstanceResult.setSuccess(false);
			if (logger.isErrorEnabled()) {
				logger.error("discoveryResourceInstance eror! parms=" + parameter.getDiscoveryInfos(), e);
			}
			return resourceInstanceResult;
		}
		
		if(parameter.isOnlyDiscover()){
			//????????????????????? malachi ?????????
			resourceInstanceResult.setSuccess(true);
			return resourceInstanceResult;
		}
		ResourceInstanceResult result = null;
		try {
			// malachi ???????????????
			result = resourceInstanceService.addResourceInstance(resourceInstanceResult
					.getResourceIntance());
		} catch (InstancelibException e) {
			resourceInstanceResult.setCode(e.getCode());
			resourceInstanceResult.setSuccess(false);
			resourceInstanceResult.setErrorMsg(e.getMessage());
			resourceInstanceResult.setBaseException(e);
			return resourceInstanceResult;
		}
		if(result != null){
			if(result.isRepeat()){// malachi ???????????????????????????
				resourceInstanceResult
				.setCode(DiscoverResourceIntanceResult.RESOURCEINSTANCE_REPEAT);
				// ????????????????????????????????????ID
				resourceInstanceResult.getResourceIntance().setId(
						result.getResourceInstanceId());
				// ??????????????????????????????????????????
				resourceInstanceResult.getResourceIntance().setRepeatValidate(false);
				logger.info("common repeat:" + result.getRepeatIds());
				resourceInstanceResult.setRepeatIds(result.getRepeatIds());
				resourceInstanceResult.setInstanceLifeState(result.getInstanceLiftStatte());
			}
			resourceInstanceResult.setSuccess(true);
		}else{
			resourceInstanceResult.setSuccess(false);
			if(logger.isWarnEnabled()){
				logger.warn("addResourceInstance warn.");
			}
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("nodeGropuId=").append(parameter.getNodeGroupId());
			b.append(" resourceId=").append(parameter.getResourceId());
			b.append(" discoverInfo:").append(parameter.getDiscoveryInfos());
			logger.info("discoveryResourceInstance end:" + b);
		}
		return resourceInstanceResult;
	}

	@Override
	public void topoDiscoveryResourceInstance(
			ResourceInstanceDiscoveryParameter parameter){
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("resourceId=").append(parameter.getResourceId());
			b.append(" discoverInfo:").append(parameter.getDiscoveryInfos());
			b.append(" domainId=").append(parameter.getDomainId());
			logger.info("topoDiscoveryResourceInstance start:" + b);
		}
		ResourceDef resourceDef = capacityService.getResourceDefById(parameter
				.getResourceId());
		if(resourceDef == null){
			if (logger.isErrorEnabled()) {
				logger.error("resourceDef not found,resourceId=" + parameter.getResourceId());
			}
			return;
		}
		ResourceInstance resourceInstance = null;
		try {
			long discoverStartTime = System.currentTimeMillis();
			resourceInstance = discover(parameter, resourceDef);
			long discoverEndTime = System.currentTimeMillis();
			if(logger.isInfoEnabled()){
				logger.info("topo discover instanceId "+resourceInstance.getId()+"\t??????"+((discoverEndTime-discoverStartTime)/1000)+"s!");
			}
		} catch (ResourceInstanceDiscoveryException e) {
			if(logger.isErrorEnabled()){
				logger.error("topo discover error!", e);
			}
			throw new RuntimeException(e);
		}
		if(resourceInstance != null){
			resourceInstance.setId(parameter.getResourceInstanceId());
			//CategoryDef categoryDef = capacityService.getCategoryById(resourceInstance.getCategoryId());
			//if(categoryDef != null){
			//	if(CapacityConst.NETWORK_DEVICE.equals(categoryDef.getParentCategory().getId())){
					if(resourceInstance.getShowIP().equals(parameter.getCoreNodeIp())){
						resourceInstance.setCore(true);
					}
				//}
			//}
			
//			LicenseUtil licenseUtil = LicenseUtil.getLicenseUtil();
			/**
			 * Topo???????????????License
			 * */
//			boolean isCalcLicense = false;
//			try {
//				isCalcLicense = resourceInstanceService.checkLicense(resourceInstance);
//			} catch (InstancelibException e1) {
//				throw new InstancelibRuntimeException(e1.getCode(), e1.getMessage());
//			}
			long refreshStartTime = System.currentTimeMillis();
			try {
//				if(isCalcLicense){
//					//??????License??????
//					licenseUtil.addLicenseStorageNum();
//				}
				resourceInstanceService.refreshResourceInstance(resourceInstance);
			} catch (Exception e) {
//				if(isCalcLicense){
//					licenseUtil.deleteLicenseStorageNum();
//				}
			}
//			if(isCalcLicense){
//				licenseUtil.deleteLicenseStorageNum();
//			}
			long refreshEndTime = System.currentTimeMillis();
			if(logger.isInfoEnabled()){
				logger.info("topo discover refreshResourceInstance instanceId:"+resourceInstance.getId()+"??????\t"+((refreshEndTime-refreshStartTime)/1000)+"s");
			}
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("resourceId=").append(parameter.getResourceId());
			b.append(" discoverInfo:").append(parameter.getDiscoveryInfos());
			b.append(" domainId=").append(parameter.getDomainId());
			logger.info("topoDiscoveryResourceInstance end:" + b);
		}
	}

	private ResourceInstance convertToResourceInstance(
			ModelResourceInstance modelResourceInstance,
			ResourceInstanceDiscoveryParameter discoveryParameter,
			ResourceDef resourceDef, ResourceInstance parentInstance) {

		ResourceInstance resourceInstance = new ResourceInstance(parentInstance);

		/*
		 * ??????????????????
		 */
		Map<String, String[]> instanceProps = modelResourceInstance
				.getPropValues();
		ResourcePropertyDef[] resourcePropertyDefs = resourceDef
				.getPropertyDefs();
		List<ModuleProp> moduleProps = new ArrayList<>();
		if (resourcePropertyDefs != null && resourcePropertyDefs.length > 0) {
			for (ResourcePropertyDef resourcePropertyDef : resourcePropertyDefs) {
				ModuleProp prop = new ModuleProp();
				prop.setKey(resourcePropertyDef.getId());
				if (instanceProps != null
						&& instanceProps.containsKey(resourcePropertyDef
								.getId())) {
					String[] moduleValue = instanceProps.remove(resourcePropertyDef.getId());
					if(moduleValue != null){
						for (int i = 0; i <  moduleValue.length ; i++) {
							if (moduleValue[i] != null) {
								String tempValue = moduleValue[i].trim();
								moduleValue[i] = tempValue;
								/*
								 * ?????????showip ??? .. ???????????????????????????????????????ip ??????????????????????????????ip.
								 * ?????????????????????????????????ip ?????????????????????ip
								 */
								if (DiscoveryParameter.IP_KEY.equalsIgnoreCase(resourcePropertyDef.getId())) {
									if(!StringUtils.isEmpty(tempValue)){
										resourceInstance.setShowIP(tempValue);
									}
								}
							}
						}
						prop.setValues(moduleValue);
					}
				}
				moduleProps.add(prop);
			}
		}
		if (instanceProps != null && !instanceProps.isEmpty()) {
			for (Entry<String, String[]> propEntry : instanceProps.entrySet()) {
				ModuleProp prop = new ModuleProp();
				prop.setKey(propEntry.getKey());
				prop.setValues(propEntry.getValue());
				moduleProps.add(prop);
			}
		}
		
		resourceInstance.setModuleProps(moduleProps);
		resourceInstance.setDomainId(discoveryParameter.getDomainId());
		resourceInstance.setResourceId(modelResourceInstance.getResourceId());
		resourceInstance.setName(modelResourceInstance.getName());
		resourceInstance.setCategoryId(modelResourceInstance.getCategoryId());
		resourceInstance.setChildType(modelResourceInstance.getChildType());
		resourceInstance.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
		if (discoveryParameter.getDiscoveryWay() != null) {
			resourceInstance.setDiscoverWay(DiscoverWayEnum
					.valueOf(discoveryParameter.getDiscoveryWay()));
		}
		resourceInstance.setDiscoverNode(String.valueOf(discoveryParameter
				.getNodeGroupId()));
		/*
		 * ???????????????
		 */
		List<ModelResourceInstance> childModelResourceInstances = modelResourceInstance
				.getChildren();
		if (childModelResourceInstances != null
				&& !childModelResourceInstances.isEmpty()) {
			List<ResourceInstance> children = new ArrayList<>(
					childModelResourceInstances.size());
			for (ModelResourceInstance childModelResourceInstance : childModelResourceInstances) {
				ResourceDef childDef = capacityService
						.getResourceDefById(childModelResourceInstance
								.getResourceId());
				if(childDef == null){
					logger.error("capacityService.getResourceDefById is null, ResourceId=" + childModelResourceInstance.getResourceId());
				}
				ResourceInstance childResourceInstance = convertToResourceInstance(
						childModelResourceInstance, discoveryParameter,
						childDef, resourceInstance);
				children.add(childResourceInstance);
			}
			resourceInstance.setChildren(children);
		}
		return resourceInstance;
	}

	private ResourceInstance discover(
			ResourceInstanceDiscoveryParameter parameter,
			ResourceDef resourceDef) throws ResourceInstanceDiscoveryException {
		Node node = null;
		try {
			// malachi ??????groupId ????????????????????????
			node = localNodeService.getLocalNodeTable().getNodeInGroup(
					parameter.getNodeGroupId());
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("discoveryResourceInstance", e);
			}
		}
		if (node == null) {
			throw new ResourceInstanceDiscoveryException(
					ServerErrorCodeConstant.ERR_NODE_NOT_FIND,
					"node is null.nodeGroupId=" + parameter.getNodeGroupId());
		}else{
			if (logger.isInfoEnabled()) {
				logger.info("discover get node ok,nodeGroupId=" + parameter.getNodeGroupId());
			}
		}
		InstanceDiscoverMBean discoverMBean = null;
		try {
			// malachi rpc????????????
			discoverMBean = client.getRemoteSerivce(node,
					InstanceDiscoverMBean.class);
		} catch(Exception e){
			if (logger.isErrorEnabled()) {
				logger.error(
						"discoveryResourceInstance error???" + e.getMessage(), e);
			}
			throw new ResourceInstanceDiscoveryException(
					ServerErrorCodeConstant.ERR_RCPCLIENT_GETMBEAN_FAIL,
					"can't connect to node[" + node.getIp() + ":"
							+ node.getPort() + "]", e);
		}
		// ????????????
		DiscoveryParameter discoveryParameter = new DiscoveryParameter();
		// ????????????
		discoveryParameter.setDiscoveryInfos(parameter.getDiscoveryInfos());
		// ????????????
		discoveryParameter.setResourceId(parameter.getResourceId());
		// ????????????
		discoveryParameter.setDiscoveryWay(parameter.getDiscoveryWay());
		// ?????????????????????
		discoveryParameter.setDomainId(parameter.getDomainId());
		
		discoveryParameter.setAnonymousNetworkDevice(parameter.isAnonymousNetworkDevice());
		// ????????????????????????
		ModelResourceInstance modelResourceInstance;
		try {
			//malachi in discovery1 ??????dcs
			modelResourceInstance = discoverMBean.discovery(discoveryParameter);
		} catch (InstanceDiscoveryException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("discoveryResourceInstance", e1);
			}
			throw new ResourceInstanceDiscoveryException(e1);
		}
		ResourceInstance resourceInstance = null;
		try{
			resourceInstance = convertToResourceInstance(
					modelResourceInstance, parameter, resourceDef, null);
			/*
			 * ??????????????????,???????????????????????????????????????????????????????????????,???????????????????????????????????????
			 */
			Map<String, String> map = parameter.getDiscoveryInfos();
			if (map != null && !map.isEmpty()) {
				List<DiscoverProp> discoverProps = new ArrayList<>(map.size());
				for (Entry<String, String> entry : map.entrySet()) {
					DiscoverProp prop = new DiscoverProp();
					prop.setKey(entry.getKey());
					String[] values = { entry.getValue() };
					prop.setValues(values);
					discoverProps.add(prop);
					// ????????????ip
					if (DiscoveryParameter.IP_KEY.equalsIgnoreCase(entry.getKey())) {
						if(!StringUtils.isEmpty(entry.getKey())){
							resourceInstance.setShowIP(entry.getValue());
						}
					}
				}
				resourceInstance.setDiscoverProps(discoverProps);
			}
			//?????????????????????????????????ip
			String name = resourceInstance.getName();
			if(StringUtils.isEmpty(name)){
				resourceInstance.setName(resourceInstance.getShowIP());
			}
			resourceInstance.setShowName(resourceInstance.getName());
			String discoverWay = parameter.getDiscoveryWay();
			// ???????????????????????????
			if (!StringUtils.isEmpty(discoverWay)) {
				resourceInstance.setDiscoverWay(DiscoverWayEnum.valueOf(discoverWay));
			}else{
				//???????????????
				resourceInstance.setDiscoverWay(DiscoverWayEnum.NONE);
			}
			// ?????????????????????nodegroup?????????nodegroup?????????group ?????????node ????????????
			resourceInstance.setDiscoverNode(String.valueOf(parameter
					.getNodeGroupId()));	
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(), e);
			}
		}
		return resourceInstance;
	}
}
