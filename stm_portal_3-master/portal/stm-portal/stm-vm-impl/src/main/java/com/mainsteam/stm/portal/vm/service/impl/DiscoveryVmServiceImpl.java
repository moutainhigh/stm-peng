package com.mainsteam.stm.portal.vm.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
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
import com.mainsteam.stm.portal.vm.api.DiscoveryVmService;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryListBo;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryListPageBo;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryParaBo;
import com.mainsteam.stm.portal.vm.dao.IVmResourceTreeDao;
import com.mainsteam.stm.portal.vm.po.VmResourceTreePo;
import com.mainsteam.stm.profilelib.ProfileAutoRefreshService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.system.resource.api.Filter;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.Util;

public class DiscoveryVmServiceImpl implements DiscoveryVmService {
	@Resource(name = "stm_system_resourceApi")
	private IResourceApi resourceApi;
	
	@Resource
	private ResourceInstanceDiscoveryService discoveryService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ProfileService profileService;

	@Resource
	private ISearchApi searchApi;
	
	@Resource
	private IVmResourceTreeDao vmResourceTreeDao;
	
	@Resource
	private ILicenseCalcService licenseCalcService;

	@Resource
	private DiscoverPropService discoverPropService;
	
	@Resource
	private ProfileAutoRefreshService profileAutoRefreshService;

	@Resource
	private NodeService nodeService;

	@Resource
	private IDomainApi domainApi;
	
	private Logger logger = Logger.getLogger(DiscoveryVmServiceImpl.class);
	
	private final static String SEARCH_NAV = "虚拟化-发现";
	
	// 资源树中的节点
	private final static String RESOURCETREE_NODE_IP = "ip";
	private final static String RESOURCETREE_NODE_NAME = "name";
	private final static String RESOURCETREE_NODE_FULLNAME = "fullName";
	private final static String RESOURCETREE_NODE_TYPE = "type";
	private final static String RESOURCETREE_NODE_RESOURCEID = "resourceId";
	private final static String RESOURCETREE_NODE_UUID = "uuid";
	private final static String RESOURCETREE_NODE_SUBINDEX = "subIndex";
	private final static String RESOURCETREE_NODE_SUBRESOURCES = "subResources";
	private final static String RESOURCETREE_NODE_CHILDTREES = "childTrees";
	private final static String SERVER_INST_IDENTY_KEY = "INST_IDENTY_KEY";
	private final static String DISCOVERYTYPE_VCENTER = "1";
	private final static String DISCOVERYTYPE_ESXI = "2";
	private final static String DISCOVERYTYPE_XENPOOl = "3";
	private final static String DISCOVERYTYPE_FUSIONCOMPUTESITE = "4";
	private final static String DISCOVERYTYPE_FUSIONCOMPUTEONEPOINTTHREE = "5";
	private final static String DISCOVERYTYPE_KVMHOST = "6";
	private final static String DISCOVERYTYPE_DTCENTER = "7";
	private final static String DISCOVERYTYPE_KYLIN = "8";
	private final static String DISCOVERYTYPE_VCENTERFivePointFive = "9";
	private final static String DISCOVERYTYPE_VCENTERSix = "10";
	private final static String DISCOVERYTYPE_VCENTERSixPointFive = "11";
	
	/**
	 * @discoveryType 1:vCenter 2:Esxi
	 */
	@Override
	public Map<String, Object> autoDiscovery(VmDiscoveryParaBo dParaBo, ILoginUser user) {
		logger.info("//--------------- 虚拟化资源发现START ---------------//");
		Long autoDiscoveryStartTime = System.currentTimeMillis();
		Map<String, Object> userCache = user.getCache();
		// 是否正在发现
		userCache.put("isAutoDiscovering", true);
		// 结果集
		Map<String, Object> resultCollection = new HashMap<String, Object>();
		// 设置发现参数
		ResourceInstanceDiscoveryParameter discoverParameter = createRiDiscoryPara(dParaBo);
		// 发现
		DiscoverResourceIntanceResult rir = discoveryService.discoveryResourceInstance(discoverParameter);
		// 前台展示树 集合
		List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
		List<VmResourceTreePo> vmResourceTreePos = new ArrayList<VmResourceTreePo>();
		if(rir.isSuccess()){
			createResourceInstance(rir.getResourceIntance(), dParaBo, treeList, resultCollection,vmResourceTreePos);
//		if(true){
//			createResourceInstance(null, dParaBo, treeList, resultCollection,vmResourceTreePos);
			deleteResource(vmResourceTreePos);
			resultCollection.put("isSuccess", true);
		}else{
			resultCollection.put("isSuccess", false);
			resultCollection.put("errorCode", rir.getCode());
		}
		// 展示树结果
		treeListFilter(treeList);
		resultCollection.put("vmResourceTree", treeList);
		//如果重复资源,更新发现信息
		if(null!=treeList&&treeList.size()>0){
			updateResourceDiscoveryPara(treeList,dParaBo);
		}
		
		// 发现时间
		resultCollection.put("autoDiscoveryTime", calcTime(System.currentTimeMillis() - autoDiscoveryStartTime));
		logger.info("//--------------- 虚拟化资源发现END ---------------//");
		return resultCollection;
	}
	
	private void updateResourceDiscoveryPara(List<Map<String, Object>> treeList,VmDiscoveryParaBo dParaBo){
		Map<String, Object> res = treeList.get(0);
		long instanceId = 0;
		if(res.containsKey("isRepeat")){
			boolean ifRepeat = (boolean)res.get("isRepeat");
			if(ifRepeat){
				String type = (String)res.get("type");
				switch(type){
				case "VCenter":
				case "VMWareVCenter5.5":
				case "VMWareVCenter6":
				case "VMWareVCenter6.5":
				case "XenPool":
				case "KvmHost":
				case "DTCenterCloudGlobalDashBoard":
				case "KyLinServer":
					instanceId = (long)res.get("id");
					break;
				}
			}
		}
		if(instanceId>0){
			Map paramter = new HashMap();
			paramter.put("discoveryType", dParaBo.getDiscoveryType());
			paramter.put("domainId", dParaBo.getDomainId().toString());
			paramter.put("DCS", dParaBo.getDCS().toString());
			paramter.put("IP", dParaBo.getIP());
			paramter.put("userName", dParaBo.getUserName());
			paramter.put("password", dParaBo.getPassword());
			paramter.put("regeion", dParaBo.getRegeion());
			paramter.put("project", dParaBo.getProject());
			updateDiscoverParamterVm(paramter, instanceId);
		}
	}
	
	/**
	 * 删除实例化资源
	 * @param vmResourceTreePos
	 */
	private void deleteResource(List<VmResourceTreePo> vmResourceTreePos) {
		if(vmResourceTreePos!=null){
			vmResourceTreePosFor : for(int i=0;i<vmResourceTreePos.size();i++){
				try {
					//uuid 宿主机ID
					String uuid = vmResourceTreePos.get(i).getUuid();
					//puuid 宿主机对应的clusterId
					String puuid = vmResourceTreePos.get(i).getPuuid();
					
					//检察是否已经入库资源
					VmResourceTreePo thisHostObj = vmResourceTreeDao.selectByUuid(uuid);
					if(null==thisHostObj) continue;
					
					//宿主机下面的虚拟机
					List<VmResourceTreePo> vpoVm = vmResourceTreeDao.selectByPuuid(uuid);
					//vpo2 该宿主机parent
					VmResourceTreePo vpoCluster = vmResourceTreeDao.selectByUuid(puuid);
					if(!Util.isEmpty(vpoVm)){
						for(int m=0;m<vpoVm.size();m++){
							logger.info("delId:"+vpoVm.get(m).getInstanceid());
							resourceInstanceService.removeResourceInstance(vpoVm.get(m).getInstanceid());
						}
					}
					
					//删除parent之前先删除host
					resourceInstanceService.removeResourceInstance(vmResourceTreePos.get(i).getInstanceid());
					vmResourceTreeDao.delByUuid(vmResourceTreePos.get(i).getUuid());
					
					if(!Util.isEmpty(vpoCluster)){
						List<VmResourceTreePo> vpCluster = vmResourceTreeDao.selectByPuuid(vpoCluster.getUuid());
						//检查该cluster下面是否有宿主机
						if(vpCluster==null||vpCluster.size()<1){
							if(null!=vpoCluster.getInstanceid() && vpoCluster.getInstanceid()>0){
								resourceInstanceService.removeResourceInstance(vpoCluster.getInstanceid());
								vmResourceTreeDao.delByUuid(vpoCluster.getUuid());
							}else{
								vmResourceTreeDao.delByUuid(vpoCluster.getUuid());
								//没有instanceId表示parent为datacenter,直接进入下个循环
								continue vmResourceTreePosFor;
							}
						}
						//检察datacenter
						List<VmResourceTreePo> poParent = vmResourceTreeDao.selectByPuuid(vpoCluster.getPuuid());
						if(null==poParent || poParent.size()==0){
							vmResourceTreeDao.delByUuid(vpoCluster.getPuuid());
						}
						
//						List<VmResourceTreePo> pocluster = vmResourceTreeDao.selectByPuuid(vpoCluster.getPuuid());
//						if(pocluster!=null){
//							int j = 0;
//							List<Long> instanceId = new ArrayList<Long>();
//							for(VmResourceTreePo po2:pocluster){
//								if(po2.getVmtype().equals("FusionComputeCluster")||po2.getVmtype().equals("ClusterComputeResource")||po2.getVmtype().equals("XenPool")
//										|| po2.getVmtype().equals("FusionComputeHost")|| po2.getVmtype().equals("HostSystem")|| po2.getVmtype().equals("XenHost")){	
//									++j;
//								}else if(po2.getInstanceid()!=null){
//									instanceId.add(po2.getInstanceid());
//								}
//							}
//							if(j<1){
//								vmResourceTreeDao.delByPuuid(vpoCluster.getPuuid());
//								resourceInstanceService.removeResourceInstances(instanceId);
//							}
//						}
					
					}
				} catch (InstancelibException e) {
					logger.error("InstancelibException", e);
				}
				
			}
		}
	}
	/**
	 * 结果集过滤(VMware,Xen,Fusion),删除没有children的节点(vm,存储除外)
	 * @param treeList
	 */
	private void treeListFilter(List<Map<String, Object>> treeList){
		List<Integer> needToDel = new ArrayList<Integer>();
		for(int i=0;i<treeList.size();i++){
			Map<String, Object> mapObj = treeList.get(i);
			if(null==mapObj||mapObj.size()==0) continue;
			
			String type = (String)mapObj.get("type");
			switch (type) {
			case "XenPool":
			case "KvmHost":
//			case "XenHost":
			case "Datacenter":
			case "VMWareCluster":
			case "VMWareCluster5.5":
			case "VMWareCluster6":
			case "VMWareCluster6.5":
//			case "HostSystem":
			case "FusionComputeCluster":
			case "FusionComputeOnePointThreeCluster":
//			case "FusionComputeHost":
			case "DTCenterCloudGlobalDashBoard":
			case "KyLinServer":
				
			List<Map<String,Object>> childrenList = (List<Map<String, Object>>) mapObj.get("children");	
			if(null!=childrenList && childrenList.size()>0){
				treeListFilter(childrenList);
			}else{
				needToDel.add(i);
			}
				break;
			default:
				needToDel.add(i);
				break;
			}
		}
		for(Integer x:needToDel){
			treeList.remove(x);
		}
	}
//	private void treeListFilter(List<Map<String, Object>> treeList){
//		try{
//			Iterator it = treeList.iterator();
//			while (it.hasNext()) {
//				Map last = (Map) it.next();
//				//XEN
//				if(last.get("type").equals("XenPool")){
//					List<Map<String,Object>> Xenpool = (List<Map<String, Object>>) last.get("children");
//					Iterator xenPoolit = Xenpool.iterator();
//					int xenCount = 0;
//					while(xenPoolit.hasNext()){
//						Map xenPoolMap = (Map)xenPoolit.next();
//						if(xenPoolMap.size()>0){
//							if(xenPoolMap.get("type").equals("XenHost")){
//								++xenCount;
//							}
//						}
//						if(xenCount==0){
//							xenPoolit.remove();
//						}
//					}
//				}else{
//					//
//					List<Map<String,Object>> pool = (List<Map<String, Object>>) last.get("children");
//					Iterator poolit = pool.iterator();
//					while(poolit.hasNext()){
//						Map poolMap = (Map)poolit.next();
//						int clustorCount = 0;
//						List<Map<String,Object>> clusterList = (List<Map<String, Object>>) poolMap.get("children");
//						Iterator clusterListit = clusterList.iterator();
//						while(clusterListit.hasNext()){
//							Map hostMap = (Map)clusterListit.next();
//							String type = (String) hostMap.get("type");
//							//集群判断
//							if(type.equals("ClusterComputeResource")||type.equals("FusionComputeCluster")){
//								clustorCount = clustorCount + 1;
//								List<Map<String,Object>> host = (List<Map<String, Object>>) hostMap.get("children");
//								if(host.size()>0){
//									Map<String, Object> e = host.get(0);
//									if(host.get(0)==null||host.get(0).size()<1){
//										hostMap.put("isDel", true);
//										clustorCount = clustorCount - 1;
//									}
//									if(hostMap.get("isDel")!=null){
//										clusterListit.remove();
//									}
//								}
//							}
//						}
//						if(clustorCount<1){
//							poolit.remove();
//						}
//					}
//				}
//			}
//		}catch(Exception e){
//			logger.error("treeListFilter",e);
//		}
//	}
	
	
	
	@Override
	public boolean addMoniterAutoDiscoveryVm(ILoginUser user, List<Long> instanceIds) {
		boolean result = false;
		if (instanceIds != null && instanceIds.size() > 0) {
			try {
				List<Long> list = new ArrayList<Long>();
				for (int i = 0; i < instanceIds.size(); i++) {
					list.add(instanceIds.get(i));
					saveSearchRel(instanceIds.get(i));
				}
				profileService.enableMonitor(list);
				
				result = true;
				logger.info("addMoniterSuppleDiscoveryVm:" + Arrays.toString(list.toArray()));
			} catch (Exception e) {
				result = false;
				logger.error("batchOpenMonitor", e);
			}
		}
		return result;
	}
	
	@Override
	public boolean addMoniterReDiscoveryVm(ILoginUser user, HashSet<Long> instanceIds,HashSet<Long> instanceUncheckedIds) {
		boolean result = true;
		if (instanceIds != null && instanceIds.size() > 0) {
			try {
				List<Long> list = new ArrayList<Long>();
				for (Long ids:instanceIds) {
					list.add(ids);
					saveSearchRel(ids);
				}
				profileService.enableMonitor(list);
				
				logger.info("addMoniterReDiscoveryVm:" + Arrays.toString(list.toArray()));
			} catch (Exception e) {
				result = false;
				logger.error("batchOpenMonitor", e);
			}
		}
		
		if (result && instanceUncheckedIds != null && instanceUncheckedIds.size() > 0) {
			try {
				List<Long> listUnMonitor = new ArrayList<Long>();
				for (Long ids:instanceUncheckedIds) {
					listUnMonitor.add(ids);
				}
				profileService.cancleMonitor(listUnMonitor);
				
				logger.info("unMoniterReDiscoveryVm:" + Arrays.toString(listUnMonitor.toArray()));
			} catch (Exception e) {
				result = false;
				logger.error("batchOpenMonitor", e);
			}
		}
		return result;
	}

	@Override
	public List<Map<String, String>> suppleDiscoveryVmResources(ILoginUser user) {
		Map<String, Map<String, String>> vmResourceMap = new HashMap<String, Map<String, String>>();
		List<Map<String, String>> vmResourceList = new ArrayList<Map<String, String>>();
		List<ResourceInstanceBo> riBoList = resourceApi.getResources(getVcenter_Esxi_RQB(user));
		List<Long> resourceIdList = new ArrayList<Long>();
		for(int i = 0; riBoList != null && i < riBoList.size(); i++){
			resourceIdList.add(riBoList.get(i).getId());
		}
		if(!resourceIdList.isEmpty()){
			try {
				List<ResourceInstance> riList = resourceInstanceService.getResourceInstances(resourceIdList);
				for(int i = 0; riList != null && i < riList.size(); i++){
					ResourceInstance ri = riList.get(i);
					String[] ips = ri.getDiscoverPropBykey("IP");
					String ip = ips != null && ips.length > 0 ? ips[0] : "";
					if(!ip.isEmpty()){
						Map<String, String> vmResource = new HashMap<String, String>();
						vmResource.put("id", String.valueOf(riBoList.get(i).getId()));
						vmResource.put("name", riBoList.get(i).getShowName());
						vmResourceMap.put(ri.getResourceId() + ip, vmResource);
					}
				}
			} catch (InstancelibException e) {
				logger.error(e.getMessage(), e);
			}
		}
		for(String key : vmResourceMap.keySet()){
			vmResourceList.add(vmResourceMap.get(key));
		}
		return vmResourceList;
	}

	@Override
	public Map<String, Object> suppleDiscovery(String instanceId, ILoginUser user) {
		Map<String, Object> userCache = user.getCache();
		Long suppleDiscoveryStartTime = System.currentTimeMillis();
		
		// 是否正在发现
		userCache.put("isSuppleDiscovering", true);
		// 结果集
		Map<String, Object> resultCollection = new HashMap<String, Object>();
		// 前台展示树 集合
		List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
		try {
			ResourceInstance ri = resourceInstanceService.getResourceInstance(Long.valueOf(instanceId));
			// 设置发现参数
			VmDiscoveryParaBo dParaBo = new VmDiscoveryParaBo();
			if("VMWareVCenter".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_VCENTER);
			}else if("vmESXi".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_ESXI);
			}else if("XenPool".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_XENPOOl);
			}else if("FusionComputeSite".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_FUSIONCOMPUTESITE);
			}else if("FusionComputeOnePointThreeSite".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_FUSIONCOMPUTEONEPOINTTHREE);
			}else if("KvmHost".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_KVMHOST);
			}else if("KyLinServer".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_KYLIN);
			}else if("DTCenterCloudGlobalDashBoard".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_DTCENTER);
			}else if("VMWareVCenter5.5".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_VCENTERFivePointFive);
			}else if("VMWareVCenter6".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_VCENTERSix);
			}else if("VMWareVCenter6.5".equals(ri.getResourceId())){
				dParaBo.setDiscoveryType(DISCOVERYTYPE_VCENTERSixPointFive);
			}
			
			dParaBo.setDomainId(ri.getDomainId());
			dParaBo.setDCS(Integer.valueOf(ri.getDiscoverNode()));
			dParaBo.setIP(ri.getDiscoverPropBykey("IP") == null ? "" : ri.getDiscoverPropBykey("IP")[0]);
			dParaBo.setUserName(ri.getDiscoverPropBykey("username") == null ? "" : ri.getDiscoverPropBykey("username")[0]);
			String password = SecureUtil.pwdDecrypt(ri.getDiscoverPropBykey("password") == null ? "" : ri.getDiscoverPropBykey("password")[0]);
			dParaBo.setPassword(password);
			dParaBo.setRegeion(ri.getDiscoverPropBykey("regeion") == null ? "" : ri.getDiscoverPropBykey("regeion")[0]);
			dParaBo.setProject(ri.getDiscoverPropBykey("project") == null ? "" : ri.getDiscoverPropBykey("project")[0]);
			ResourceInstanceDiscoveryParameter discoverParameter = createRiDiscoryPara(dParaBo);
			List<VmResourceTreePo> vmResourceTreePos = new ArrayList<VmResourceTreePo>();
			// 发现
			DiscoverResourceIntanceResult rir = discoveryService.discoveryResourceInstance(discoverParameter);
			if(rir.isSuccess()){
				createResourceInstance(rir.getResourceIntance(), dParaBo, treeList, resultCollection,vmResourceTreePos);
				deleteResource(vmResourceTreePos);
				resultCollection.put("isSuccess", true);
			}else{
				resultCollection.put("isSuccess", false);
				resultCollection.put("errorCode", rir.getCode());
			}
			
		} catch (NumberFormatException e) {
			logger.error("getResourceInstance", e);
		} catch (InstancelibException e) {
			logger.error("getResourceInstance", e);
		}
		// 展示树结果
		treeListFilter(treeList);
		resultCollection.put("vmResourceTree", treeList);
		
		// 发现时间
		resultCollection.put("suppleDiscoveryTime", calcTime(System.currentTimeMillis() - suppleDiscoveryStartTime));
		
		return resultCollection;
	}

	@Override
	public boolean addMoniterSuppleDiscoveryVm(ILoginUser user, List<Long> instanceIds) {
		boolean result = false;
		if (instanceIds != null && instanceIds.size() > 0) {
			try {
				List<Long> list = new ArrayList<Long>();
				for (int i = 0; i < instanceIds.size(); i++) {
					list.add(instanceIds.get(i));
					saveSearchRel(instanceIds.get(i));
				}
				profileService.enableMonitor(list);
				result = true;
				logger.info("addMoniterSuppleDiscoveryVm:" + Arrays.toString(list.toArray()));
			} catch (Exception e) {
				result = false;
				logger.error("batchOpenMonitor", e);
			}
		}
		return result;
	}
	
	/**
	 * 创建虚拟化资源
	 * @param ri
	 * @param dcs
	 * @param domainId
	 */
	private void createResourceInstance(ResourceInstance ri, VmDiscoveryParaBo dParaBo, List<Map<String, Object>> treeList, Map<String, Object> resultCollection,List<VmResourceTreePo> vmResourceTreePos){
		String[] resourceTrees = ri.getModulePropBykey("resourceTree");
		//---------- hyperv data -----------
		//String[] resourceTrees = {"{\"childTrees\":[{\"childTrees\":[{\"childTrees\":[],\"ip\":\"VMIP\",\"name\":\"新建虚拟机@2\",\"resourceId\":\"HyperVVm\",\"subResources\":[],\"type\":\"HyperVVm\",\"uuid\":\"1CAF2C99-BD90-4CE6-B1A4-899D9CD38755\"},{\"childTrees\":[],\"ip\":\"VMIP\",\"name\":\"新建虚拟机@1\",\"resourceId\":\"HyperVVm\",\"subResources\":[],\"type\":\"HyperVVm\",\"uuid\":\"EE41BB94-C3FD-4EF6-B5A8-C6DEC79F10CB\"}],\"name\":\"WIN-E3521RVBTS8\",\"resourceId\":\"HyperVHost\",\"subResources\":[],\"type\":\"HyperVHost\",\"uuid\":\"756d2c59-faf0-3e58-9c19-85f298817980\"}],\"name\":\"Hyper-V 管理器\",\"resourceId\":\"HyperVSupervisor\",\"subResources\":[],\"type\":\"HyperVSupervisor\",\"uuid\":\"c2d55b22-f10f-34f8-a4be-9e68e8c834ba\"}",""};
		//---------- kvm data ----------
		//String[] resourceTrees = {"{\"childTrees\":[{\"childTrees\":[],\"ip\":\"vmip\",\"name\":\"ubuntu2\",\"resourceId\":\"KvmVM\",\"subResources\":[],\"type\":\"KvmVM\",\"uuid\":\"04c07717-cb4f-6e9f-d9b0-38380fe26726\"},{\"childTrees\":[],\"ip\":\"vmip\",\"name\":\"ubuntu_linux6\",\"resourceId\":\"KvmVM\",\"subResources\":[],\"type\":\"KvmVM\",\"uuid\":\"f3d012e2-4bdc-46b2-27e7-9fb9043254d4\"},{\"childTrees\":[],\"ip\":\"vmip\",\"name\":\"asd123\",\"resourceId\":\"KvmVM\",\"subResources\":[],\"type\":\"KvmVM\",\"uuid\":\"9fcdb21d-609b-d7eb-5912-84c42e7d0fee\"},{\"childTrees\":[],\"ip\":\"vmip\",\"name\":\"ubuntu_linux\",\"resourceId\":\"KvmVM\",\"subResources\":[],\"type\":\"KvmVM\",\"uuid\":\"813f7ceb-dd68-31d6-928c-4ac13f7fcf36\"},{\"childTrees\":[],\"ip\":\"vmip\",\"name\":\"ubuntu_linux2\",\"resourceId\":\"KvmVM\",\"subResources\":[],\"type\":\"KvmVM\",\"uuid\":\"8af5963d-eea2-3ff5-af44-cdda17214b88\"},{\"childTrees\":[],\"ip\":\"vmip\",\"name\":\"ubuntu_linux3\",\"resourceId\":\"KvmVM\",\"subResources\":[],\"type\":\"KvmVM\",\"uuid\":\"8674e104-cec0-32b7-9226-85b59e3e5b01\"},{\"childTrees\":[],\"ip\":\"datastoreIp\",\"name\":\"default\",\"resourceId\":\"KvmDataStore\",\"subResources\":[],\"type\":\"KvmDataStore\",\"uuid\":\"5bb6d062-f9bc-bbd0-74cf-54e69add8900\"}],\"ip\":\"192.168.1.186\",\"name\":\"ubuntu\",\"resourceId\":\"KvmHost\",\"subResources\":[],\"type\":\"KvmHost\",\"uuid\":\"564de1cb-e56d-4629-0d53-be01f6ca845d\"}",""}; 
		//---------- aliyun data ----------
		//String[] resourceTrees = {"{\"childTrees\":[{\"childTrees\":[],\"ip\":\"10.2.1.11\",\"name\":\"ecs.t1\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.t1\"},{\"childTrees\":[],\"ip\":\"10.2.1.12\",\"name\":\"ecs.s2\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.s2\"},{\"childTrees\":[],\"ip\":\"10.2.1.13\",\"name\":\"ecs.s3\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.s3\"},{\"childTrees\":[],\"ip\":\"10.2.1.14\",\"name\":\"ecs.c1\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.c1\"},{\"childTrees\":[],\"ip\":\"10.2.1.15\",\"name\":\"ecs.c2\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.c2\"},{\"childTrees\":[],\"ip\":\"10.2.1.16\",\"name\":\"ecs.s1\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.s1\"},{\"childTrees\":[],\"ip\":\"10.2.1.17\",\"name\":\"ecs.m1\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.m1\"},{\"childTrees\":[],\"ip\":\"10.2.1.18\",\"name\":\"ecs.m2\",\"resourceId\":\"DTCenterECS\",\"subResources\":[],\"type\":\"DTCenterECS\",\"uuid\":\"ecs.m2\"}],\"ip\":\"IP\",\"name\":\"CloudGlobalDashBoard\",\"resourceId\":\"DTCenterCloudGlobalDashBoard\",\"subResources\":[],\"type\":\"DTCenterCloudGlobalDashBoard\",\"uuid\":\"3195ab20-f136-3397-8736-a1e1a6498d8b\"}",""};
		//---------- kylin data ----------
		//String[] resourceTrees = {"{\"childTrees\":[{\"childTrees\":[],\"ip\":\"10.2.5.61\",\"name\":\"windows7\",\"resourceId\":\"KyLinVm\",\"subResources\":[],\"type\":\"KyLinVm\",\"uuid\":\"63d53dd3-794c-438e-bc95-18353bdeaa58\"},{\"childTrees\":[],\"ip\":\"10.2.5.59\",\"name\":\"kylin2008\",\"resourceId\":\"KyLinVm\",\"subResources\":[],\"type\":\"KyLinVm\",\"uuid\":\"7bb55806-918d-4d0b-98d1-d0be1ba860e3\"},{\"childTrees\":[],\"ip\":\"10.2.5.56\",\"name\":\"kylin\",\"resourceId\":\"KyLinVm\",\"subResources\":[],\"type\":\"KyLinVm\",\"uuid\":\"27709385-cbdd-4231-bd72-d93c2192712f\"}],\"ip\":\"10.2.5.1\",\"name\":\"银河麒麟虚拟化\",\"resourceId\":\"KyLinServer\",\"subResources\":[],\"type\":\"KyLinServer\",\"uuid\":\"0b284516-cf4d-3a0e-9dab-b10591ee6244\"}",""};
		//---------- vmware data ----------
		//String[] resourceTrees = {"{\"childTrees\":[{\"childTrees\":[{\"childTrees\":[{\"childTrees\":[{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/！！！！！测试管理服务器-243！！！！！禁删！！！！！\",\"ip\":\"\",\"name\":\"！！！！！测试管理服务器-243！！！！！禁删！！！！！\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52e8d3c1-cd21-18d0-d95a-65a54795233a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试Centos65-64-156\",\"ip\":\"\",\"name\":\"测试Centos65-64-156\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52b88ee9-9efb-a569-6d4a-b8f6820eaae0\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/外借周关力Windowns2008-64-43\",\"ip\":\"\",\"name\":\"外借周关力Windowns2008-64-43\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5234acf4-28ae-647a-9f8c-84ffd9956b09\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试Win2008-64-39测试部\",\"ip\":\"129.99.127.18\",\"name\":\"测试Win2008-64-39测试部\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5254a9b8-547f-416d-0e0d-d913af6dbf69\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2008-64-192.168.1.250-120G\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.250-120G\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52632e86-808a-25c4-4d98-2c9b8c24627a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试资源池[2]新建虚拟机[2]\",\"ip\":\"\",\"name\":\"测试资源池[2]新建虚拟机[2]\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"500047d8-c4be-c5f3-faa4-6d60099ba73d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/数据库Win2008-64-17oracle11g\",\"ip\":\"\",\"name\":\"数据库Win2008-64-17oracle11g\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ea0b19-a9a9-d975-5b08-f7f67f9b4c5b\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/徐阳！！！禁删！！！Windows2003-32-114公司腾讯通！！！\",\"ip\":\"\",\"name\":\"徐阳！！！禁删！！！Windows2003-32-114公司腾讯通！！！\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"523bae6f-1f4e-9a94-8797-c47fd8a76cac\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试Win2003-32-192.168.1.190\",\"ip\":\"\",\"name\":\"测试Win2003-32-192.168.1.190\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52c49bd9-2567-cac1-d136-51499163d5d5\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RHEL57-64-112\",\"ip\":\"\",\"name\":\"RHEL57-64-112\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"525fbfdb-2b44-dfab-7379-8512e10a3027\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试Centos65-64-182\",\"ip\":\"\",\"name\":\"测试Centos65-64-182\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ffdd1d-ef72-2902-ee5e-744b40763b22\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试RHEL57-64-241测试部\",\"ip\":\"\",\"name\":\"测试RHEL57-64-241测试部\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52de42e2-407f-2459-34ad-85f197921eba\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/赵品循！！！禁删！！！Ubuntu-32-119内网MYSQL服务器！！！\",\"ip\":\"\",\"name\":\"赵品循！！！禁删！！！Ubuntu-32-119内网MYSQL服务器！！！\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ce03e0-9308-549f-2939-ad67cafdef7f\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试资源池[2]新建虚拟机[1]\",\"ip\":\"\",\"name\":\"测试资源池[2]新建虚拟机[1]\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"50008a98-2288-87de-27a7-c27de68036a1\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试Win2003-32-192.168.1.232\",\"ip\":\"\",\"name\":\"测试Win2003-32-192.168.1.232\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52775ea0-a602-d144-85ab-6f48ad1755c1\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Redhat5.7-64-oracle-15-oc35\",\"ip\":\"\",\"name\":\"Redhat5.7-64-oracle-15-oc35\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"528a0c6d-57dc-e599-3311-55cc22aed69d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win7-192.168.1.134\",\"ip\":\"\",\"name\":\"Win7-192.168.1.134\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52757c2f-f042-49ed-191c-e8571543ab95\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/！！！Centos65-64-146公司HAPM_DEMO！！！\",\"ip\":\"\",\"name\":\"！！！Centos65-64-146公司HAPM_DEMO！！！\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52878c87-9819-e15d-c2e4-f7b4bea818c9\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/环境Win2008-64-226\",\"ip\":\"\",\"name\":\"环境Win2008-64-226\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5227bbf2-ec96-9a00-ed3f-bad7c2270312\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试资源池新建虚拟机[2]\",\"ip\":\"\",\"name\":\"测试资源池新建虚拟机[2]\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"500006c5-6506-a6ea-a8da-143d188d18be\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RHEL57-64-170英文版\",\"ip\":\"\",\"name\":\"RHEL57-64-170英文版\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"527ce741-6bc1-8d1c-8554-fd4d730a9435\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/环境Win2003-64-234\",\"ip\":\"\",\"name\":\"环境Win2003-64-234\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5293f7f9-bd6c-0f2e-627e-c419c0ad2fe6\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win7-192.168.1.184\",\"ip\":\"\",\"name\":\"Win7-192.168.1.184\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52f655fd-02e3-8657-eeff-21ebcc7d354d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试Win2003-64-127\",\"ip\":\"\",\"name\":\"测试Win2003-64-127\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52f598f0-c7a5-4e86-717e-c2ef36af8ad5\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/商务Windows2003-32-188文菊、周关力\",\"ip\":\"\",\"name\":\"商务Windows2003-32-188文菊、周关力\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"521462a0-e6a2-c99e-aa1b-7c17fd251f51\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试RHEL57-64-136测试部\",\"ip\":\"\",\"name\":\"测试RHEL57-64-136测试部\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"523fc71a-9bb5-3612-91f9-1dba480ac351\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2008-64-192.168.1.251-40G\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.251-40G\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5264bf27-8a39-7939-c210-508aa62eab9f\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/测试资源池新建虚拟机[1]\",\"ip\":\"\",\"name\":\"测试资源池新建虚拟机[1]\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5000e717-c6e5-e83f-bca1-cffe260b3eb2\"}],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/192.168.1.20\",\"ip\":\"192.168.1.20\",\"name\":\"192.168.1.20\",\"resourceId\":\"vmESXi\",\"subResources\":[{\"id\":\"key-vim.host.BlockHba-vmhba38\",\"name\":\"vmhba38\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"87cbf0c1-0967-37b8-a7f5-cdf69cebe859-/vmfs/devices/cdrom/mpx.vmhba38:C0:T0:L0\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"8f14e45f-ceea-367a-9a36-dedd4bea2543\"},{\"id\":\"key-vim.host.BlockHba-vmhba0\",\"name\":\"vmhba0\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"ae618ff4-643a-3044-a8c0-fe277ba2af4a\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.BlockHba-vmhba32\",\"name\":\"vmhba32\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"3e0c6226-ed78-3142-8bbc-22f15d105189\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.BlockHba-vmhba33\",\"name\":\"vmhba33\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"d5f9db2f-78bd-3f1e-ae29-713196b9caef\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.BlockHba-vmhba34\",\"name\":\"vmhba34\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"40a9d94a-cae6-3b1e-abbb-9f6eba9df0a3\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.BlockHba-vmhba35\",\"name\":\"vmhba35\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"30dae703-e5cc-3dc0-b835-28de1eefdfb1\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.BlockHba-vmhba36\",\"name\":\"vmhba36\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"5918914a-d7f8-31be-80a2-0ddf5cb1334b\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.BlockHba-vmhba37\",\"name\":\"vmhba37\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"e94d0965-8196-3606-811a-25584f0d7512\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.PhysicalNic-vmnic0\",\"name\":\"vmnic0\",\"resourceId\":\"EsxNetWorkAdapter\",\"subIndex\":\"9d1072de-7384-3f37-b26f-b14b3705b43b\",\"type\":\"EsxNetWorkAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"id\":\"key-vim.host.PhysicalNic-vmnic1\",\"name\":\"vmnic1\",\"resourceId\":\"EsxNetWorkAdapter\",\"subIndex\":\"e5001dac-5daf-37c7-a83a-d48dff0c98a8\",\"type\":\"EsxNetWorkAdapter\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"}],\"type\":\"HostSystem\",\"uuid\":\"710e079f-c2c7-30d3-ab61-e606b960cf83\"},{\"childTrees\":[{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/！！！外借Win2003-32-151周关力、杨茜redmine！！！\",\"ip\":\"192.168.1.151\",\"name\":\"！！！外借Win2003-32-151周关力、杨茜redmine！！！\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5246fdf5-0d27-b1e0-ecc0-9013585e9ab1\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-41-TD BAK\",\"ip\":\"192.168.1.41\",\"name\":\"Win2003-32-41-TD BAK\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52b96753-bf3f-b991-1e01-a2b21e48b7f9\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2008-64-35\",\"ip\":\"\",\"name\":\"Win2008-64-35\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5237615a-1720-0233-7ec3-c9996951ce20\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/XP-138-WMI\",\"ip\":\"\",\"name\":\"XP-138-WMI\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52084e05-f3e3-75bf-19f6-0d344a019b21\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat57-64-214\",\"ip\":\"\",\"name\":\"RedHat57-64-214\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52c2e4ee-0b9d-e210-aab2-2abb02fca825\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat57-64-142\",\"ip\":\"\",\"name\":\"RedHat57-64-142\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52d60068-22d6-4a28-a50b-861522c73139\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win7-165-外借何建平中集VPN\",\"ip\":\"\",\"name\":\"Win7-165-外借何建平中集VPN\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52d572b7-7248-5a85-ca7f-879305d6dad3\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/xp临-192.168.1.121杨海燕\",\"ip\":\"\",\"name\":\"xp临-192.168.1.121杨海燕\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5207bb00-b269-78d0-02a7-fffd8a3b70b2\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/数据库RedHat57-64-135mysql5.6.12\",\"ip\":\"\",\"name\":\"数据库RedHat57-64-135mysql5.6.12\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5202831b-41b3-d193-29e3-bd9a72bc5e32\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-33\",\"ip\":\"192.168.1.33\",\"name\":\"Win2003-32-33\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52a75c54-4e53-ee4b-8d6d-987ebea357eb\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/模拟器Win2003-32-171\",\"ip\":\"\",\"name\":\"模拟器Win2003-32-171\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"522ac131-0087-8120-7bf0-539887f9751f\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-143\",\"ip\":\"\",\"name\":\"Win2003-32-143\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52e71abb-c787-d392-1c90-00cfa781511a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat5-32-29-mysql\",\"ip\":\"\",\"name\":\"RedHat5-32-29-mysql\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52d557fd-7383-315d-1371-8e03897ca017\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-32\",\"ip\":\"\",\"name\":\"Win2003-32-32\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52d32fef-63ab-8ebe-ae45-a79a13470687\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat5-32-29旧\",\"ip\":\"\",\"name\":\"RedHat5-32-29旧\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5287ade6-9a85-469e-3661-63be67e3459a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat5-32-30\",\"ip\":\"192.168.1.30\",\"name\":\"RedHat5-32-30\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52842097-bbd2-1a4e-8353-cad0bc46a2d4\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/win7-32-247LR\",\"ip\":\"\",\"name\":\"win7-32-247LR\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5214d923-6139-c8c3-2bb4-881e9b8d526f\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2008-64-152\",\"ip\":\"\",\"name\":\"Win2008-64-152\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"525aca03-94f4-27b0-564a-c4905198087d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-36\",\"ip\":\"192.168.1.36\",\"name\":\"Win2003-32-36\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ed37eb-d159-cffe-f0f8-811fd17418f7\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/模拟器Win2008-64-49\",\"ip\":\"\",\"name\":\"模拟器Win2008-64-49\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ae383f-2653-4e29-8feb-e6b579e214cd\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-223\",\"ip\":\"\",\"name\":\"Win2003-32-223\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"521f64b1-5e28-e6ff-72ed-8eb71f751eb1\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2008-64-154刘宇峰\",\"ip\":\"192.168.1.154\",\"name\":\"Win2008-64-154刘宇峰\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52c49092-fdc2-e662-7c27-b403e20bf017\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-40\",\"ip\":\"\",\"name\":\"Win2003-32-40\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52b14059-4148-aaec-1b36-1c434b21c298\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat57-64-109\",\"ip\":\"\",\"name\":\"RedHat57-64-109\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52a45fe9-32f4-01d7-e323-6167044063f2\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/RedHat5-64-31-mysql\",\"ip\":\"192.168.1.31\",\"name\":\"RedHat5-64-31-mysql\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"526f89e6-23b7-646a-7249-c163fd625f03\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-37\",\"ip\":\"\",\"name\":\"Win2003-32-37\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5255fcd8-54df-f222-943c-716cddaa6236\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2008-64-172\",\"ip\":\"\",\"name\":\"Win2008-64-172\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52cf50f4-6186-0fc5-721f-3e13c58832e5\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/win7-32-238 LR\",\"ip\":\"\",\"name\":\"win7-32-238 LR\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"521ed858-ef6f-77c0-ff26-b5cf53f693ba\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/新建虚拟机asdfasdfasdf\",\"ip\":\"\",\"name\":\"新建虚拟机asdfasdfasdf\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"50004dea-a5bf-a0ea-86d5-07bd651fda85\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-34\",\"ip\":\"\",\"name\":\"Win2003-32-34\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5233cde3-28c6-23ae-8602-763e98e01e15\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/Win2003-32-38\",\"ip\":\"\",\"name\":\"Win2003-32-38\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5259d974-1570-de31-9e70-c594b7e543e0\"}],\"fullName\":\"/192.168.1.235/新建数据中心 2/7/192.168.1.18\",\"ip\":\"192.168.1.18\",\"name\":\"192.168.1.18\",\"resourceId\":\"vmESXi\",\"subResources\":[{\"id\":\"key-vim.host.BlockHba-vmhba0\",\"name\":\"vmhba0\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"ae618ff4-643a-3044-a8c0-fe277ba2af4a\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.BlockHba-vmhba32\",\"name\":\"vmhba32\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"3e0c6226-ed78-3142-8bbc-22f15d105189\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.BlockHba-vmhba33\",\"name\":\"vmhba33\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"d5f9db2f-78bd-3f1e-ae29-713196b9caef\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.BlockHba-vmhba34\",\"name\":\"vmhba34\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"40a9d94a-cae6-3b1e-abbb-9f6eba9df0a3\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.BlockHba-vmhba35\",\"name\":\"vmhba35\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"30dae703-e5cc-3dc0-b835-28de1eefdfb1\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.BlockHba-vmhba36\",\"name\":\"vmhba36\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"5918914a-d7f8-31be-80a2-0ddf5cb1334b\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.InternetScsiHba-vmhba37\",\"name\":\"vmhba37\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"e94d0965-8196-3606-811a-25584f0d7512\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.PhysicalNic-vmnic0\",\"name\":\"vmnic0\",\"resourceId\":\"EsxNetWorkAdapter\",\"subIndex\":\"9d1072de-7384-3f37-b26f-b14b3705b43b\",\"type\":\"EsxNetWorkAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"},{\"id\":\"key-vim.host.PhysicalNic-vmnic1\",\"name\":\"vmnic1\",\"resourceId\":\"EsxNetWorkAdapter\",\"subIndex\":\"e5001dac-5daf-37c7-a83a-d48dff0c98a8\",\"type\":\"EsxNetWorkAdapter\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"}],\"type\":\"HostSystem\",\"uuid\":\"d0473172-3e19-3adc-aa54-c1ceec3cb55f\"}],\"fullName\":\"/192.168.1.235/新建数据中心 2/7\",\"ip\":\"\",\"name\":\"7\",\"resourceId\":\"VMWareCluster\",\"subResources\":[],\"type\":\"ClusterComputeResource\",\"uuid\":\"8f14e45f-ceea-367a-9a36-dedd4bea2543\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/8\",\"ip\":\"\",\"name\":\"8\",\"resourceId\":\"VMWareCluster\",\"subResources\":[],\"type\":\"ClusterComputeResource\",\"uuid\":\"c9f0f895-fb98-3b91-99f5-1fd0297e236d\"},{\"childTrees\":[{\"childTrees\":[{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/新建虚拟机\",\"ip\":\"\",\"name\":\"新建虚拟机\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"50006875-47c1-542f-3bad-d2a17c387ea3\"}],\"fullName\":\"poolFullName\",\"ip\":\"poolIp\",\"name\":\"111\",\"resourceId\":\"\",\"subResources\":[],\"type\":\"ResourcePool\",\"uuid\":\"698d51a1-9d8a-321c-a581-499d7b7jason\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/性能Win2003-64-192.168.1.196ITOM\",\"ip\":\"\",\"name\":\"性能Win2003-64-192.168.1.196ITOM\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52c94a84-58d9-bd90-814c-3241c34b8539\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-192.168.1.245\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.245\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"523a20f0-8f07-86f9-abe3-04f04df84b7a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能Win2008-64-192.168.1.215ITOM\",\"ip\":\"\",\"name\":\"功能Win2008-64-192.168.1.215ITOM\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5209a060-4c6f-8232-cfab-eccc6a3edae0\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/性能rhel57-64-192.168.1.240ITSM\",\"ip\":\"\",\"name\":\"性能rhel57-64-192.168.1.240ITSM\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"520eb48d-87d2-cea1-fcc8-587af3dc7e9d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-192.168.1.199\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.199\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ea1c9f-0ed0-6f4c-9dc0-8d6e48457430\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/环境rhel57-64-192.168.1.51主机模拟器\",\"ip\":\"\",\"name\":\"环境rhel57-64-192.168.1.51主机模拟器\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52e6df31-9450-ee5b-4608-2d68f00ed559\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能Win2003-64-192.168.1.185\",\"ip\":\"\",\"name\":\"功能Win2003-64-192.168.1.185\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"527c5ece-4f00-08f9-c363-36d542f32c7b\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2003-32？-125山东易构外场设备服务器\",\"ip\":\"\",\"name\":\"Win2003-32？-125山东易构外场设备服务器\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"529811c6-8809-e687-9238-34f517a3c19a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-192.168.1.200\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.200\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"527610c7-469c-dab8-a247-3988a02ebb8a\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能rhel57-64-192.168.1.105\",\"ip\":\"\",\"name\":\"功能rhel57-64-192.168.1.105\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52136016-94f8-2617-5f3d-2c090ba84f79\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/姚晓霞长期3_Win2003-64-123\",\"ip\":\"\",\"name\":\"姚晓霞长期3_Win2003-64-123\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52b3bcaa-5955-f7c0-22f8-9f74910dbb49\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/姚晓霞长期1_Win2003-64-170\",\"ip\":\"\",\"name\":\"姚晓霞长期1_Win2003-64-170\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52b20e62-768d-10aa-522f-53081cca2b03\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/ubuntu_14.04_64_192.168.1.148\",\"ip\":\"\",\"name\":\"ubuntu_14.04_64_192.168.1.148\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52c009df-30a8-2f5f-e76e-8b929e18b377\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能rhel57-64-192.168.1.242UNIVIEW%2fITSM\",\"ip\":\"\",\"name\":\"功能rhel57-64-192.168.1.242UNIVIEW%2fITSM\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"523a1219-1954-bf50-bd08-be3f4b4f8b94\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-178 模拟器不要关闭\",\"ip\":\"\",\"name\":\"Win2008-64-178 模拟器不要关闭\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52a7434e-a2a3-8660-57be-9714e6aa5007\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/rhel57-64-192.168.1.198\",\"ip\":\"\",\"name\":\"rhel57-64-192.168.1.198\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"5278f7dd-da40-1cf7-5147-7e1a516c02de\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/！！！赵品循！！！禁删！！！Win7-32？-192.168.1.189外网服务器！！！！！！\",\"ip\":\"\",\"name\":\"！！！赵品循！！！禁删！！！Win7-32？-192.168.1.189外网服务器！！！！！！\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"524af41b-ee5e-c3ef-951f-2586232b678f\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-42\",\"ip\":\"\",\"name\":\"Win2008-64-42\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52877a3c-ec45-3885-00c3-0947038c39f1\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能Win2008-64-192.168.1.137ITM\",\"ip\":\"\",\"name\":\"功能Win2008-64-192.168.1.137ITM\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52037f0a-8e09-6bc4-e3a6-8b3f41a16297\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-192.168.1.220\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.220\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"525bf7a0-c7b3-d61a-3576-4df5f5b9cabb\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/win7-32-246-LR\",\"ip\":\"192.168.1.246\",\"name\":\"win7-32-246-LR\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"520d03a8-34bb-2142-06ee-9c6e26983bb8\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-126赵刚山东易构UNIVIEW-ITSM\",\"ip\":\"\",\"name\":\"Win2008-64-126赵刚山东易构UNIVIEW-ITSM\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52991a7b-02d0-7713-2146-732f038d8bc9\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能Win2008-64-192.168.1.133\",\"ip\":\"\",\"name\":\"功能Win2008-64-192.168.1.133\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52345502-8c25-acfb-925e-7181ca2442bb\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/罗爽Win2008-64-192.168.1.227\",\"ip\":\"\",\"name\":\"罗爽Win2008-64-192.168.1.227\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"527a16b4-f09a-5266-a76c-7a5293736394\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/test1\",\"ip\":\"\",\"name\":\"test1\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"500056b3-2c2b-0389-e464-ec7dfb69486d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/新建虚拟机pppppppppppppppppppppppp\",\"ip\":\"\",\"name\":\"新建虚拟机pppppppppppppppppppppppp\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"50002f92-2860-f338-9ff9-86beb91afc3f\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/环境Win2003-64-192.168.1.235VCENTER\",\"ip\":\"\",\"name\":\"环境Win2003-64-192.168.1.235VCENTER\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52e9c959-79ac-17fc-81dc-7558c9c90051\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/功能Win2003-64-192.168.1.183_原122\",\"ip\":\"\",\"name\":\"功能Win2003-64-192.168.1.183_原122\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ce70b4-5a32-dc9b-a94e-fed85d32533d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2003-32-192.168.1.249谢飞信息办ITM20121228\",\"ip\":\"\",\"name\":\"Win2003-32-192.168.1.249谢飞信息办ITM20121228\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"520e2df3-1aa3-9ad1-7316-25962005cc80\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2008-64-192.168.1.174\",\"ip\":\"\",\"name\":\"Win2008-64-192.168.1.174\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"52ea8dba-1d26-3a5a-db0a-8a8e4b978e29\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/Win2003-64-208张建辉\",\"ip\":\"\",\"name\":\"Win2003-64-208张建辉\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"527ef0ea-7cab-d36e-a2cd-29fe56082c91\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22/新建虚拟机\",\"ip\":\"\",\"name\":\"新建虚拟机\",\"resourceId\":\"VMWareVM\",\"subResources\":[],\"type\":\"VirtualMachine\",\"uuid\":\"50006875-47c1-542f-3bad-d2a17c387ea3\"}],\"fullName\":\"/192.168.1.235/新建数据中心 2/192.168.1.22\",\"ip\":\"192.168.1.22\",\"name\":\"192.168.1.22\",\"resourceId\":\"vmESXi\",\"subResources\":[{\"id\":\"key-vim.host.BlockHba-vmhba38\",\"name\":\"vmhba38\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"87cbf0c1-0967-37b8-a7f5-cdf69cebe859\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba0\",\"name\":\"vmhba0\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"ae618ff4-643a-3044-a8c0-fe277ba2af4a\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba32\",\"name\":\"vmhba32\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"3e0c6226-ed78-3142-8bbc-22f15d105189\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba33\",\"name\":\"vmhba33\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"d5f9db2f-78bd-3f1e-ae29-713196b9caef\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba34\",\"name\":\"vmhba34\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"40a9d94a-cae6-3b1e-abbb-9f6eba9df0a3\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba35\",\"name\":\"vmhba35\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"30dae703-e5cc-3dc0-b835-28de1eefdfb1\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba36\",\"name\":\"vmhba36\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"5918914a-d7f8-31be-80a2-0ddf5cb1334b-/vmfs/devices/cdrom/mpx.vmhba36:C0:T0:L0\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.BlockHba-vmhba37\",\"name\":\"vmhba37\",\"resourceId\":\"EsxStorageAdapter\",\"subIndex\":\"e94d0965-8196-3606-811a-25584f0d7512\",\"type\":\"EsxStorageAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.PhysicalNic-vmnic0\",\"name\":\"vmnic0\",\"resourceId\":\"EsxNetWorkAdapter\",\"subIndex\":\"9d1072de-7384-3f37-b26f-b14b3705b43b\",\"type\":\"EsxNetWorkAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"id\":\"key-vim.host.PhysicalNic-vmnic1\",\"name\":\"vmnic1\",\"resourceId\":\"EsxNetWorkAdapter\",\"subIndex\":\"e5001dac-5daf-37c7-a83a-d48dff0c98a8\",\"type\":\"EsxNetWorkAdapter\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"}],\"type\":\"HostSystem\",\"uuid\":\"16ef0997-4172-33ed-a600-8e193db13088\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/datastore1 (2)\",\"ip\":\"\",\"name\":\"datastore1 (2)\",\"resourceId\":\"VMWareDatastore\",\"subResources\":[],\"type\":\"Datastore\",\"uuid\":\"4fccc301-6b3c8bae-74dd-0025906a536d\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/datastore1 (3)-1\",\"ip\":\"\",\"name\":\"datastore1 (3)-1\",\"resourceId\":\"VMWareDatastore\",\"subResources\":[],\"type\":\"Datastore\",\"uuid\":\"50291fd6-1c0efca0-f54a-008cfa0c6a49\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/datastore1\",\"ip\":\"\",\"name\":\"datastore1\",\"resourceId\":\"VMWareDatastore\",\"subResources\":[],\"type\":\"Datastore\",\"uuid\":\"502a3b98-5c734a22-a419-008cfa0c6a35\"},{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心 2/datastore2\",\"ip\":\"\",\"name\":\"datastore2\",\"resourceId\":\"VMWareDatastore\",\"subResources\":[],\"type\":\"Datastore\",\"uuid\":\"4fd0c6ae-642af008-86a6-0025906a536d\"}],\"fullName\":\"/192.168.1.235/新建数据中心 2\",\"ip\":\"\",\"name\":\"新建数据中心 2\",\"resourceId\":\"VMWareDatastore\",\"subResources\":[],\"type\":\"Datacenter\",\"uuid\":\"58b5668d-ff17-3195-b406-86974868a911\"},{\"childTrees\":[{\"childTrees\":[],\"fullName\":\"/192.168.1.235/新建数据中心/9\",\"ip\":\"\",\"name\":\"9\",\"resourceId\":\"VMWareCluster\",\"subResources\":[],\"type\":\"ClusterComputeResource\",\"uuid\":\"45c48cce-2e2d-3fbd-aa1a-fc51c7c6ad26\"}],\"fullName\":\"/192.168.1.235/新建数据中心\",\"ip\":\"\",\"name\":\"新建数据中心\",\"resourceId\":\"VMWareDatastore\",\"subResources\":[],\"type\":\"Datacenter\",\"uuid\":\"fe1f7b6d-eb7d-364a-8783-1b69d190075c\"}],\"fullName\":\"/192.168.1.235\",\"ip\":\"\",\"name\":\"192.168.1.235\",\"resourceId\":\"VMWareVCenter\",\"subResources\":[],\"type\":\"VCenter\",\"uuid\":\"27cc2c41-977c-3217-beef-b278bcbc4272\"}", ""};
		Map resourceTreeMap = JSONObject.parseObject(resourceTrees[0], HashMap.class);
		instantiatedResource(resourceTreeMap, dParaBo, treeList, null, null, null, null, null, resultCollection,vmResourceTreePos);
	}
	
	/**
	 * 迭代发现返回字符串
	 * 实例化资源
	 * @param resourceTreeMap
	 * @param dcs
	 * @param domainId
	 */
	private void instantiatedResource(Map resourceTreeMap, VmDiscoveryParaBo dParaBo, List<Map<String, Object>> showTreeList, String puuid,
			String vcenteruuid, String clusteruuid, String hostuuid, String datacenteruuid, Map<String, Object> resultCollection,List<VmResourceTreePo> vmResourceTreePos) {
		
		Map<String, Object> showTreeMap = new HashMap<String, Object>();
		showTreeList.add(showTreeMap);
		// 是否入库
		boolean needInstantiated = true;
		// 初始资源实例
		ResourceInstance ri = new ResourceInstance();
		ri.setName((String)resourceTreeMap.get(RESOURCETREE_NODE_NAME));
		ri.setShowName(ri.getName());
		ri.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
		ri.setShowIP((String) resourceTreeMap.get(RESOURCETREE_NODE_IP));
		ri.setDiscoverNode(String.valueOf(dParaBo.getDCS()));
		ri.setDomainId(dParaBo.getDomainId());
		String resourceId = (String)resourceTreeMap.get(RESOURCETREE_NODE_RESOURCEID);
		ResourceDef resourceDef = null;
		if(resourceId != null && !"".equals(resourceId)){
			resourceDef = capacityService.getResourceDefById(resourceId);
		}else{
			needInstantiated = false;
		}
		
		// 资源关系写入数据库
		String uuid = (String)resourceTreeMap.get(RESOURCETREE_NODE_UUID);
		
		VmResourceTreePo newVmTreePo = new VmResourceTreePo();
		newVmTreePo.setUuid(uuid);
		newVmTreePo.setPuuid(puuid);
		newVmTreePo.setVmtype((String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE));
		newVmTreePo.setVmname((String)resourceTreeMap.get(RESOURCETREE_NODE_NAME));
		newVmTreePo.setVmfullname((String)resourceTreeMap.get(RESOURCETREE_NODE_FULLNAME));
		newVmTreePo.setVcenteruuid(vcenteruuid);
		newVmTreePo.setDatacenteruuid(datacenteruuid);
		newVmTreePo.setClusteruuid(clusteruuid);
		newVmTreePo.setHostuuid(hostuuid);
		// 控制license数量
		String categoryId = resourceDef != null ? resourceDef.getCategory().getId() : (String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE);
		if(licenseCalcService.isLicenseEnough(categoryId)){
			VmResourceTreePo vmTreePo = vmResourceTreeDao.selectByUuid(uuid);
			// 资源实例写入数据库
			if(needInstantiated && resourceDef != null){
				// 发现属性
				ri.setDiscoverProps(createDiscoverProps(dParaBo));
				// 模型属性
				List<ModuleProp> mpList = new ArrayList<ModuleProp>();
				StringBuffer instantiation = new StringBuffer(500);
				String[] instanceIds = resourceDef.getInstantiationDef().getInstanceId();
				ResourcePropertyDef[] resourcePropertyDefs = resourceDef.getPropertyDefs();
				for(int i = 0; resourcePropertyDefs != null && i < resourcePropertyDefs.length; i ++){
					String key = resourcePropertyDefs[i].getId();
					String value = (String) resourceTreeMap.get(key);
					ModuleProp mp = new ModuleProp();
					mp.setKey(key);
					mp.setValues(new String[]{value});
					mpList.add(mp);
					if(Arrays.binarySearch(instanceIds, key) >= 0){
						instantiation.append(value);
					}
				}
				if(!mpList.isEmpty()){
					if(instantiation.length() > 0){
						ModuleProp mp = new ModuleProp();
						mp.setKey(SERVER_INST_IDENTY_KEY);
						mp.setValues(new String[]{SecureUtil.md5Encryp(instantiation.toString())});
						mpList.add(mp);
					}
					ri.setModuleProps(mpList);
				}
				// 加入子资源
				Object subResources = resourceTreeMap.get(RESOURCETREE_NODE_SUBRESOURCES);
				if(subResources != null && subResources instanceof JSONArray){
					List<ResourceInstance> childRiList = createChildResourceInstance((JSONArray)subResources, resourceDef, dParaBo);
					ri.setChildren(childRiList);
				}
				// 主资源的resourceId and categoryId
				ri.setResourceId(resourceDef.getId());
				ri.setCategoryId(resourceDef.getCategory().getId());
				// 实例化resourceInstance
				try {
					ResourceInstanceResult rir = resourceInstanceService.addResourceInstance(ri);
					
					// 刷新资源  修改bug 密码修改后不生效的问题
					if(rir.isRepeat()){
						ri.setId(rir.getResourceInstanceId());
//						rir.getInstanceLiftStatte().get(ri.getId());
//						ri.setLifeState(rir.g);
						resourceInstanceService.refreshResourceInstance(ri);
					}
					showTreeMap.put("isRepeat", rir.isRepeat());
					showTreeMap.put("id", rir.getResourceInstanceId());
					
					newVmTreePo.setInstanceid(rir.getResourceInstanceId());
					newVmTreePo.setResourceid(ri.getResourceId());
					// 新的资源ID为下个资源的PID,用于结构表示
					switch (ri.getResourceId()) {
					case "VMWareVCenter":
					case "VMWareVCenter5.5":
					case "VMWareVCenter6":
					case "VMWareVCenter6.5":
					case "FusionComputeSite":
					case "FusionComputeOnePointThreeSite":
						vcenteruuid = uuid;
						break;
					case "vmESXi":
					case "vmESXi5.5":
					case "vmESXi6":
					case "vmESXi6.5":
					case "XenHost":
					case "KvmHost":
					case "FusionComputeHost":
					case "FusionComputeOnePointThreeHost":
						hostuuid = uuid;
						break;
					case "VMWareCluster":
					case "VMWareCluster5.5":
					case "VMWareCluster6":
					case "VMWareCluster6.5":
					case "XenPool":
					case "FusionComputeCluster":
					case "FusionComputeOnePointThreeCluster":
						clusteruuid = uuid;
						break;
					default:
						break;
					}
					// 这里要判断关系在数据库中是否存在 -- 这里分两处写是因为入库失败则不写入关系表
					if(vmTreePo == null){
						vmResourceTreeDao.insert(newVmTreePo);
					}else{
						vmResourceTreeDao.updateByUuid(newVmTreePo);
					}
				} catch (InstancelibException e) {
					logger.error("VM addResourceInstance", e);
					e.printStackTrace();
				}
			}else{
				// 这里要判断关系在数据库中是否存在 -- 这里分两处写是因为入库失败则不写入关系表
				if(vmTreePo == null){
					vmResourceTreeDao.insert(newVmTreePo);
				}else{
					vmResourceTreeDao.updateByUuid(newVmTreePo);
				}
				showTreeMap.put("id", uuid);
			}
			
			// 数据中心
			if ("Datacenter".equals((String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE))) {
				datacenteruuid = (String)resourceTreeMap.get(RESOURCETREE_NODE_UUID);
			}
			puuid = uuid;
			
			// 前台节点显示
			showTreeMap.put("name", ri.getShowName());
			String resourceType = (String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE);
			showTreeMap.put("type", resourceType);
			// 前台子节点
			List<Map<String, Object>> showChildTreeList = new ArrayList<Map<String, Object>>();
			showTreeMap.put("children", showChildTreeList);
			// 是否有下级节点,资源池内的虚拟机不重复入库
			if(!resourceType.equals("ResourcePool")){
				if(resourceTreeMap.containsKey(RESOURCETREE_NODE_CHILDTREES)){
					Object childTrees = resourceTreeMap.get(RESOURCETREE_NODE_CHILDTREES);
					if(childTrees instanceof JSONArray){
						JSONArray jsonArray = (JSONArray)childTrees;
						boolean flag = true;
						
						//针对资源池,虚拟机全入库后,对所属资源池做更新
						List<Map> resourcePoolList = new ArrayList<Map>();
						for(int i = 0; i < jsonArray.size(); i++){
							Map childMap = JSONObject.parseObject(jsonArray.get(i).toString(), HashMap.class);
							
							String type = (String)childMap.get(RESOURCETREE_NODE_TYPE);
							if(type.equals("ResourcePool")){
								if(flag){
									//删除之前保存的资源池
									vmResourceTreeDao.delResourcePoolByUuid(uuid);
									//将相关虚拟机信息更新
									List<VmResourceTreePo> vrtList = vmResourceTreeDao.getResourcePoolVM(uuid);
									for(VmResourceTreePo vrt:vrtList){
										vrt.setResourcepool("");
										vmResourceTreeDao.updateByUuid(vrt);
										vrt.getDatacenteruuid();
									}
									flag = false;
								}
								instantiatedResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
										clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
								resourcePoolList.add(childMap);
								//针对资源池,特殊处理,无child不入库,暂不过滤
//								if(childMap.containsKey(RESOURCETREE_NODE_CHILDTREES)){
//									Object childTreeTemp = childMap.get(RESOURCETREE_NODE_CHILDTREES);
//									if(childTreeTemp instanceof JSONArray){
//										JSONArray jsonArrayTemp = (JSONArray)childTreeTemp;
//										if(jsonArrayTemp.size()>0){
//											instantiatedResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
//													clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
//											
//											resourcePoolList.add(childMap);
//										}
//									}else if(childTreeTemp instanceof JSONObject){
//										Map childMapTemp = JSONObject.parseObject(((JSONObject)childTreeTemp).toString(), HashMap.class);
//										if(null!=childMapTemp){
//											instantiatedResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
//													clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
//											resourcePoolList.add(childMap);
//										}
//									}
//								}
							}else{
								instantiatedResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
										clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
							}
						}
						if(resourcePoolList.size()>0){
							handlResourcePoolTree(resourcePoolList);
						}
					}else if(childTrees instanceof JSONObject){
						Map childMap = JSONObject.parseObject(((JSONObject)childTrees).toString(), HashMap.class);
						instantiatedResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid, clusteruuid,
								hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
					}
				}
			}
		}else{
			CategoryDef cdf = capacityService.getCategoryById(categoryId);
			String typeName = "";
			if(null!=cdf){
				typeName = cdf.getName();
			}
			resultCollection.put("tooltips", typeName+"超过License授权数,无法继续添加!");
			String vmType = newVmTreePo.getVmtype();
			//查找是否包含该宿主机
			if(vmType.equals("FusionComputeHost")||
					vmType.equals("HostSystem")||
					vmType.equals("XenHost")||
					vmType.equals("FusionComputeOnePointThreeHost")){
				if(vmResourceTreeDao.selectByUuid(newVmTreePo.getUuid())==null){
					vmResourceTreePos.add(newVmTreePo);
				}
			}
			
		}
	}
	
	private void handlResourcePoolTree(List<Map> mapList){
		for(Map map:mapList){
			String poolUuid = (String)map.get(RESOURCETREE_NODE_UUID);
			if(map.containsKey(RESOURCETREE_NODE_CHILDTREES)){
				Object childTreeTemp = map.get(RESOURCETREE_NODE_CHILDTREES);
				if(childTreeTemp instanceof JSONArray){
					JSONArray jsonArray = (JSONArray)childTreeTemp;
					for(int i = 0; i < jsonArray.size(); i++){
						Map childMap = JSONObject.parseObject(jsonArray.get(i).toString(), HashMap.class);
						String vMachineUuid = (String)childMap.get(RESOURCETREE_NODE_UUID);
						
						//更新虚拟机Resourcepool字段
						VmResourceTreePo po = vmResourceTreeDao.selectByUuid(vMachineUuid);
						if(null!=po){
							po.setResourcepool(poolUuid);
							vmResourceTreeDao.updateByUuid(po);
						}
					}
				}else if(childTreeTemp instanceof JSONObject){
					Map childMap = JSONObject.parseObject(((JSONObject)childTreeTemp).toString(), HashMap.class);
					
					String vMachineUuid = (String)childMap.get(RESOURCETREE_NODE_UUID);
					//更新虚拟机Resourcepool字段
					VmResourceTreePo po = vmResourceTreeDao.selectByUuid(vMachineUuid);
					if(null!=po){
						po.setResourcepool(poolUuid);
						vmResourceTreeDao.updateByUuid(po);
					}
				}
			}
		}
	}
	
	private void createReDiscoveryResourceInstance(ResourceInstance ri, VmDiscoveryParaBo dParaBo, List<Map<String, Object>> treeList, Map<String, Object> resultCollection,List<VmResourceTreePo> vmResourceTreePos){	
		String[] resourceTrees = ri.getModulePropBykey("resourceTree");
		Map resourceTreeMap = JSONObject.parseObject(resourceTrees[0], HashMap.class);
		instantiatedReDiscoveryResource(resourceTreeMap, dParaBo, treeList, null, null, null, null, null, resultCollection,vmResourceTreePos);
	}
	
	private void parseResourceTree(Map resourceTreeMap,List<VmResourceTreePo> instanceList){
		String uuid = (String)resourceTreeMap.get(RESOURCETREE_NODE_UUID);
		String type = (String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE);
		
		VmResourceTreePo vmTreePo = new VmResourceTreePo();
		vmTreePo.setUuid(uuid);
		vmTreePo.setVmtype(type);
		instanceList.add(vmTreePo);
		
		if(resourceTreeMap.containsKey(RESOURCETREE_NODE_CHILDTREES)){
			Object childTrees = resourceTreeMap.get(RESOURCETREE_NODE_CHILDTREES);
			if(childTrees instanceof JSONArray){
				JSONArray jsonArray = (JSONArray)childTrees;
				for(int i = 0; i < jsonArray.size(); i++){
					Map childMap = JSONObject.parseObject(jsonArray.get(i).toString(), HashMap.class);
					parseResourceTree(childMap,instanceList);
				}
			}else if(childTrees instanceof JSONObject){
				Map childMap = JSONObject.parseObject(((JSONObject)childTrees).toString(), HashMap.class);
				parseResourceTree(childMap,instanceList);
			}
		}
	}
	
	private void instantiatedReDiscoveryResource(Map resourceTreeMap, VmDiscoveryParaBo dParaBo, List<Map<String, Object>> showTreeList, String puuid,
			String vcenteruuid, String clusteruuid, String hostuuid, String datacenteruuid, Map<String, Object> resultCollection,List<VmResourceTreePo> vmResourceTreePos) {
		
		Map<String, Object> showTreeMap = new HashMap<String, Object>();
		showTreeList.add(showTreeMap);
		//是否重复资源
		boolean ifRepeatByuuid = false;
		
		// 是否入库
		boolean needInstantiated = true;
		// 初始资源实例
		ResourceInstance ri = new ResourceInstance();
		ri.setName((String)resourceTreeMap.get(RESOURCETREE_NODE_NAME));
		ri.setShowName(ri.getName());
		ri.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
		ri.setShowIP((String) resourceTreeMap.get(RESOURCETREE_NODE_IP));
		ri.setDiscoverNode(String.valueOf(dParaBo.getDCS()));
		ri.setDomainId(dParaBo.getDomainId());
		String resourceId = (String)resourceTreeMap.get(RESOURCETREE_NODE_RESOURCEID);
		ResourceDef resourceDef = null;
		if(resourceId != null && !"".equals(resourceId)){
			resourceDef = capacityService.getResourceDefById(resourceId);
		}else{
			needInstantiated = false;
		}
		
		// 资源关系写入数据库
		String uuid = (String)resourceTreeMap.get(RESOURCETREE_NODE_UUID);
		
		logger.info("autoRerfreshVmResource instantiatedReDiscoveryResource all uuid="+uuid);
		
		VmResourceTreePo newVmTreePo = new VmResourceTreePo();
		newVmTreePo.setUuid(uuid);
		newVmTreePo.setPuuid(puuid);
		newVmTreePo.setVmtype((String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE));
		newVmTreePo.setVmname((String)resourceTreeMap.get(RESOURCETREE_NODE_NAME));
		newVmTreePo.setVmfullname((String)resourceTreeMap.get(RESOURCETREE_NODE_FULLNAME));
		newVmTreePo.setVcenteruuid(vcenteruuid);
		newVmTreePo.setDatacenteruuid(datacenteruuid);
		newVmTreePo.setClusteruuid(clusteruuid);
		newVmTreePo.setHostuuid(hostuuid);
		// 控制license数量
		String categoryId = resourceDef != null ? resourceDef.getCategory().getId() : (String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE);
		VmResourceTreePo vmTreePo = vmResourceTreeDao.selectByUuid(uuid);
		
		logger.info("this is instantiatedReDiscoveryResource vmTreePo != null("+vmTreePo != null+")"
				+" | licenseCalcService.isLicenseEnough("+categoryId+")="+licenseCalcService.isLicenseEnough(categoryId)
				+" | needInstantiated="+needInstantiated
				+" | resourceDef != null("+(resourceDef != null)+")"
				);
		
		//如果是重复资源,不需要验证lisence
		if(vmTreePo != null || licenseCalcService.isLicenseEnough(categoryId)){
			// 资源实例写入数据库
			if(needInstantiated && resourceDef != null){
				logger.info("this is instantiatedReDiscoveryResource lisence check and needInstantiated");
				
				// 发现属性
				ri.setDiscoverProps(createDiscoverProps(dParaBo));
				// 模型属性
				List<ModuleProp> mpList = new ArrayList<ModuleProp>();
				StringBuffer instantiation = new StringBuffer(500);
				String[] instanceIds = resourceDef.getInstantiationDef().getInstanceId();
				ResourcePropertyDef[] resourcePropertyDefs = resourceDef.getPropertyDefs();
				for(int i = 0; resourcePropertyDefs != null && i < resourcePropertyDefs.length; i ++){
					String key = resourcePropertyDefs[i].getId();
					String value = (String) resourceTreeMap.get(key);
					ModuleProp mp = new ModuleProp();
					mp.setKey(key);
					mp.setValues(new String[]{value});
					mpList.add(mp);
					if(Arrays.binarySearch(instanceIds, key) >= 0){
						instantiation.append(value);
					}
				}
				if(!mpList.isEmpty()){
					if(instantiation.length() > 0){
						ModuleProp mp = new ModuleProp();
						mp.setKey(SERVER_INST_IDENTY_KEY);
						mp.setValues(new String[]{SecureUtil.md5Encryp(instantiation.toString())});
						mpList.add(mp);
					}
					ri.setModuleProps(mpList);
				}
				// 加入子资源
				Object subResources = resourceTreeMap.get(RESOURCETREE_NODE_SUBRESOURCES);
				if(subResources != null && subResources instanceof JSONArray){
					List<ResourceInstance> childRiList = createChildResourceInstance((JSONArray)subResources, resourceDef, dParaBo);
					ri.setChildren(childRiList);
				}
				// 主资源的resourceId and categoryId
				ri.setResourceId(resourceDef.getId());
				ri.setCategoryId(resourceDef.getCategory().getId());
				// 实例化resourceInstance
				try {
					logger.info("autoRerfreshVmResource instantiatedReDiscoveryResource add uuid="+uuid);
					ResourceInstanceResult rir = resourceInstanceService.addResourceInstance(ri);
					
					// 刷新资源  修改bug 密码修改后不生效的问题
					if(rir.isRepeat()){
						ri.setId(rir.getResourceInstanceId());
						resourceInstanceService.refreshResourceInstance(ri);
					}
					
					InstanceLifeStateEnum ilse = ri.getLifeState();
					boolean ifmonitored = true;
					if(ilse!=null){
						switch (ilse) {
						case NOT_MONITORED:
							ifmonitored = false;
							break;
						default:
							break;
						}
					}
					showTreeMap.put("ifMonitored", ifmonitored);
					showTreeMap.put("isRepeat", rir.isRepeat());
					showTreeMap.put("id", rir.getResourceInstanceId());
					
					newVmTreePo.setInstanceid(rir.getResourceInstanceId());
					newVmTreePo.setResourceid(ri.getResourceId());
					// 新的资源ID为下个资源的PID,用于结构表示
					switch (ri.getResourceId()) {
					case "VMWareVCenter":
					case "VMWareVCenter5.5":
					case "VMWareVCenter6":
					case "VMWareVCenter6.5":
					case "FusionComputeSite":
					case "FusionComputeOnePointThreeSite":
						vcenteruuid = uuid;
						break;
					case "vmESXi":
					case "vmESXi5.5":
					case "vmESXi6":
					case "vmESXi6.5":
					case "XenHost":
					case "KvmHost":
					case "FusionComputeHost":
					case "FusionComputeOnePointThreeHost":
						hostuuid = uuid;
						break;
					case "VMWareCluster":
					case "VMWareCluster5.5":
					case "VMWareCluster6":
					case "VMWareCluster6.5":
					case "XenPool":
					case "FusionComputeCluster":
					case "FusionComputeOnePointThreeCluster":
						clusteruuid = uuid;
						break;
					default:
						break;
					}
					// 这里要判断关系在数据库中是否存在 -- 这里分两处写是因为入库失败则不写入关系表
					if(vmTreePo == null){
						vmResourceTreeDao.insert(newVmTreePo);
					}else{
						ifRepeatByuuid = true;
						vmResourceTreeDao.updateByUuid(newVmTreePo);
					}
				} catch (InstancelibException e) {
					logger.error("VM addResourceInstance", e);
					e.printStackTrace();
				}
			}else{
				// 这里要判断关系在数据库中是否存在 -- 这里分两处写是因为入库失败则不写入关系表
				if(vmTreePo == null){
					vmResourceTreeDao.insert(newVmTreePo);
				}else{
					ifRepeatByuuid = true;
					vmResourceTreeDao.updateByUuid(newVmTreePo);
				}
				showTreeMap.put("id", uuid);
			}
			
			// 数据中心
			if ("Datacenter".equals((String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE))) {
				datacenteruuid = (String)resourceTreeMap.get(RESOURCETREE_NODE_UUID);
			}
			puuid = uuid;
			
//			ifRepeatByuuid = false;
			
			showTreeMap.put("uuidRepeat", ifRepeatByuuid);
			// 前台节点显示
			showTreeMap.put("name", ri.getShowName());
			String resourceType = (String)resourceTreeMap.get(RESOURCETREE_NODE_TYPE);
			showTreeMap.put("type", resourceType);
			// 前台子节点
			List<Map<String, Object>> showChildTreeList = new ArrayList<Map<String, Object>>();
			showTreeMap.put("children", showChildTreeList);
			
			// 是否有下级节点,资源池内的虚拟机不重复入库
			if(!resourceType.equals("ResourcePool")){
				if(resourceTreeMap.containsKey(RESOURCETREE_NODE_CHILDTREES)){
					Object childTrees = resourceTreeMap.get(RESOURCETREE_NODE_CHILDTREES);
					if(childTrees instanceof JSONArray){
						JSONArray jsonArray = (JSONArray)childTrees;
						boolean flag = true;
						
						//针对资源池,虚拟机全入库后,对所属资源池做更新
						List<Map> resourcePoolList = new ArrayList<Map>();
						for(int i = 0; i < jsonArray.size(); i++){
							Map childMap = JSONObject.parseObject(jsonArray.get(i).toString(), HashMap.class);
							
							String type = (String)childMap.get(RESOURCETREE_NODE_TYPE);
							if(type.equals("ResourcePool")){
								if(flag){
									//删除之前保存的资源池
									vmResourceTreeDao.delResourcePoolByUuid(uuid);
									flag = false;
								}
								instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
										clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
								resourcePoolList.add(childMap);
								//针对资源池,特殊处理,无child不入库
//								if(childMap.containsKey(RESOURCETREE_NODE_CHILDTREES)){
//									Object childTreeTemp = childMap.get(RESOURCETREE_NODE_CHILDTREES);
//									if(childTreeTemp instanceof JSONArray){
//										JSONArray jsonArrayTemp = (JSONArray)childTreeTemp;
//										if(jsonArrayTemp.size()>0){
//											instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
//													clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
//										}
//										resourcePoolList.add(childMap);
//									}else if(childTreeTemp instanceof JSONObject){
//										Map childMapTemp = JSONObject.parseObject(((JSONObject)childTreeTemp).toString(), HashMap.class);
//										if(null!=childMapTemp){
//											instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
//													clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
//										}
//										resourcePoolList.add(childMap);
//									}
//								}
							}else{
								instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
										clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
							}
						}
						if(resourcePoolList.size()>0){
							handlResourcePoolTree(resourcePoolList);
						}
					}else if(childTrees instanceof JSONObject){
						Map childMap = JSONObject.parseObject(((JSONObject)childTrees).toString(), HashMap.class);
						instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid, clusteruuid,
								hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
					}
				}
			}
			
//			if(resourceTreeMap.containsKey(RESOURCETREE_NODE_CHILDTREES)){
//				Object childTrees = resourceTreeMap.get(RESOURCETREE_NODE_CHILDTREES);
//				if(childTrees instanceof JSONArray){
//					JSONArray jsonArray = (JSONArray)childTrees;
//					for(int i = 0; i < jsonArray.size(); i++){
//						Map childMap = JSONObject.parseObject(jsonArray.get(i).toString(), HashMap.class);
//						instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid,
//								clusteruuid, hostuuid, datacenteruuid, resultCollection,vmResourceTreePos );
//					}
//				}else if(childTrees instanceof JSONObject){
//					Map childMap = JSONObject.parseObject(((JSONObject)childTrees).toString(), HashMap.class);
//					instantiatedReDiscoveryResource(childMap, dParaBo, showChildTreeList, puuid, vcenteruuid, clusteruuid,
//							hostuuid, datacenteruuid, resultCollection,vmResourceTreePos);
//				}
//			}
		}else{
			CategoryDef cdf = capacityService.getCategoryById(categoryId);
			String typeName = "";
			if(null!=cdf){
				typeName = cdf.getName();
			}
			resultCollection.put("tooltips", typeName+"超过License授权数,无法继续添加!");
			String vmType = newVmTreePo.getVmtype();
			//查找是否包含该宿主机
			if(vmType.equals("FusionComputeHost")||vmType.equals("HostSystem")||
					vmType.equals("XenHost")||vmType.equals("FusionComputeOnePointThreeHost")){
				if(vmResourceTreeDao.selectByUuid(newVmTreePo.getUuid())==null){
					vmResourceTreePos.add(newVmTreePo);
				}
			}
			
		}
	}
	
	
	/**
	 * 创建子资源
	 * @param childArray
	 * @param mainRDef
	 * @param dcs
	 * @param domainId
	 * @return
	 */
	private List<ResourceInstance> createChildResourceInstance(JSONArray childArray, ResourceDef mainRDef, VmDiscoveryParaBo dParaBo){
		List<ResourceInstance> riList = new ArrayList<ResourceInstance>();
		for(int i = 0; childArray != null && i < childArray.size(); i ++){
			Map childMap = JSONObject.parseObject(childArray.get(i).toString(), HashMap.class);
			ResourceDef[] childRDefs = mainRDef.getChildResourceDefs();
			for(int j = 0; childRDefs != null && j < childRDefs.length; j ++){
				ResourceDef childRDef = childRDefs[j];
				if(childRDef.getId().equals(childMap.get(RESOURCETREE_NODE_RESOURCEID))){
					ResourceInstance childRi = new ResourceInstance();
					childRi.setResourceId(childRDef.getId());
					childRi.setCategoryId(null);
					childRi.setChildType(childRDef.getType());
					childRi.setLifeState(InstanceLifeStateEnum.MONITORED);
					childRi.setDiscoverNode(String.valueOf(dParaBo.getDCS()));
					childRi.setDomainId(dParaBo.getDomainId());
					childRi.setName((String)childMap.get(RESOURCETREE_NODE_NAME));
					childRi.setShowName(childRi.getName());
					// 模型属性
					List<ModuleProp> mpList = new ArrayList<ModuleProp>();
					ResourcePropertyDef[] resourcePropertyDefs = childRDef.getPropertyDefs();
					for(int k = 0; resourcePropertyDefs != null && k < resourcePropertyDefs.length; k ++){
						String key = resourcePropertyDefs[k].getId();
						String value = (String) childMap.get(key);
						ModuleProp mp = new ModuleProp();
						mp.setKey(key);
						mp.setValues(new String[]{value});
						mpList.add(mp);
					}
					//手动添加uuid
					ModuleProp uuid = new ModuleProp();
					uuid.setKey(RESOURCETREE_NODE_UUID);
					uuid.setValues(new String[]{(String) childMap.get(RESOURCETREE_NODE_UUID)});
					mpList.add(uuid);
					
					StringBuffer instantiation = new StringBuffer(500);
					String[] instanceIds = childRDef.getInstantiationDef().getInstanceId();
					for(int l = 0; instanceIds != null && l < instanceIds.length; l++){
						instantiation.append((String)childMap.get(RESOURCETREE_NODE_SUBINDEX));
					}
					
					if(instantiation.length() > 0){
						ModuleProp mp = new ModuleProp();
						mp.setKey(SERVER_INST_IDENTY_KEY);
						mp.setValues(new String[]{SecureUtil.md5Encryp(instantiation.toString())});
						mpList.add(mp);
						
						
						
						childRi.setModuleProps(mpList);
						riList.add(childRi);
					}
				}
			}
		}
		return riList;
	}
	/**
	 * 创建发现属性
	 * @param dcs
	 * @param domainId
	 * @param IP
	 * @param username
	 * @param password
	 * @return
	 */
	private List<DiscoverProp> createDiscoverProps(VmDiscoveryParaBo dParaBo){
		List<DiscoverProp> disPropList = new ArrayList<DiscoverProp>();
		DiscoverProp disProp_DCS = new DiscoverProp();
		disProp_DCS.setKey("DCS"); disProp_DCS.setValues(new String[]{String.valueOf(dParaBo.getDCS())});
		disPropList.add(disProp_DCS);
		DiscoverProp disProp_DomainId = new DiscoverProp();
		disProp_DomainId.setKey("domainId"); disProp_DomainId.setValues(new String[]{String.valueOf(dParaBo.getDomainId())});
		disPropList.add(disProp_DomainId);
		DiscoverProp disProp_IP = new DiscoverProp();
		disProp_IP.setKey("IP"); disProp_IP.setValues(new String[]{dParaBo.getIP()});
		disPropList.add(disProp_IP);
		DiscoverProp disProp_UserName = new DiscoverProp();
		disProp_UserName.setKey("username"); disProp_UserName.setValues(new String[]{dParaBo.getUserName()});
		disPropList.add(disProp_UserName);
		DiscoverProp disProp_Password = new DiscoverProp();
		disProp_Password.setKey("password"); disProp_Password.setValues(new String[]{SecureUtil.pwdEncrypt(dParaBo.getPassword())});
		disPropList.add(disProp_Password);
		DiscoverProp disProp_discoveryType = new DiscoverProp();
		disProp_discoveryType.setKey("discoveryType"); disProp_discoveryType.setValues(new String[]{dParaBo.getDiscoveryType()});
		disPropList.add(disProp_discoveryType);
		DiscoverProp disProp_regeion = new DiscoverProp();
		disProp_regeion.setKey("regeion"); disProp_regeion.setValues(new String[]{dParaBo.getRegeion()});
		disPropList.add(disProp_regeion);
		DiscoverProp disProp_project = new DiscoverProp();
		disProp_project.setKey("project"); disProp_project.setValues(new String[]{dParaBo.getProject()});
		disPropList.add(disProp_project);
		return disPropList;
	}
	
	/**
	 * 添加相应的搜索信息
	 * @param instanceId
	 */
	private void saveSearchRel(long instanceId){
		ResourceBizRel rbr = new ResourceBizRel(instanceId, instanceId, SEARCH_NAV);
		searchApi.saveSearchResource(rbr);
	}
	/**
	 * 把Long的时间段转换成时分秒
	 * 
	 * @param time
	 * @return
	 */
	private String calcTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String hms = sdf.format(time - TimeZone.getDefault().getRawOffset());
		return hms;
	}

	/**
	 * 创建发现参数
	 * @param domain
	 * @param dcs
	 * @param discoveryType
	 * @param ip
	 * @param userName
	 * @param password
	 * @return
	 */
	private ResourceInstanceDiscoveryParameter createRiDiscoryPara(VmDiscoveryParaBo dParaBo){
		// 设置发现参数
		ResourceInstanceDiscoveryParameter discoverParameter = new ResourceInstanceDiscoveryParameter();
		if(DISCOVERYTYPE_VCENTER.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("VMWareVCenter");
		}else if(DISCOVERYTYPE_ESXI.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("vmESXi");
		}else if(DISCOVERYTYPE_XENPOOl.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("XenPool");
		}else if(DISCOVERYTYPE_FUSIONCOMPUTESITE.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("FusionComputeSite");
		}else if(DISCOVERYTYPE_FUSIONCOMPUTEONEPOINTTHREE.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("FusionComputeOnePointThreeSite");
		}else if(DISCOVERYTYPE_KVMHOST.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("KvmHost");
		}else if(DISCOVERYTYPE_DTCENTER.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("DTCenterCloudGlobalDashBoard");
		}else if(DISCOVERYTYPE_KYLIN.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("KyLinServer");
		}else if(DISCOVERYTYPE_VCENTERFivePointFive.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("VMWareVCenter5.5");
		}else if(DISCOVERYTYPE_VCENTERSix.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("VMWareVCenter6");
		}else if(DISCOVERYTYPE_VCENTERSixPointFive.equals(dParaBo.getDiscoveryType())){
			discoverParameter.setResourceId("VMWareVCenter6.5");
		}
		
		// nodeGroup ID
		discoverParameter.setNodeGroupId(dParaBo.getDCS());
		// domain ID
		discoverParameter.setDomainId(dParaBo.getDomainId());
		// ip username password
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.put("IP", dParaBo.getIP());
		discoveryInfos.put("username", dParaBo.getUserName());
		discoveryInfos.put("password", SecureUtil.pwdEncrypt(dParaBo.getPassword()));
		discoveryInfos.put("discoveryType", dParaBo.getDiscoveryType());
		discoveryInfos.put("regeion", dParaBo.getRegeion());
		discoveryInfos.put("project", dParaBo.getProject());
		discoverParameter.setDiscoveryInfos(discoveryInfos);
		// 只做发现
		discoverParameter.setOnlyDiscover(true);
		discoverParameter.setDiscoveryWay(null);
		return discoverParameter;
	}
	
	public DiscoverResourceIntanceResult discoveryTest(Map<String, String> param){
		VmDiscoveryParaBo paraBo = map2VmDiscoveryParaBo(param);
		ResourceInstanceDiscoveryParameter disParam = createRiDiscoryPara(paraBo);
		DiscoverResourceIntanceResult dris = discoveryService.discoveryResourceInstance(disParam);
		return dris;
	}
	
	/**
	 * 更新树下面的所有发现参数
	 */
	@Override
	public int updateDiscoverParamterVm(Map paramter, long instanceId){
		int result = 1;
		Map<Long, Long> domainArgMap = new HashMap<Long, Long>();
		String domainidKey = "domainId";
		//避免多余信息插入资源的发现参数
		Map mapForAutoRefreshInfo = new HashMap();
		if(paramter.containsKey("ifAutoRefresh")){
			mapForAutoRefreshInfo.put("ifAutoRefresh", paramter.get("ifAutoRefresh"));
			paramter.remove("ifAutoRefresh");
			if(paramter.containsKey("autoRefreshCycleDay")){
				mapForAutoRefreshInfo.put("autoRefreshCycleDay", paramter.get("autoRefreshCycleDay"));
				paramter.remove("autoRefreshCycleDay");
			}
		}
		
		List<DiscoverProp> DiscoverProps = new ArrayList<DiscoverProp>();
		try {
			DiscoverResourceIntanceResult dris = discoveryTest(paramter);
			if(dris.isSuccess()){
				ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
				List<VmResourceTreePo> vmTreePoList = vmResourceTreeDao.selectAllChildrenByInstanceId(instanceId);
				for(int i = 0; vmTreePoList != null && i < vmTreePoList.size(); i++){
					VmResourceTreePo vmTreePo = vmTreePoList.get(i);
					if(vmTreePo.getInstanceid() != null && vmTreePo.getInstanceid() != 0){
						DiscoverProps.addAll(createDiscoverProp(paramter, vmTreePo.getInstanceid()));
					}
					if(paramter.get(domainidKey) != null && !"".equals(paramter.get(domainidKey)) && vmTreePo.getInstanceid() != null && vmTreePo.getInstanceid() != 0){
						domainArgMap.put(vmTreePo.getInstanceid(), Long.valueOf((String) paramter.get(domainidKey)));
					}
				}
				
				//userName 转换为 username
				if(null!=paramter.get("userName")&&!"".equals(paramter.get("userName"))){
					paramter.put("username", paramter.get("userName"));
					paramter.remove("userName");
				}
				// 判断是否要更新发现参数
				DiscoverProps.addAll(createDiscoverProp(paramter, instanceId));
				if(!DiscoverProps.isEmpty()){
					discoverPropService.updateProps(DiscoverProps);
				}
				// 判断是否要更新域
				if(paramter.get(domainidKey) != null && !"".equals(paramter.get(domainidKey))){
					domainArgMap.put(instanceId, Long.valueOf((String) paramter.get(domainidKey)));
				}
				if(!String.valueOf(ri.getDomainId()).equals(paramter.get(domainidKey)) && paramter.get(domainidKey) != null && !domainArgMap.isEmpty()){
					resourceInstanceService.updateResourceInstanceDomain(domainArgMap);
				}
			}else{
				result = 2;
			}
		} catch (InstancelibException e) {
			result = 0;
			logger.error("vm updateDiscoverParamter", e);
		}
		
		//更新自动刷新参数
		if(result==1){
			updateAutoRefreshInfo(mapForAutoRefreshInfo,instanceId);
		}
		
		return result;
	}
	private void updateAutoRefreshInfo(Map paramter,long instanceId){
		ProfileAutoRefresh par = profileAutoRefreshService.getAutoRefreshProfileByInstance(instanceId);
		if(paramter.containsKey("ifAutoRefresh") && "on".equals(paramter.get("ifAutoRefresh"))){
			if(null==par){
				par = new ProfileAutoRefresh();
				par.setInstanceId(instanceId);
				par.setIsUse(1);
				par.setExecutRepeat(Integer.valueOf((String)paramter.get("autoRefreshCycleDay")));
				profileAutoRefreshService.addAutoRefreshProfile(par);
			}else{
				par.setExecutRepeat(Integer.valueOf((String)paramter.get("autoRefreshCycleDay")));
				par.setIsUse(1);
				profileAutoRefreshService.updateAutoRefreshProfile(par);
			}
		}else{
			if(null!=par){
				par.setIsUse(0);
				profileAutoRefreshService.updateAutoRefreshProfile(par);
			}
		}
	}
	
	@Override
	public int reDiscoveryVm(Map paramter, long instanceId){
		int result = 1;
		DiscoverResourceIntanceResult dris = discoveryTest(paramter);
		Map<String, Object> resultCollection = new HashMap<String, Object>();
		List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
		List<VmResourceTreePo> vmResourceTreePos = new ArrayList<VmResourceTreePo>();
		if(dris.isSuccess()){
			createResourceInstance(dris.getResourceIntance(), map2VmDiscoveryParaBo(paramter), treeList, resultCollection,vmResourceTreePos);
			deleteResource(vmResourceTreePos);
		}else{
			logger.info("rediscover error code:" + dris.getCode());
			result = 0;
		}
		return result;
	}
	
	@Override
	public Map<String, Object> reDiscoveryTreeResult(VmDiscoveryParaBo dParaBo, ILoginUser user) {
		Long autoDiscoveryStartTime = System.currentTimeMillis();
		Map<String, Object> userCache = user.getCache();
		// 是否正在发现
		userCache.put("isAutoDiscovering", true);
		// 结果集
		Map<String, Object> resultCollection = new HashMap<String, Object>();
		// 设置发现参数
		ResourceInstanceDiscoveryParameter discoverParameter = createRiDiscoryPara(dParaBo);
		// 发现
		DiscoverResourceIntanceResult rir = discoveryService.discoveryResourceInstance(discoverParameter);
		// 前台展示树 集合
		List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
		List<VmResourceTreePo> instanceList = new ArrayList<VmResourceTreePo>();
		List<VmResourceTreePo> vmResourceTreePos = new ArrayList<VmResourceTreePo>();
		if(rir.isSuccess()){
			parseResourceTreeHandle(rir.getResourceIntance(),instanceList);
			//查询已有资源比对缺失资源
			long instanceId = dParaBo.getInstanceId();
			List<VmResourceTreePo> lastTimeInstanceList = vmResourceTreeDao.selectAllChildrenByInstanceId(instanceId);
			//删除之前发现,本次发现消失的资源,先删除释放license
			deleteDisappearResource(instanceList,lastTimeInstanceList);
			
			createReDiscoveryResourceInstance(rir.getResourceIntance(), dParaBo, treeList, resultCollection,vmResourceTreePos);
			
			//删除因license原因,不能监控的资源
			deleteResource(vmResourceTreePos);
			resultCollection.put("isSuccess", true);
		}else{
			resultCollection.put("isSuccess", false);
			resultCollection.put("errorCode", rir.getCode());
		}
		
		// 过滤展示树结果
		treeListFilter(treeList);
		resultCollection.put("vmResourceTree", treeList);
		// 发现时间
		resultCollection.put("reDiscoveryTime", calcTime(System.currentTimeMillis() - autoDiscoveryStartTime));
		return resultCollection;
	}
	
	@Override
	public void autoRerfreshVmResourceTest(VmDiscoveryParaBo dParaBo){
		// 设置发现参数
		ResourceInstanceDiscoveryParameter discoverParameter = createRiDiscoryPara(dParaBo);
		// 发现
		DiscoverResourceIntanceResult rir = discoveryService.discoveryResourceInstance(discoverParameter);
		autoRerfreshVmResource(discoverParameter,rir);
	}
	
	private VmDiscoveryParaBo setVmDiscoveryParaBo(ResourceInstanceDiscoveryParameter discoverParameter){
		VmDiscoveryParaBo vdpb = new VmDiscoveryParaBo();
		vdpb.setDCS(discoverParameter.getNodeGroupId());
		vdpb.setDomainId(discoverParameter.getDomainId());
		vdpb.setInstanceId(discoverParameter.getResourceInstanceId());
		String resourceId = discoverParameter.getResourceId();
		String discoveryType = "1"; 
		switch (resourceId) {
		case "VMWareVCenter":
			discoveryType = "1"; 
			break;
		case "vmESXi":
			discoveryType = "2"; 
			break;
		case "XenPool":
			discoveryType = "3"; 
			break;
		case "FusionComputeSite":
			discoveryType = "4"; 
			break;
		case "FusionComputeOnePointThreeSite":
			discoveryType = "5"; 
			break;
		case "KvmHost":
			discoveryType = "6";
			break;
		case "DTCenterCloudGlobalDashBoard":
			discoveryType = "7";
			break;
		case "KyLinServer":
			discoveryType = "8";
			break;
		case "VMWareVCenter5.5":
			discoveryType = "9";
			break;
		case "VMWareVCenter6":
			discoveryType = "10";
			break;
		case "VMWareVCenter6.5":
			discoveryType = "11";
			break;
		}
		vdpb.setDiscoveryType(discoveryType);
		
		Map<String, String> discoveryInfos = discoverParameter.getDiscoveryInfos();
		
		if(null!=discoveryInfos){
			if(discoveryInfos.containsKey("IP")){
				vdpb.setIP(discoveryInfos.get("IP"));
			}
			if(discoveryInfos.containsKey("username")){
				vdpb.setUserName(discoveryInfos.get("username"));
			}
			if(discoveryInfos.containsKey("password")){
				vdpb.setPassword(discoveryInfos.get("password"));
			}
			if(discoveryInfos.containsKey("discoveryType")){
				vdpb.setDiscoveryType(discoveryInfos.get("discoveryType"));
			}
			if(discoveryInfos.containsKey("regeion")){
				vdpb.setRegeion(discoveryInfos.get("regeion"));
			}
			if(discoveryInfos.containsKey("project")){
				vdpb.setProject(discoveryInfos.get("project"));
			}
		}
		return vdpb;
	}
	
	private void parseResourceTreeHandle(ResourceInstance ri,List<VmResourceTreePo> instanceList){
		String[] resourceTrees = ri.getModulePropBykey("resourceTree");
		Map resourceTreeMap = JSONObject.parseObject(resourceTrees[0], HashMap.class);
		//遍历出所有发现出的资源
		parseResourceTree(resourceTreeMap,instanceList);
	}
	
	@Override
	public void autoRerfreshVmResource(ResourceInstanceDiscoveryParameter discoverParameter , DiscoverResourceIntanceResult rir) {
		try{
			logger.info("autoRerfreshVmResource parse is begin : ");
			Long autoDiscoveryStartTime = System.currentTimeMillis();
			
			VmDiscoveryParaBo dParaBo = setVmDiscoveryParaBo(discoverParameter);
			
			// 结果集
			Map<String, Object> resultCollection = new HashMap<String, Object>();
			
			// 前台展示树 集合
			List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
			List<VmResourceTreePo> instanceList = new ArrayList<VmResourceTreePo>();
			List<VmResourceTreePo> vmResourceTreePos = new ArrayList<VmResourceTreePo>();
			if(rir.isSuccess()){
				parseResourceTreeHandle(rir.getResourceIntance(),instanceList);
				//查询已有资源比对缺失资源
				long instanceId = dParaBo.getInstanceId();
				List<VmResourceTreePo> lastTimeInstanceList = vmResourceTreeDao.selectAllChildrenByInstanceId(instanceId);
				//删除之前发现,本次发现消失的资源,先删除释放license
				deleteDisappearResource(instanceList,lastTimeInstanceList);
				
				createReDiscoveryResourceInstance(rir.getResourceIntance(), dParaBo, treeList, resultCollection,vmResourceTreePos);
				
				//删除因license原因,不能监控的资源
				deleteResource(vmResourceTreePos);
				resultCollection.put("isSuccess", true);
			}else{
				resultCollection.put("isSuccess", false);
				resultCollection.put("errorCode", rir.getCode());
				logger.info("autoRerfreshVmResource discovery false");
			}
			
			// 过滤无children的节点
			treeListFilter(treeList);
			//遍历加入监控
			addTreeResourceMonitor(treeList);
			
			resultCollection.put("vmResourceTree", treeList);
			// 发现时间
			resultCollection.put("reDiscoveryTime", calcTime(System.currentTimeMillis() - autoDiscoveryStartTime));
			logger.info("autoRerfreshVmResource parse is over :  by use time "+calcTime(System.currentTimeMillis() - autoDiscoveryStartTime));
//			return resultCollection;
		}catch(Exception e){
			logger.error("autoRerfreshVmResource e : "+e);
			StackTraceElement[] elements = e.getStackTrace();
			for (StackTraceElement stackTraceElement : elements) {
				logger.error(stackTraceElement);
			}
		}
	}
	
	/**
	 *  遍历加入监控
	 * @param treeList
	 */
	public boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   return isNum.matches(); 
	}
	
	private void addTreeResourceMonitor(List<Map<String, Object>> treeList){
		StringBuffer sb = new StringBuffer();
		StringBuffer sbEnableRepeat = new StringBuffer();
		StringBuffer sbEnableNew = new StringBuffer();
		
		for(int i=0;i<treeList.size();i++){
			Map<String, Object> mapObj = treeList.get(i);
			boolean ifAddMonitor = true;
			if(!mapObj.containsKey("id"))  continue;
			if(!mapObj.containsKey("uuidRepeat")) ifAddMonitor = false;
			if(!mapObj.containsKey("ifMonitored")) ifAddMonitor = false;
			
			sb.append(mapObj.get("id").toString()+",");
			
			if(ifAddMonitor){
				String idStr = mapObj.get("id").toString();
				if(!isNumeric(idStr)) continue;
				boolean uuidRepeat = (boolean)mapObj.get("uuidRepeat");	
				boolean ifMonitored = (boolean)mapObj.get("ifMonitored");
					
				Long instanceId = Long.valueOf(idStr);
				try {
					if(uuidRepeat){
						if(ifMonitored){
							profileService.enableMonitor(instanceId);
						}
						sbEnableRepeat.append(instanceId+",");
					}else{
						profileService.enableMonitor(instanceId);
						sbEnableNew.append(instanceId+",");
					}
				} catch (Exception e) {
					logger.error("autoRerfreshVmResource enableMonitor error", e);
				}
			}
			
			if(!mapObj.containsKey("children")) continue;
			List<Map<String,Object>> childrenList = (List<Map<String, Object>>) mapObj.get("children");	
			if(null!=childrenList && childrenList.size()>0){
				addTreeResourceMonitor(childrenList);
			}
		}
		logger.info("addTreeResourceMonitor sbEnableRepeat ids "+sbEnableRepeat.toString());
		logger.info("addTreeResourceMonitor sbEnableNew ids "+sbEnableNew.toString());
		logger.info("addTreeResourceMonitor sb ids="+sb.toString());
	}
	
	private void deleteDisappearResource(List<VmResourceTreePo> thisTimeInstanceList,List<VmResourceTreePo> lastTimeInstanceList){
		List<VmResourceTreePo> ClusterNeedToDel = new ArrayList<VmResourceTreePo>();
		List<VmResourceTreePo> DataStoreNeedToDel = new ArrayList<VmResourceTreePo>();
		List<VmResourceTreePo> HostNeedToDel = new ArrayList<VmResourceTreePo>();
		List<VmResourceTreePo> VMNeedToDel = new ArrayList<VmResourceTreePo>();
		List<VmResourceTreePo> ResourcePoolNeedToCheck = new ArrayList<VmResourceTreePo>();
		
		for(VmResourceTreePo vrtp:lastTimeInstanceList){
			boolean flag = true;
			for(VmResourceTreePo vrtpT:thisTimeInstanceList){
				if(vrtpT.getUuid().equals(vrtp.getUuid())){
					flag = false;
				}
			}
			if(flag){
				switch (vrtp.getVmtype()) {
				case "XenHost":
				case "HostSystem":
				case "FusionComputeHost":
				case "FusionComputeOnePointThreeHost":
				case "KvmHost":
					HostNeedToDel.add(vrtp);
					break;
				case "XenVM":
				case "VirtualMachine":
				case "FusionComputeVM":
				case "FusionComputeOnePointThreeVM":
				case "DTCenterECS":
				case "KyLinVM":
					VMNeedToDel.add(vrtp);
					break;
				case "XenSR":
				case "Datastore":
				case "FusionComputeDataStore":
				case "FusionComputeOnePointThreeDataStore":
					DataStoreNeedToDel.add(vrtp);
					break;
				case "XenPool":
				case "ClusterComputeResource":
				case "FusionComputeCluster":
				case "FusionComputeOnePointThreeCluster":
				case "DTCenterCloudGlobalDashBoard":
				case "KyLinServer":
					ClusterNeedToDel.add(vrtp);
					break;
				case "ResourcePool":
					//所属host,cluster如果消失,在其他资源经过处理后再处理ResourcePool
					ResourcePoolNeedToCheck.add(vrtp);
					break;
				case "Datacenter":
					break;
				default:
					break;
				}
			}
		}
		//先删除虚拟机,避免先删除宿主机的情况下出问题
		if(VMNeedToDel.size()>0){
			for(VmResourceTreePo vrtp:VMNeedToDel){
				try {
					resourceInstanceService.removeResourceInstance(vrtp.getInstanceid());
				} catch (InstancelibException e) {
					logger.error("deleteDisappearResource InstancelibException instanceId : "+vrtp.getInstanceid(), e);
					vmResourceTreeDao.delByUuid(vrtp.getUuid());
					continue;
				}
				vmResourceTreeDao.delByUuid(vrtp.getUuid());
			}
		}
		if(DataStoreNeedToDel.size()>0){
			for(VmResourceTreePo vrtp:DataStoreNeedToDel){
				try {
					resourceInstanceService.removeResourceInstance(vrtp.getInstanceid());
				} catch (InstancelibException e) {
					logger.error("deleteDisappearResource InstancelibException instanceId : "+vrtp.getInstanceid(), e);
					vmResourceTreeDao.delByUuid(vrtp.getUuid());
					continue;
				}
				vmResourceTreeDao.delByUuid(vrtp.getUuid());
			}
		}
		if(HostNeedToDel.size()>0){
			deleteRediscoveryHostResource(HostNeedToDel);
		}
		if(ClusterNeedToDel.size()>0){
			deleteRediscoveryClusterResource(ClusterNeedToDel);
		}
		if(ResourcePoolNeedToCheck.size()>0){
			filterRediscoveryResourcePool(ResourcePoolNeedToCheck);
		}
	}
	private void filterRediscoveryResourcePool(List<VmResourceTreePo> vmResourceTreePos) {
		for(VmResourceTreePo vpt:vmResourceTreePos){
			String hostUuid = vpt.getHostuuid();
			String clusterUuid = vpt.getClusteruuid();
			Long instanceId = new Long(0);
			if(null!=hostUuid&&!"".equals(hostUuid)){
				VmResourceTreePo vstp = vmResourceTreeDao.selectByUuid(hostUuid);
				if(null!=vstp && null!=vstp.getInstanceid()){
					instanceId = vstp.getInstanceid();
				}
			}else if(null!=clusterUuid&&!"".equals(clusterUuid)){
				VmResourceTreePo vstp = vmResourceTreeDao.selectByUuid(clusterUuid);
				if(null!=vstp && null!=vstp.getInstanceid()){
					instanceId = vstp.getInstanceid();
				}
			}
			//所属host,cluster消失
			if(instanceId==0){
				vmResourceTreeDao.delByUuid(vpt.getUuid());
			}
		}
	}
	private void deleteRediscoveryClusterResource(List<VmResourceTreePo> vmResourceTreePos) {
		if(vmResourceTreePos!=null){
			for(VmResourceTreePo po:vmResourceTreePos){
				try {
					//cluster下的资源
					List<VmResourceTreePo> hostPo = vmResourceTreeDao.selectByPuuid(po.getUuid());
					
					if(null==hostPo || hostPo.size()<1){
						resourceInstanceService.removeResourceInstance(po.getInstanceid());
						vmResourceTreeDao.delByUuid(po.getUuid());
					}
					
					//检察datacenter
					List<VmResourceTreePo> dataCenterChildren = vmResourceTreeDao.selectByPuuid(po.getPuuid());
					
					if(null==dataCenterChildren || dataCenterChildren.size()==0){
						vmResourceTreeDao.delByUuid(po.getPuuid());
					}
//					if(dataCenterChildren!=null){
//						int j = 0;
//						List<Long> instanceId = new ArrayList<Long>();
//						for(VmResourceTreePo po2:dataCenterChildren){
//							//有cluster,或宿主机就不能删除
//							if(po2.getVmtype().equals("FusionComputeCluster")||po2.getVmtype().equals("ClusterComputeResource")||po2.getVmtype().equals("XenPool")
//								|| po2.getVmtype().equals("FusionComputeHost")|| po2.getVmtype().equals("HostSystem")|| po2.getVmtype().equals("XenHost")){
//								++j;
//							}else if(po2.getInstanceid()!=null){
//								instanceId.add(po2.getInstanceid());
//							}
//						}
//						if(j<1){
//							vmResourceTreeDao.delByPuuid(po.getPuuid());
//							resourceInstanceService.removeResourceInstances(instanceId);
//						}
//					}
				} catch (Exception e) {
					logger.error("deleteRediscoveryClusterResource InstancelibException instanceId : "+po.getInstanceid(), e);
				}
				
			}
		}
	}
	
	private void deleteRediscoveryHostResource(List<VmResourceTreePo> vmResourceTreePos) {
		if(vmResourceTreePos!=null){
			for(int i=0;i<vmResourceTreePos.size();i++){
				
					//uuid 宿主机ID
					String uuid = vmResourceTreePos.get(i).getUuid();
					//puuid 宿主机对应的clusterId
					String puuid = vmResourceTreePos.get(i).getPuuid();
					
					//宿主机下面的虚拟机
					List<VmResourceTreePo> vpoVm = vmResourceTreeDao.selectByPuuid(uuid);
					//vpo2 该宿主机头上的cluster或datacenter
					VmResourceTreePo vpoParent = vmResourceTreeDao.selectByUuid(puuid);
					
						if(!Util.isEmpty(vpoVm)){
							for(int m=0;m<vpoVm.size();m++){
								try {
									logger.info("deleteRediscoveryHostResource vpoVm:"+vpoVm.get(m).getInstanceid());
									resourceInstanceService.removeResourceInstance(vpoVm.get(m).getInstanceid());
								} catch (Exception e) {
									logger.error("deleteRediscoveryHostResource del vm instanceId :"+vpoVm.get(m).getInstanceid(), e);
								}
							}
						}
					
				
					try {
						//删除parent之前先删除host
						resourceInstanceService.removeResourceInstance(vmResourceTreePos.get(i).getInstanceid());
						vmResourceTreeDao.delByUuid(vmResourceTreePos.get(i).getUuid());
					
					} catch (Exception e) {
						logger.error("deleteRediscoveryHostResource host instanceId :"+vmResourceTreePos.get(i).getInstanceid(), e);
					}
					
					if(!Util.isEmpty(vpoParent)){
						List<VmResourceTreePo> vpPar = vmResourceTreeDao.selectByPuuid(puuid);
						//检查该parent下面是否有资源
						if(vpPar==null||vpPar.size()<1){
							if(null!=vpoParent.getInstanceid() && vpoParent.getInstanceid()>0){
								try {
									//进行cluster的删除
									resourceInstanceService.removeResourceInstance(vpoParent.getInstanceid());
									vmResourceTreeDao.delByUuid(vpoParent.getUuid());
								} catch (Exception e) {
									logger.error("deleteRediscoveryHostResource cluster instanceId :"+vpoParent.getInstanceid(), e);
								}
								
								
								//进行datacenter数据删除
								List<VmResourceTreePo> vpDataCenter = vmResourceTreeDao.selectByPuuid(vpoParent.getDatacenteruuid());
								VmResourceTreePo vpoDataCenter = vmResourceTreeDao.selectByUuid(vpoParent.getDatacenteruuid());
								if(null==vpDataCenter || vpDataCenter.size()<1){
									vmResourceTreeDao.delByUuid(vpoDataCenter.getUuid());
								}
							}else{
								vmResourceTreeDao.delByUuid(vpoParent.getUuid());
							}
						}
					}
				
				
			}
		}
	}
	
	private VmDiscoveryParaBo map2VmDiscoveryParaBo(Map<String, String> param){
		VmDiscoveryParaBo paraBo = new VmDiscoveryParaBo();
		paraBo.setDiscoveryType((String)param.get("discoveryType"));
		paraBo.setDCS(Integer.valueOf(param.get("DCS")));
		paraBo.setDomainId(Long.valueOf(param.get("domainId")));
		paraBo.setIP((String)param.get("IP"));
		paraBo.setUserName(param.get("userName"));
		paraBo.setPassword(param.get("password"));
		paraBo.setRegeion(param.get("regeion"));
		paraBo.setProject(param.get("project"));
		return paraBo;
	}
	
	private List<DiscoverProp> createDiscoverProp(Map paramter, long instanceId){
		List<DiscoverProp> DiscoverProps = new ArrayList<DiscoverProp>();
		Iterator<String> iter = paramter.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			DiscoverProp discoverProp = new DiscoverProp();
			discoverProp.setInstanceId(instanceId);
			discoverProp.setKey(key);
			// 看是否要加密的value值
			String value = (String)paramter.get(key);
			if (SecureUtil.isPassswordKey(key)) {
				value = SecureUtil.pwdEncrypt(value);
			}
			discoverProp.setValues(new String[]{value});
			DiscoverProps.add(discoverProp);
		}
		return DiscoverProps;
	}

	@Override
	public VmDiscoveryListPageBo getDiscoveryList(VmDiscoveryListPageBo pageBo, ILoginUser user) {
		// DCSMap
		Map<String, String> dcsNodeGroupMap = new HashMap<String, String>();
		try {
			NodeTable nodeTable = nodeService.getNodeTable();
			if(nodeTable != null){
				List<NodeGroup> nodeGroups = nodeTable.getGroups();
				for(int i = 0; nodeGroups != null && i < nodeGroups.size(); i ++){
					NodeGroup nodeGroup = nodeGroups.get(i);
					dcsNodeGroupMap.put(String.valueOf(nodeGroup.getId()), nodeGroup.getName());
				}
			}
		} catch (NodeException e1) {
			logger.error("get NodeTable error:", e1);
		}
		List<VmDiscoveryListBo> vmDLBoList = new ArrayList<VmDiscoveryListBo>();
		List<ResourceInstanceBo> riBoList = resourceApi.getResources(getVcenter_Esxi_RQB(user));
		for(int i = 0; riBoList != null && i < riBoList.size(); i++){
			try {
				ResourceInstanceBo riBo = riBoList.get(i);
				ResourceInstance ri = resourceInstanceService.getResourceInstance(riBo.getId());
				VmDiscoveryListBo vmdlBo = new VmDiscoveryListBo();
				vmdlBo.setCategoryId(riBo.getCategoryId());
				vmdlBo.setDomainId(riBo.getDomainId());
				vmdlBo.setInstanceId(riBo.getId());
				vmdlBo.setResourceId(riBo.getResourceId());
				vmdlBo.setTypeName(capacityService.getResourceDefById(riBo.getResourceId()).getName());
				vmdlBo.setDiscoverNode(riBo.getDiscoverNode());
				vmdlBo.setDiscoverNodeName(dcsNodeGroupMap.get(riBo.getDiscoverNode()));
				vmdlBo.setDomainName(null==domainApi.get(riBo.getDomainId())?"":domainApi.get(riBo.getDomainId()).getName());
				String[] userNameArr = ri.getDiscoverPropBykey("username");
				vmdlBo.setUserName(userNameArr != null && userNameArr.length > 0 ? userNameArr[0] : "");
				String[] passwordArr = ri.getDiscoverPropBykey("password");
				vmdlBo.setPassword(passwordArr != null && passwordArr.length > 0 ? SecureUtil.pwdDecrypt(passwordArr[0]) : "");
				String[] regeionArr = ri.getDiscoverPropBykey("regeion");
				vmdlBo.setRegeion(regeionArr != null && regeionArr.length > 0 ? regeionArr[0] : "");
				String[] projectArr = ri.getDiscoverPropBykey("project");
				vmdlBo.setProject(projectArr != null && projectArr.length > 0 ? projectArr[0] : "");
				if("VirtualHost".equals(riBo.getCategoryId())){
					vmdlBo.setIp(riBo.getDiscoverIP());
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_ESXI);
				} else if("VCenter".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_VCENTER);
				} else if("XenPools".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_XENPOOl);
				} else if("FusionComputeSites".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_FUSIONCOMPUTESITE);
				} else if("FusionComputeOnePointThreeSites".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_FUSIONCOMPUTEONEPOINTTHREE);
				} else if("KvmHosts".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_KVMHOST);
				} else if("DTCenterCloudGlobalDashBoards".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_DTCENTER);
				} else if("KyLinServers".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_KYLIN);
				} else if("VCenter5.5".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_VCENTERFivePointFive);
				} else if("VCenter6".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_VCENTERSix);
				} else if("VCenter6.5".equals(riBo.getCategoryId())){
					String[] IP = ri.getDiscoverPropBykey("IP");
					vmdlBo.setIp(IP != null && IP.length > 0 ? IP[0] : "");
					vmdlBo.setDiscoveryType(DISCOVERYTYPE_VCENTERSixPointFive);
				}
				vmDLBoList.add(vmdlBo);
			} catch (InstancelibException e) {
				logger.error("", e);
			}
		}
		pageBo.setTotalRecord(vmDLBoList.size());
		// 分页
		int startIndex = (int) Math.min(pageBo.getStartRow(), vmDLBoList.size());
		int endIndex = (int) Math.min(pageBo.getStartRow() + pageBo.getRowCount(), vmDLBoList.size());
		vmDLBoList = vmDLBoList.subList(startIndex, endIndex);
		//设置自动刷新参数
		setAutoRefreshInfo(vmDLBoList);
		pageBo.setVmDiscoveryList(vmDLBoList);
		return pageBo;
	}
	//设置自动刷新参数
	private void setAutoRefreshInfo(List<VmDiscoveryListBo> vdlbList){
		for(VmDiscoveryListBo vdlb:vdlbList){
			ProfileAutoRefresh par = profileAutoRefreshService.getAutoRefreshProfileByInstance(vdlb.getInstanceId());
			
			if(null!=par && par.getIsUse()==1){
				vdlb.setIfAutoRefresh(true);
				vdlb.setAutoRefreshCycleDay(par.getExecutRepeat());
			}else{
				vdlb.setIfAutoRefresh(false);
			}
		}
	}
	
	private ResourceQueryBo getVcenter_Esxi_RQB(ILoginUser user){
		ResourceQueryBo rqBo = new ResourceQueryBo(user);
		rqBo.setFilter(new Filter() {
			public boolean filter(ResourceInstanceBo riBo) {
				boolean isFilter = false;
				switch (riBo.getCategoryId()) {
				case "VCenter":
				case "VCenter5.5":
				case "VCenter6":
				case "VCenter6.5":
				case "VirtualHost":
				case "VirtualHost5.5":
				case "VirtualHost6":
				case "VirtualHost6.5":
				case "XenPools":
				case "KvmHosts":
				case "FusionComputeSites":
				case "FusionComputeOnePointThreeSites":
				case "DTCenterCloudGlobalDashBoards":
				case "KyLinServers":
					try {
						ResourceInstance ri = resourceInstanceService.getResourceInstance(riBo.getId());
						String[] discoveryTypeArr = ri.getDiscoverPropBykey("discoveryType");
						String discoveryType = discoveryTypeArr != null && discoveryTypeArr.length > 0 && !"".equals(discoveryTypeArr[0]) ? discoveryTypeArr[0] : "-1";
						if((DISCOVERYTYPE_VCENTER.equals(discoveryType) && "VCenter".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_VCENTERFivePointFive.equals(discoveryType) && "VCenter5.5".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_VCENTERSix.equals(discoveryType) && "VCenter6".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_VCENTERSixPointFive.equals(discoveryType) && "VCenter6.5".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_ESXI.equals(discoveryType) && "VirtualHost".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_XENPOOl.equals(discoveryType) && "XenPools".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_KVMHOST.equals(discoveryType) && "KvmHosts".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_DTCENTER.equals(discoveryType) && "DTCenterCloudGlobalDashBoards".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_KYLIN.equals(discoveryType) && "KyLinServers".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_FUSIONCOMPUTESITE.equals(discoveryType) && "FusionComputeSites".equals(riBo.getCategoryId()))
							|| (DISCOVERYTYPE_FUSIONCOMPUTEONEPOINTTHREE.equals(discoveryType) && "FusionComputeOnePointThreeSites".equals(riBo.getCategoryId()))){
							isFilter = true;
						}
					} catch (InstancelibException e) {
						logger.error("", e);
						isFilter = false;
					}
					break;
				default:
					isFilter = false;
					break;
				}
				return isFilter;
			}
		});
		return rqBo;
	}

	@Override
	public int testDiscoverVm(Map paramter, long instanceId) {
		int result = 0;
		DiscoverResourceIntanceResult drir = discoveryTest(paramter);
		if(drir.isSuccess()){
			result = 1;
		}else{
			result = drir.getCode();
		}
		return result;
	}
}
