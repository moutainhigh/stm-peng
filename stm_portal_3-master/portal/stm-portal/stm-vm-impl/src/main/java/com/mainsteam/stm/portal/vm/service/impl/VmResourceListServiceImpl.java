package com.mainsteam.stm.portal.vm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;
import com.mainsteam.stm.portal.resource.dao.IResVmResourceTreeDao;
import com.mainsteam.stm.portal.vm.api.VmResourceListService;
import com.mainsteam.stm.portal.vm.bo.VmResourceBo;
import com.mainsteam.stm.portal.vm.bo.VmResourcePageBo;
import com.mainsteam.stm.portal.vm.dao.IVmResourceTreeDao;
import com.mainsteam.stm.portal.vm.po.VmResourceTreePo;
import com.mainsteam.stm.portal.vm.util.BeanSortUtil;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.UnitTransformUtil;
import com.mainsteam.stm.util.Util;

public class VmResourceListServiceImpl implements VmResourceListService {
	
	private static Logger logger = Logger.getLogger(VmResourceListServiceImpl.class);
	
	@Resource(name = "stm_system_resourceApi")
	private IResourceApi resourceApi;

	@Resource
	private InstanceStateService instanceStateService;
	
	@Resource
	private ResourceCategoryApi resourceCategoryApi;

	@Resource
	private MetricStateService metricStateService;
	
	@Resource
	private MetricDataService metricDataService;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ProfileService profileService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private ModulePropService modulePropService;
	
	@Resource
	private DiscoverPropService discoverPropService;

	@Resource
	private IVmResourceTreeDao vmResourceTreeDao;
	
	@Resource
	private CustomPropService customPropService;
	
	@Resource
	private IUserApi userApi;

	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptApi;
	@Resource
	private IResVmResourceTreeDao resVmResourceTreeDao;
	
	private int getVMCountByType(String type,List<ResourceInstanceBo> risbListUnFilter){
		VmResourceBo vrb = new VmResourceBo();
		vrb.setCategoryId(type);
		List<VmResourceTreePo> vtpList = vmResourceTreeDao.getVMTreeListAll(vrb);
		List<VmResourceTreePo> resultList = new ArrayList<VmResourceTreePo>();
		for(VmResourceTreePo vpt:vtpList){
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
			//资源池所在的宿主机或集群,在用户权限内,才允许用户查看资源池
			boolean limitFlag = false;
			if(instanceId>0){
				for(ResourceInstanceBo rib:risbListUnFilter){
					if(instanceId.longValue()==rib.getId().longValue()){
						limitFlag = true;
						break;
					}
				}
			}
			if(limitFlag){
				resultList.add(vpt);
			}
		}
		return resultList.size();
	}
	
	//针对fusion显示两个版本资源
	private String[] switchQueryCategoryIds(String categoryid){
		String[] result ;//OnePointThree
		switch (categoryid) {
		case "FusionComputeClusters":
			result = new String[]{categoryid,"FusionComputeOnePointThreeClusters"};
			break;
		case "FusionComputeHosts":
			result = new String[]{categoryid,"FusionComputeOnePointThreeHosts"};
			break;
		case "FusionComputeVMs":
			result = new String[]{categoryid,"FusionComputeOnePointThreeVMs"};
			break;
		case "FusionComputeDataStores":
			result = new String[]{categoryid,"FusionComputeOnePointThreeDataStores"};
			break;
		case "VirtualCluster":
			result = new String[]{categoryid,"VirtualCluster5.5","VirtualCluster6","VirtualCluster6.5"};
			break;
		case "VirtualHost":
			result = new String[]{categoryid,"VirtualHost5.5","VirtualHost6","VirtualHost6.5"};
			break;
		case "VirtualStorage":
			result = new String[]{categoryid,"VirtualStorage5.5","VirtualStorage6","VirtualStorage6.5"};
			break;
		case "VirtualVM":
			result = new String[]{categoryid,"VirtualVM5.5","VirtualVM6","VirtualVM6.5"};
			break;
		default:
			result = new String[]{categoryid};
			break;
		}
		return result;
	}
	
	@Override
	public VmResourcePageBo getMonitorList(VmResourcePageBo pageBo, ILoginUser user) {
		List<ResourceInstanceBo> risbListUnFilter = resourceApi.getResources(user);
		List<String> requiredCategory = new ArrayList<String>();
		requiredCategory.add("VM");
		
		List<ResourceCategoryBo> rcbList = resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, requiredCategory);
		for(ResourceCategoryBo rcb:rcbList){
			if(rcb.getId().equals("VM")){
				List<ResourceCategoryBo> rcbChildrenList = rcb.getChildCategorys();
				int OPTcluster =0,OPThost=0,OPTdatastore=0,OPTvms=0;
				int VCcluster=0,VChost=0,VCstorage=0,VCvm=0;
				for(ResourceCategoryBo rcbChild:rcbChildrenList){
					if(rcbChild.getId().equals("FusionComputeOnePointThree")){
						List<ResourceCategoryBo> optChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo optChild:optChildrenList){
							switch(optChild.getId()){
							case "FusionComputeOnePointThreeClusters":
								OPTcluster = optChild.getResourceNumber();
								break;
							case "FusionComputeOnePointThreeHosts":
								OPThost = optChild.getResourceNumber();
								break;
							case "FusionComputeOnePointThreeDataStores":
								OPTdatastore = optChild.getResourceNumber();
								break;
							case "FusionComputeOnePointThreeVMs":
								OPTvms = optChild.getResourceNumber();
								break;
							}
						}
					}
					if("VMware5.5".equals(rcbChild.getId()) 
						||	"VMware6".equals(rcbChild.getId())
						||	"VMware6.5".equals(rcbChild.getId())){
						List<ResourceCategoryBo> optChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo optChild:optChildrenList){
							switch(optChild.getId()){
							case "VirtualCluster5.5":
							case "VirtualCluster6":
							case "VirtualCluster6.5":
								VCcluster = optChild.getResourceNumber();
								break;
							case "VirtualHost5.5":
							case "VirtualHost6":
							case "VirtualHost6.5":
								VChost = optChild.getResourceNumber();
								break;
							case "VirtualStorage5.5":
							case "VirtualStorage6":
							case "VirtualStorage6.5":
								VCstorage = optChild.getResourceNumber();
								break;
							case "VirtualVM5.5":
							case "VirtualVM6":
							case "VirtualVM6.5":
								VCvm = optChild.getResourceNumber();
								break;
							}
						}
					}
				}
				
				for(ResourceCategoryBo rcbChild:rcbChildrenList){
					if(rcbChild.getId().equals("VMware")){
						ResourceCategoryBo pool = new ResourceCategoryBo();
						pool.setId("ResourcePool");
						pool.setName("资源池");
						pool.setResourceNumber(getVMCountByType("ResourcePool",risbListUnFilter));
						
						if(null!=rcbChild.getChildCategorys()){
							rcbChild.getChildCategorys().add(pool);
						}else{
							List<ResourceCategoryBo> rcbListTemp = new ArrayList<ResourceCategoryBo>();
							rcbListTemp.add(pool);
							rcbChild.setChildCategorys(rcbListTemp);
						}
						
						List<ResourceCategoryBo> fcChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo fcChild:fcChildrenList){
							switch(fcChild.getId()){
							case "VirtualCluster":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VCcluster);
								break;
							case "VirtualHost":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VChost);
								break;
							case "VirtualStorage":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VCstorage);
								break;
							case "VirtualVM":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VCvm);
								break;
							}
						}
					}else if(rcbChild.getId().equals("FusionCompute")){
						//将FusionComputeOnePointThree数量合并到FusionCompute
						List<ResourceCategoryBo> fcChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo fcChild:fcChildrenList){
							switch(fcChild.getId()){
							case "FusionComputeClusters":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPTcluster);
								break;
							case "FusionComputeHosts":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPThost);
								break;
							case "FusionComputeDataStores":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPTdatastore);
								break;
							case "FusionComputeVMs":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPTvms);
								break;
							}
						}
					}
				}
			}
		}
		pageBo.setResourceCategoryBos(rcbList);
		
		VmResourceBo condition = pageBo.getCondition();
		
		List<Long> filteredInstanceIds = new ArrayList<Long>();
		
		List<VmResourceBo> risbListFiltered = new ArrayList<VmResourceBo>();
		String[] categoryIds = switchQueryCategoryIds(condition.getCategoryId());
		for(int i = 0; i < risbListUnFilter.size(); i ++){
			ResourceInstanceBo riBo = risbListUnFilter.get(i);
			if(matchFilterCondition(riBo, condition.getiPorName(), condition.getDomainId(), categoryIds, InstanceLifeStateEnum.MONITORED)){
				risbListFiltered.add(riBo2VmBo(riBo));
				filteredInstanceIds.add(riBo.getId());
			}
		}
		if(!risbListFiltered.isEmpty()){
			// 查询资源状态
			List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(filteredInstanceIds);
			// 组装资源状态
			Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap<Long, InstanceStateEnum>();
			for(int i = 0; instanceStateDataList != null && i < instanceStateDataList.size(); i++){
				InstanceStateData isd = instanceStateDataList.get(i);
				instanceStateDataMap.put(isd.getInstanceID(), isd.getState());
			}
			// 过滤资源状态
			List<VmResourceBo> tmpMonitorList = new ArrayList<VmResourceBo>();
			for(int i = 0; i < risbListFiltered.size(); i++){
				VmResourceBo  vmBo = risbListFiltered.get(i);
				InstanceStateEnum instanceState = null;
				if(instanceStateDataMap.containsKey(vmBo.getId())){
					instanceState = instanceStateDataMap.get(vmBo.getId());
				}
				instanceState = instanceState == null ? InstanceStateEnum.UNKNOWN_NOTHING : instanceState;
				if(null != condition.getResourceStatus() && !"all".equals(condition.getResourceStatus())){
					if(!matchResourceAvaAla(condition.getResourceStatus(), instanceState)){
						continue;
					}
				}
				vmBo.setResourceStatus(instanceStateToColor(instanceState));
				tmpMonitorList.add(vmBo);
			}
			risbListFiltered = tmpMonitorList;
		}
		pageBo.setTotalRecord(risbListFiltered.size());

		//  要排序 分页前先查询指标信息
		if(!Util.isEmpty(pageBo.getSort()) && !Util.isEmpty(pageBo.getOrder()) && !risbListFiltered.isEmpty()){
			queryResourceMetricInfo(risbListFiltered, InstanceLifeStateEnum.MONITORED, condition.getCategoryId());
		}
		
		// 排序
		if(!Util.isEmpty(pageBo.getSort()) && !Util.isEmpty(pageBo.getOrder())){
			risbListFiltered = new BeanSortUtil<VmResourceBo>(risbListFiltered, pageBo.getSort(), pageBo.getOrder()).sort();
		}
		
		// 分页
		int startIndex = (int) Math.min(pageBo.getStartRow(), risbListFiltered.size());
		int endIndex = (int) Math.min(pageBo.getStartRow() + pageBo.getRowCount(), risbListFiltered.size());
		risbListFiltered = risbListFiltered.subList(startIndex, endIndex);
		
		// 不排序 分页后查询指标信息
		if((Util.isEmpty(pageBo.getSort()) || Util.isEmpty(pageBo.getOrder())) && !risbListFiltered.isEmpty()){
			queryResourceMetricInfo(risbListFiltered, InstanceLifeStateEnum.MONITORED, condition.getCategoryId());
		}
		
		pageBo.setVmResources(risbListFiltered);
		return pageBo;
	}

	@Override
	public VmResourcePageBo getUnMonitorList(VmResourcePageBo pageBo, ILoginUser user) {
		List<ResourceInstanceBo> risbListUnFilter = resourceApi.getResources(user);
		List<String> requiredCategory = new ArrayList<String>();
		requiredCategory.add("VM");
//		pageBo.setResourceCategoryBos(resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, requiredCategory));
		List<ResourceCategoryBo> rcbList = resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, requiredCategory);
		for(ResourceCategoryBo rcb:rcbList){
			if(rcb.getId().equals("VM")){
				List<ResourceCategoryBo> rcbChildrenList = rcb.getChildCategorys();
				int OPTcluster =0,OPThost=0,OPTdatastore=0,OPTvms=0;
				int VCcluster=0,VChost=0,VCstorage=0,VCvm=0;
				for(ResourceCategoryBo rcbChild:rcbChildrenList){
					if(rcbChild.getId().equals("FusionComputeOnePointThree")){
						List<ResourceCategoryBo> optChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo optChild:optChildrenList){
							switch(optChild.getId()){
							case "FusionComputeOnePointThreeClusters":
								OPTcluster = optChild.getResourceNumber();
								break;
							case "FusionComputeOnePointThreeHosts":
								OPThost = optChild.getResourceNumber();
								break;
							case "FusionComputeOnePointThreeDataStores":
								OPTdatastore = optChild.getResourceNumber();
								break;
							case "FusionComputeOnePointThreeVMs":
								OPTvms = optChild.getResourceNumber();
								break;
							}
						}
					}
					if("VMware5.5".equals(rcbChild.getId()) 
							||	"VMware6".equals(rcbChild.getId())
							||	"VMware6.5".equals(rcbChild.getId())){
							List<ResourceCategoryBo> optChildrenList = rcbChild.getChildCategorys();
							for(ResourceCategoryBo optChild:optChildrenList){
								switch(optChild.getId()){
								case "VirtualCluster5.5":
								case "VirtualCluster6":
								case "VirtualCluster6.5":
									VCcluster = optChild.getResourceNumber();
									break;
								case "VirtualHost5.5":
								case "VirtualHost6":
								case "VirtualHost6.5":
									VChost = optChild.getResourceNumber();
									break;
								case "VirtualStorage5.5":
								case "VirtualStorage6":
								case "VirtualStorage6.5":
									VCstorage = optChild.getResourceNumber();
									break;
								case "VirtualVM5.5":
								case "VirtualVM6":
								case "VirtualVM6.5":
									VCvm = optChild.getResourceNumber();
									break;
								}
							}
						}
				}
				
				for(ResourceCategoryBo rcbChild:rcbChildrenList){
					if(rcbChild.getId().equals("VMware")){
						ResourceCategoryBo pool = new ResourceCategoryBo();
						pool.setId("ResourcePool");
						pool.setName("资源池");
						pool.setResourceNumber(getVMCountByType("ResourcePool",risbListUnFilter));
						
						if(null!=rcbChild.getChildCategorys()){
							rcbChild.getChildCategorys().add(pool);
						}else{
							List<ResourceCategoryBo> rcbListTemp = new ArrayList<ResourceCategoryBo>();
							rcbListTemp.add(pool);
							rcbChild.setChildCategorys(rcbListTemp);
						}
						
						List<ResourceCategoryBo> fcChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo fcChild:fcChildrenList){
							switch(fcChild.getId()){
							case "VirtualCluster":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VCcluster);
								break;
							case "VirtualHost":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VChost);
								break;
							case "VirtualStorage":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VCstorage);
								break;
							case "VirtualVM":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+VCvm);
								break;
							}
						}
					}else if(rcbChild.getId().equals("FusionCompute")){
						//将FusionComputeOnePointThree数量合并到FusionCompute
						List<ResourceCategoryBo> fcChildrenList = rcbChild.getChildCategorys();
						for(ResourceCategoryBo fcChild:fcChildrenList){
							switch(fcChild.getId()){
							case "FusionComputeClusters":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPTcluster);
								break;
							case "FusionComputeHosts":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPThost);
								break;
							case "FusionComputeDataStores":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPTdatastore);
								break;
							case "FusionComputeVMs":
								fcChild.setResourceNumber(fcChild.getResourceNumber()+OPTvms);
								break;
							}
						}
					}
				}
			}
		}
		pageBo.setResourceCategoryBos(rcbList);
		
		VmResourceBo condition = pageBo.getCondition();
		List<VmResourceBo> risbListFiltered = new ArrayList<VmResourceBo>();
		String[] categoryIds = switchQueryCategoryIds(condition.getCategoryId());
		for(int i = 0; i < risbListUnFilter.size(); i ++){
			ResourceInstanceBo riBo = risbListUnFilter.get(i);
			if(matchFilterCondition(riBo, condition.getiPorName(), condition.getDomainId(), categoryIds, InstanceLifeStateEnum.NOT_MONITORED)){
				VmResourceBo vmBo = riBo2VmBo(riBo);
				risbListFiltered.add(vmBo);
			}
		}
		
		pageBo.setTotalRecord(risbListFiltered.size());
		// 分页
		int startIndex = (int) Math.min(pageBo.getStartRow(), risbListFiltered.size());
		int endIndex = (int) Math.min(pageBo.getStartRow() + pageBo.getRowCount(), risbListFiltered.size());
		risbListFiltered = risbListFiltered.subList(startIndex, endIndex);
		//  查询指标信息
		if(!risbListFiltered.isEmpty()){
			queryResourceMetricInfo(risbListFiltered, InstanceLifeStateEnum.NOT_MONITORED, condition.getCategoryId());
		}
		
		pageBo.setVmResources(risbListFiltered);
		return pageBo;
	}
	
	private boolean matchFilterCondition(ResourceInstanceBo riBo, String ipOrShowName, Long domainId, String[] categoryId, InstanceLifeStateEnum lifeStateEnum){
		if(null != domainId){
			if(domainId.longValue() != riBo.getDomainId().longValue()){
				return false;
			}
		}
		boolean flag = false;
		for(String cate:categoryId){
			if(null!=riBo.getCategoryId() && riBo.getCategoryId().equals(cate)){
				flag = true;
				break;
			}
		}
		if(!flag){
			return flag;
		}
		
		if(ipOrShowName != null && !"".equals(ipOrShowName)){
			String showName = riBo.getShowName() == null ? "" : riBo.getShowName();
			String discoverIp = riBo.getDiscoverIP() == null ? "" : riBo.getDiscoverIP();
			//不区分大小写
			showName = showName.toLowerCase();
			String ipOrShowNameTemp = ipOrShowName.toLowerCase();
			if(!showName.contains(ipOrShowNameTemp) && !discoverIp.contains(ipOrShowNameTemp)){
				return false;
			}
		}
		if(lifeStateEnum != riBo.getLifeState()){
			return false;
		}
		return true;
	}
	/**
	 * ribo2VmBo
	 * @param riBo
	 * @return
	 */
	private VmResourceBo riBo2VmBo(ResourceInstanceBo riBo){
		VmResourceBo vmBo = new VmResourceBo();
		vmBo.setId(riBo.getId());
		vmBo.setResourceId(riBo.getResourceId());
		vmBo.setCategoryId(riBo.getCategoryId());
		vmBo.setSourceName(riBo.getShowName());
		vmBo.setIp(riBo.getDiscoverIP());
		
		// 根据资源id获取责任人
		CustomProp prop=null;
		try {
			prop = customPropService.getPropByInstanceAndKey(riBo.getId(), "liablePerson");
		} catch (InstancelibException e) {			
			
		}
		User user = new User();
		if (prop != null) {
			String[] accountIds =prop.getValues();
			if (accountIds.length > 0) {
				user=userApi.get(Long.parseLong(accountIds[0]));	
				vmBo.setLiablePerson(user.getName());
			}
		}
		return vmBo;
	}
	
	/**
	 * 指标状态转换成相应的颜色
	 * @param stateEnum
	 * @return
	 */
	private static String metricStateToColor(MetricStateEnum stateEnum) {
		String ise = "gray";
		if(stateEnum != null){
			switch (stateEnum) {
			case CRITICAL:
				ise = "red";
				break;
			case SERIOUS:
				ise = "orange";
				break;
			case WARN:
				ise = "yellow";
				break;
			case NORMAL:
			case NORMAL_NOTHING:
				ise = "green";
				break;
			default:
				ise = "gray";
				break;
			}
		}
		return ise;
	}
	
	/**
	 * 资源实例状态转换成相应的颜色
	 * @param stateEnum
	 * @return
	 */
	private static String instanceStateToColor(InstanceStateEnum stateEnum) {
		String ise = null;
		switch (stateEnum) {
		case CRITICAL:
			ise = "res_critical";
			break;
		case CRITICAL_NOTHING:
			ise = "res_critical_nothing";
			break;
		case SERIOUS:
			ise = "res_serious";
			break;
		case WARN:
			ise = "res_warn";
			break;
		case NORMAL:
		case NORMAL_NOTHING:
			ise = "res_normal_nothing";
			break;
		case NORMAL_CRITICAL:
			ise = "res_normal_critical";
			break;
//		case NORMAL_UNKNOWN:
//			ise = "res_normal_unknown";
//			break;
//		case UNKNOWN_NOTHING:
//			ise = "res_unknown_nothing";
//			break;
//		case UNKOWN:
//			ise = "res_unkown";
//			break;
		default :
			ise = "res_normal_nothing";
				break;
		}
		return ise;
	}
	private static InstanceStateEnum getInstStateEnum(String instStateEnumString) {
		InstanceStateEnum ise = null;
		if (null == instStateEnumString) {
			return ise;
		} else {
			switch (instStateEnumString) {
			case "all":
				break;
			case "critical":
				ise = InstanceStateEnum.CRITICAL;
				break;
			case "serious":
				ise = InstanceStateEnum.SERIOUS;
				break;
			case "warn":
				ise = InstanceStateEnum.WARN;
				break;
			case "normal":
				ise = InstanceStateEnum.NORMAL;
				break;
			case "unknown":
				ise = InstanceStateEnum.UNKOWN;
				break;
			}
		}

		return ise;
	}

	private boolean matchResourceAvaAla(String instanceStatus, InstanceStateEnum stateEnum){
		boolean flag = false;
		switch (instanceStatus) {
			case "available":{
				switch (stateEnum) {
				case NORMAL:
				case NORMAL_NOTHING:
				case NORMAL_CRITICAL:
				case NORMAL_UNKNOWN:
				case SERIOUS:
				case WARN:
					flag = true;
					break;
				}
				break;
			}
			case "notavailable":{
				switch (stateEnum) {
				case CRITICAL:
				case CRITICAL_NOTHING:
					flag = true;
					break;
				}
				break;
			}
			case "resunknown":{
				switch (stateEnum) {
				case UNKNOWN_NOTHING:
					flag = true;
					break;
				}
				break;
			}
			case "critical":{
				switch (stateEnum) {
				case CRITICAL:
				case NORMAL_CRITICAL:
					flag = true;
					break;
				}
				break;
			}
			case "serious":{
				switch (stateEnum) {
				case SERIOUS:
					flag = true;
					break;
				}
				break;
			}
			case "warn":{
				switch (stateEnum) {
				case WARN:
					flag = true;
					break;
				}
				break;
			}
			case "normal":{
				switch (stateEnum) {
				case NORMAL:
				case NORMAL_NOTHING:
				case UNKNOWN_NOTHING:
					flag = true;
					break;
				}
				break;
			}
			case "unknown":{
				switch (stateEnum) {
				case UNKOWN:
				case NORMAL_UNKNOWN:
					flag = true;
					break;
				}
				break;
			}
		}
		return flag;
	}
	/**
	 * 查询指标信息
	 * @param vmBos
	 * @param lifeSate
	 */
	private void queryResourceMetricInfo(List<VmResourceBo> vmBos, InstanceLifeStateEnum lifeSate, String categoryId){
		List<Long> instanceIdList = new ArrayList<Long>();
		long[] instanceIdArray = new long[vmBos.size()];
		
		// 指标查询条件
		for(int i = 0; i < vmBos.size(); i++){
			VmResourceBo vmBo = vmBos.get(i);
			instanceIdList.add(vmBo.getId());
			instanceIdArray[i] = vmBo.getId();
		}

		// 查询vcenter cluster datacenter host 等关系
		Map<Long, VmResourceTreePo> vmTreePoMap = new HashMap<Long, VmResourceTreePo>();
		List<VmResourceTreePo> vmTreePoList = vmResourceTreeDao.selectRelationIdsByInstanceId(instanceIdList);
		for(int i = 0; vmTreePoList != null && i < vmTreePoList.size(); i++){
			vmTreePoMap.put(vmTreePoList.get(i).getInstanceid(), vmTreePoList.get(i));
		}
		for(int i = 0; i < vmBos.size(); i++){
			VmResourceBo vmBo = vmBos.get(i);
			if(vmTreePoMap.containsKey(vmBo.getId())){
				try{
					VmResourceTreePo vmTreePo = vmTreePoMap.get(vmBo.getId());
					vmBo.setDataCenter(vmTreePo.getDatacentername());
					if(vmTreePo.getVcenterinstanceid() != null){
						ResourceInstance ri = resourceInstanceService.getResourceInstance(vmTreePo.getVcenterinstanceid());
						vmBo.setvCenter(ri != null ? ri.getShowName() : null);
					}
					if(vmTreePo.getClusterinstanceid() != null){
						ResourceInstance ri = resourceInstanceService.getResourceInstance(vmTreePo.getClusterinstanceid());
						vmBo.setCluster(ri != null ? ri.getShowName() : null);
					}
					if(vmTreePo.getHostinstanceid() != null){
						ResourceInstance ri = resourceInstanceService.getResourceInstance(vmTreePo.getHostinstanceid());
						vmBo.setExsi(ri != null ? ri.getShowName() : null);
					}
				}catch(InstancelibException e){
					logger.error("query vm relation", e);
				}
			}
		}

		// 信息指标
		List<String> infoMetric = new ArrayList<String>();
		if("VirtualCluster".equals(categoryId) || "VirtualStorage".equals(categoryId) ||
			"VirtualCluster5.5".equals(categoryId) || "VirtualStorage5.5".equals(categoryId) ||
			"VirtualCluster6".equals(categoryId) || "VirtualStorage6".equals(categoryId) ||
			"VirtualCluster6.5".equals(categoryId) || "VirtualStorage6.5".equals(categoryId)){
			infoMetric.add("HostNum");
			infoMetric.add("TotalMemSize");
			infoMetric.add("DatastoreSize");
			infoMetric.add("CPUNum");
			infoMetric.add("MEMNum");
			infoMetric.add("DataStorageFreeSpace");
			infoMetric.add("DataStorageVolume");
			infoMetric.add("VMNum");
		}
		if("VirtualHost".equals(categoryId) || 
			"VirtualHost5.5".equals(categoryId) ||
			"VirtualHost6".equals(categoryId) ||
			"VirtualHost6.5".equals(categoryId)){
			infoMetric.add("VMNum");
		}
		if("VirtualVM".equals(categoryId) ||
			"VirtualVM5.5".equals(categoryId) ||
			"VirtualVM6".equals(categoryId) ||
			"VirtualVM6.5".equals(categoryId)){
			infoMetric.add("osVersion");
//			infoMetric.add("ip");
		}
		if("XenHosts".equals(categoryId) || "FusionComputeHosts".equals(categoryId)){
			infoMetric.add("address");
			infoMetric.add(MetricIdConsts.SYS_UPTIME);
		}
		if("XenVMs".equals(categoryId)){
			infoMetric.add(MetricIdConsts.SYS_UPTIME);
			infoMetric.add("ip");
		}
		if("FusionComputeVMs".equals(categoryId)){
			infoMetric.add(MetricIdConsts.SYS_UPTIME);
			infoMetric.add("address");
		}
		if("XenSRs".equals(categoryId) || "FusionComputeDataStores".equals(categoryId) ){
			infoMetric.add("address");
			infoMetric.add("physicalSize");
			infoMetric.add("physicalUtilisation");
			infoMetric.add("type");
		}
		if("DTCenterECSs".equals(categoryId)){
			infoMetric.add(MetricIdConsts.METRIC_IP);
			infoMetric.add("regeion");
			infoMetric.add("cpuNum");
			infoMetric.add("memSize");
		}
		if("KyLinVms".equals(categoryId)){
			infoMetric.add(MetricIdConsts.METRIC_IP);
			//infoMetric.add("cpuNum");
			infoMetric.add("memSize");
			infoMetric.add("createTime");
		}
		// 查询信息指标
		Map<Long, List<MetricData>> infoMetricMap = new HashMap<Long, List<MetricData>>();
		List<MetricData> metricDataList = metricDataService.getMetricInfoDatas(instanceIdArray, infoMetric.toArray(new String[infoMetric.size()]));
		for(int i = 0; i < metricDataList.size(); i++){
			MetricData metricData = metricDataList.get(i);
			if(infoMetricMap.containsKey(metricData.getResourceInstanceId())){
				infoMetricMap.get(metricData.getResourceInstanceId()).add(metricData);
			}else{
				List<MetricData> infoMetricList = new ArrayList<MetricData>();
				infoMetricList.add(metricData);
				infoMetricMap.put(metricData.getResourceInstanceId(), infoMetricList);
			}
		}
		
		// 性能指标
		String[] metrics = {MetricIdConsts.METRIC_CPU_RATE, MetricIdConsts.METRIC_MEME_RATE, "DiskUsagePercentage", "CPUPercent", "MEMPercent", "physicalRate"};
		Map<Long, Map<String, MetricStateEnum>> iMStateMap = new HashMap<Long, Map<String, MetricStateEnum>>();
		List<MetricStateData> msdList = metricStateService.findMetricState(instanceIdList, Arrays.asList(metrics));
		for(int i = 0; msdList != null && i < msdList.size(); i++){
			MetricStateData msd = msdList.get(i);
			Long instanceId = msd.getInstanceID();
			Map<String, MetricStateEnum> mStateMap = null;
			if(iMStateMap.containsKey(instanceId)){
				mStateMap = iMStateMap.get(instanceId);
			}else{
				mStateMap = new HashMap<String, MetricStateEnum>();
				iMStateMap.put(instanceId, mStateMap);
			}
			mStateMap.put(msd.getMetricID(), msd.getState());
		}
		// 设置指标查询条件
		MetricRealtimeDataQuery mrdq = new MetricRealtimeDataQuery();
		mrdq.setInstanceID(instanceIdArray);
		mrdq.setMetricID(metrics);
		Page<Map<String, ?>, MetricRealtimeDataQuery> page = metricDataService.queryRealTimeMetricDatas(mrdq, 1, 10000);
		List<Map<String, ?>> metricMapList = page.getDatas();
		// 组装查询回的指标数据
		Map<String, Map<String, ?>> reLoadMetricMap = new HashMap<String, Map<String, ?>>();
		for(int i = 0; i < metricMapList.size(); i++){
			Map<String, ?> metricMap = metricMapList.get(i);
			if(metricMap.get("instanceid") != null){
				String instanceId = metricMap.get("instanceid").toString();
				reLoadMetricMap.put(instanceId, metricMap);
			}
		}
		// 循环是否有这个资源实例的指标
		for(int i = 0; i < vmBos.size(); i++){
			VmResourceBo vmBo = vmBos.get(i);
			Long instanceId = vmBo.getId();
			// 如果有查询回的指标数据
			if(reLoadMetricMap.containsKey(instanceId.toString())){
				boolean isMonitor_CPU = false, isMonitor_MEM = false, isMonitor_DISKUSAGE = false, isMonitor_cpuPercent = false, isMonitor_memPercent = false, isMonitor_physicalRate = false;
				// 判断cpu内存是否已监控
				try {
//					ProfileMetric cpuPMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, MetricIdConsts.METRIC_CPU_RATE);
					ProfileMetric cpuPMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, MetricIdConsts.METRIC_CPU_RATE);
					if(cpuPMetric != null && cpuPMetric.isMonitor()){
						isMonitor_CPU = true;
						vmBo.setCpuRateIsAlarm(cpuPMetric.isAlarm());
					}
//					ProfileMetric memPMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, MetricIdConsts.METRIC_MEME_RATE);
					ProfileMetric memPMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, MetricIdConsts.METRIC_MEME_RATE);
					if(memPMetric != null && memPMetric.isMonitor()){
						isMonitor_MEM = true;
						vmBo.setMemRateIsAlarm(memPMetric.isAlarm());
					}
					// 只有存储才用查询
					if("VMWareDatastore".equals(vmBo.getResourceId()) ||
						"VMWareDatastore5.5".equals(vmBo.getResourceId()) ||
						"VMWareDatastore6".equals(vmBo.getResourceId()) ||
						"VMWareDatastore6.5".equals(vmBo.getResourceId())){
//						ProfileMetric diskUsage = profileService.getMetricByInstanceIdAndMetricId(instanceId, "DiskUsagePercentage");
						ProfileMetric diskUsage = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, "DiskUsagePercentage");
						if(diskUsage != null && diskUsage.isMonitor()){
							isMonitor_DISKUSAGE = true;
							vmBo.setDatastoreRateIsAlarm(diskUsage.isAlarm());
						}
					}
					// 只有cluster才用查询
					if("VMWareCluster".equals(vmBo.getResourceId()) ||
						"VMWareCluster5.5".equals(vmBo.getResourceId()) ||
						"VMWareCluster6".equals(vmBo.getResourceId()) ||
						"VMWareCluster6.5".equals(vmBo.getResourceId())){
//						ProfileMetric cpuPercentMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, "CPUPercent");
						ProfileMetric cpuPercentMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, "CPUPercent");
						if(cpuPercentMetric != null && cpuPercentMetric.isMonitor()){
							isMonitor_cpuPercent = true;
							vmBo.setCpuPercentIsAlarm(cpuPercentMetric.isAlarm());
						}
//						ProfileMetric memPercentMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, "MEMPercent");
						ProfileMetric memPercentMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, "MEMPercent");
						if(memPercentMetric != null && memPercentMetric.isMonitor()){
							isMonitor_memPercent = true;
							vmBo.setMemPercentIsAlarm(memPercentMetric.isAlarm());
						}
					}
					if("XenSR".equals(vmBo.getResourceId())
						|| "KvmDataStore".equals(vmBo.getResourceId())){
//						ProfileMetric physicalRateMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, "physicalRate");
						ProfileMetric physicalRateMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, "physicalRate");
						if(physicalRateMetric != null && physicalRateMetric.isMonitor()){
							isMonitor_physicalRate = true;
							vmBo.setPhysicalRateIsAlarm(physicalRateMetric.isAlarm());
						}
					}
					if("FusionComputeDataStore".equals(vmBo.getResourceId())){
//						ProfileMetric physicalRateMetric = profileService.getMetricByInstanceIdAndMetricId(instanceId, "physicalRate");
						ProfileMetric physicalRateMetric = profileService.getProfileMetricByInstanceIdAndMetricId(instanceId, "physicalRate");
						if(physicalRateMetric != null && physicalRateMetric.isMonitor()){
							isMonitor_physicalRate = true;
							vmBo.setPhysicalRateIsAlarm(physicalRateMetric.isAlarm());
						}
					}
				} catch (ProfilelibException e) {
					logger.error("query vm prefMetric", e);
				}
				Map<String, ?> metricMap = reLoadMetricMap.get(instanceId.toString());
				// CPU指标值
				if(isMonitor_CPU && metricMap.containsKey(MetricIdConsts.METRIC_CPU_RATE)){
					if(null!=metricMap.get(MetricIdConsts.METRIC_CPU_RATE)){
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(vmBo.getResourceId(), MetricIdConsts.METRIC_CPU_RATE);
						vmBo.setCpuRate(metricMap.get(MetricIdConsts.METRIC_CPU_RATE).toString() + (null==rmDef?"":rmDef.getUnit()));
					}else{
						vmBo.setCpuRate("--");
					}
				}
				// 内存指标值
				if(isMonitor_MEM && metricMap.containsKey(MetricIdConsts.METRIC_MEME_RATE)){
					if(null!=metricMap.get(MetricIdConsts.METRIC_MEME_RATE)){
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(vmBo.getResourceId(), MetricIdConsts.METRIC_MEME_RATE);
						vmBo.setMemRate(metricMap.get(MetricIdConsts.METRIC_MEME_RATE).toString() + (null==rmDef?"":rmDef.getUnit()));
					}else{
						vmBo.setMemRate("--");
					}
				}
				// 磁盘使用百分比
				if(isMonitor_DISKUSAGE && metricMap.containsKey("DiskUsagePercentage")){
					if(null!=metricMap.get("DiskUsagePercentage")){
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(vmBo.getResourceId(), "DiskUsagePercentage");
						vmBo.setDatastoreRate(metricMap.get("DiskUsagePercentage").toString() + (null==rmDef?"":rmDef.getUnit()));
					}else{
						vmBo.setDatastoreRate("--");
					}
				}
				// CPU百分比
				if(isMonitor_cpuPercent && metricMap.containsKey("CPUPercent")){
					if(null!=metricMap.get("CPUPercent")){
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(vmBo.getResourceId(), "CPUPercent");
						vmBo.setCpuPercent(metricMap.get("CPUPercent").toString() + (null==rmDef?"":rmDef.getUnit()));
					}else{
						vmBo.setCpuPercent("--");
					}
				}
				// 内存百分比
				if(isMonitor_memPercent && metricMap.containsKey("MEMPercent")){
					if(null!=metricMap.get("MEMPercent")){
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(vmBo.getResourceId(), "MEMPercent");
						vmBo.setMemPercent(metricMap.get("MEMPercent").toString() + (null==rmDef?"":rmDef.getUnit()));
					}else{
						vmBo.setMemPercent("--");
					}
				}
				// 空间利用率
				if(isMonitor_physicalRate && metricMap.containsKey("physicalRate")){
					if(null!=metricMap.get("physicalRate")){
						ResourceMetricDef rmDef = capacityService.getResourceMetricDef(vmBo.getResourceId(), "physicalRate");
						vmBo.setPhysicalRate(metricMap.get("physicalRate").toString() + (null==rmDef?"":rmDef.getUnit()));
					}else{
						vmBo.setPhysicalRate("--");
					}
				}
				// CPU指标状态
				if(isMonitor_CPU && iMStateMap.containsKey(instanceId)){
					if(iMStateMap.get(instanceId).containsKey(MetricIdConsts.METRIC_CPU_RATE)){
						vmBo.setCpuRateState(metricStateToColor(iMStateMap.get(instanceId).get(MetricIdConsts.METRIC_CPU_RATE)));
					}
				}
				// 内存指标状态
				if(isMonitor_MEM && iMStateMap.containsKey(instanceId)){
					if(iMStateMap.get(instanceId).containsKey(MetricIdConsts.METRIC_MEME_RATE)){
						vmBo.setMemRateState(metricStateToColor(iMStateMap.get(instanceId).get(MetricIdConsts.METRIC_MEME_RATE)));
					}
				}
				// 磁盘使用百分比指标状态
				if(isMonitor_DISKUSAGE && iMStateMap.containsKey(instanceId)){
					if(iMStateMap.get(instanceId).containsKey("DiskUsagePercentage")){
						vmBo.setDatastoreRateState(metricStateToColor(iMStateMap.get(instanceId).get("DiskUsagePercentage")));
					}
				}
				// CPU百分比指标状态
				if(isMonitor_cpuPercent && iMStateMap.containsKey(instanceId)){
					if(iMStateMap.get(instanceId).containsKey("CPUPercent")){
						vmBo.setCpuPercentState(metricStateToColor(iMStateMap.get(instanceId).get("CPUPercent")));
					}
				}
				// 内存百分比指标状态
				if(isMonitor_memPercent && iMStateMap.containsKey(instanceId)){
					if(iMStateMap.get(instanceId).containsKey("MEMPercent")){
						vmBo.setMemPercentState(metricStateToColor(iMStateMap.get(instanceId).get("MEMPercent")));
					}
				}
				// 空间利用率指标状态
				if(isMonitor_physicalRate && iMStateMap.containsKey(instanceId)){
					if(iMStateMap.get(instanceId).containsKey("physicalRate")){
						vmBo.setPhysicalRateState(metricStateToColor(iMStateMap.get(instanceId).get("physicalRate")));
					}
				}
			}
			
			//信息指标
			if(infoMetricMap.containsKey(instanceId)){
				for(MetricData metricData : infoMetricMap.get(instanceId)){
					ResourceMetricDef rmd = null;
					switch (metricData.getMetricId()) {
					case "HostNum":{
						Integer value = metricData.getData() != null && metricData.getData().length > 0 ? Integer.valueOf(metricData.getData()[0]) : null;
						vmBo.setExsiTotal(value);
						break;
					}
					case "VMNum":{
						Integer value = metricData.getData() != null && metricData.getData().length > 0 ? Integer.valueOf(metricData.getData()[0]) : null;
						vmBo.setVmTotal(value);
						break;
					}
					case "TotalMemSize":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "TotalMemSize");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setAllMem(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "DatastoreSize":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "DatastoreSize");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setAllDatastore(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "CPUNum":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "CPUNum");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setAllCpu(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "MEMNum":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "MEMNum");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setAllMem(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "DataStorageFreeSpace":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "DataStorageFreeSpace");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setFreeDatastore(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "DataStorageVolume":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "DataStorageVolume");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setAllDatastore(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "osVersion":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "osVersion");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setVmOS(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "address":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "address");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setAddress(UnitTransformUtil.transform(value, rmd.getUnit()));
							if("FusionComputeVMs".equals(categoryId) && (null==vmBo.getIp()||"".equals(vmBo.getIp()))){
								vmBo.setIp(vmBo.getAddress());
							}
						}
						break;
					}
					case "physicalSize":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "physicalSize");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setPhysicalSize(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "physicalUtilisation":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "physicalUtilisation");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setPhysicalUtilisation(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case MetricIdConsts.SYS_UPTIME:{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), MetricIdConsts.SYS_UPTIME);
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setRuntime(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "type":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "type");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setType(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "ip":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "ip");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setIp(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "regeion":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "regeion");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setRegeion(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "cpuNum":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "cpuNum");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setCpuNum(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "memSize":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "memSize");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setMemSize(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					case "createTime":{
						rmd = capacityService.getResourceMetricDef(vmBo.getResourceId(), "createTime");
						if(rmd != null){
							String value = metricData.getData() != null && metricData.getData().length > 0 ? metricData.getData()[0] : null;
							vmBo.setCreateTime(UnitTransformUtil.transform(value, rmd.getUnit()));
						}
						break;
					}
					default:
						break;
					}
				}
			}
			List<String> modulePropMetrics = new ArrayList<String>();
			if("DTCenterECSs".equals(categoryId)){
				modulePropMetrics.add("uuid");
			}			
			try {
				for (int j = 0; j < modulePropMetrics.size(); j++) {
					String metricId = modulePropMetrics.get(j);
					ModuleProp moduleProp = null;
					switch (metricId) {
						case "uuid":
							moduleProp = modulePropService.getPropByInstanceAndKey(instanceId, metricId);
							vmBo.setUuid(moduleProp.getValues().length > 0 ? moduleProp.getValues()[0] : "");
							break;
						default:
							break;
					}
				}
			} catch (InstancelibException e) {
				logger.error("query module prop had throw a exception and instanceId is " + instanceId, e);
			}
			
			List<String> discoverPropMetrics = new ArrayList<String>();
			if("KyLinVms".equals(categoryId)){
				discoverPropMetrics.add("project");
			}
			try {
				for (int j = 0; j < discoverPropMetrics.size(); j++) {
					String metricId = discoverPropMetrics.get(j);
					DiscoverProp discoverProp = null;
					switch (metricId) {
					case "project":
						discoverProp = discoverPropService.getPropByInstanceAndKey(instanceId, metricId);
						vmBo.setProjectName(discoverProp.getValues().length > 0 ? discoverProp.getValues()[0] : "");
					default:
						break;
					}
				}
			} catch (InstancelibException e) {
				logger.error("query discover prop had throw a exception and instanceId is " + instanceId, e);
			}
			
			// 临时解决VirtualCluster的虚拟机数目采集不到问题
			if("VirtualCluster".equals(categoryId)) {
				int vmnum = 0;
				List<VmResourceTreePo> pos = vmResourceTreeDao.selectAllChildrenByInstanceId(instanceId);
				for (int j = 0; j < pos.size(); j++) {
					VmResourceTreePo po = pos.get(j);
					if("vmESXi".equals(po.getResourceid())) {

					} else
					if("VMWareVM".equals(po.getResourceid())) {
						vmnum++;
					}
				}
				vmBo.setVmTotal(vmnum);
			}
			// 临时解决VirtualStorage的虚拟机数目采集不到问题
			if("VirtualStorage".equals(categoryId)) {
				int vmnum = 0;
				MetricData md = infoMetricQueryAdaptApi.getMetricInfoData(instanceId, "VMList");
				if(md != null) {
					String[] vmUuidList = md.getData();
					if(vmUuidList != null && vmUuidList.length > 0) {
						vmnum = vmUuidList[0].split(",").length;
					}
				}
				vmBo.setVmTotal(vmnum);
			}
			
		}
	}

	@Override
	public boolean deleteVmResources(long[] instanceIds) throws Exception {
		List<Long> delInstanceIds = new ArrayList<Long>();
		for (int i = 0; i < instanceIds.length; i++) {
			long instanceId = instanceIds[i];
			ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
			String categoryId = ri.getCategoryId();
			boolean isDelChildResource = false;
			if("VirtualHost".equals(categoryId) || "VirtualCluster".equals(categoryId)
				|| "VirtualHost5.5".equals(categoryId) || "VirtualCluster5.5".equals(categoryId)
				|| "VirtualHost6".equals(categoryId) || "VirtualCluster6".equals(categoryId)
				|| "VirtualHost6.5".equals(categoryId) || "VirtualCluster6.5".equals(categoryId)){
				isDelChildResource = true;
			}
			delInstanceIds.addAll(deleteVmResource(instanceId, isDelChildResource));
			delInstanceIds.add(instanceId);
		}
		if(!delInstanceIds.isEmpty()){
			resourceInstanceService.removeResourceInstances(delInstanceIds);
		}
		return true;
	}
	
	@Override
	public boolean deleteResourcePools(String[] uuids) {
		for(String uuid:uuids){
			if(vmResourceTreeDao.delByUuid(uuid)!=1){
				return false;
			}
			//将相关虚拟机信息更新
			List<VmResourceTreePo> vrtList = vmResourceTreeDao.getResourcePoolVM(uuid);
			for(VmResourceTreePo vrt:vrtList){
				vrt.setResourcepool("");
				vmResourceTreeDao.updateByUuid(vrt);
				vrt.getDatacenteruuid();
			}
		}
		return true;
	}
	
	//设置其他类型资源数量
	private List<ResourceCategoryBo> setResourceCategoryBos(VmResourcePageBo pageBo,List<ResourceInstanceBo> risbListUnFilter){
		List<String> requiredCategory = new ArrayList<String>();
		requiredCategory.add("VM");
		return resourceCategoryApi.getResourceCategoryListByResources(risbListUnFilter, requiredCategory);
	}
	
	@Override
	public VmResourcePageBo getResourcePoolList(VmResourcePageBo pageBo,ILoginUser user){
		List<ResourceInstanceBo> risbListUnFilter = resourceApi.getResources(user);
		List<VmResourceTreePo> vtpList = vmResourceTreeDao.getVMTreeListAll(pageBo.getCondition());
		List<VmResourceBo> vrbList = new ArrayList<VmResourceBo>();
		for(VmResourceTreePo vpt:vtpList){
			String hostUuid = vpt.getHostuuid();
			String clusterUuid = vpt.getClusteruuid();
			Long instanceId = new Long(0);
			if(null!=hostUuid&&!"".equals(hostUuid)){
				VmResourceTreePo vstp = vmResourceTreeDao.selectByUuid(hostUuid);
				instanceId = vstp.getInstanceid();
			}else if(null!=clusterUuid&&!"".equals(clusterUuid)){
				VmResourceTreePo vstp = vmResourceTreeDao.selectByUuid(clusterUuid);
				instanceId = vstp.getInstanceid();
			}
			//资源池所在的宿主机或集群,在用户权限内,才允许用户查看资源池
			boolean limitFlag = false;
			if(instanceId>0){
				for(ResourceInstanceBo rib:risbListUnFilter){
					if(instanceId.longValue()==rib.getId().longValue()){
						limitFlag = true;
						break;
					}
				}
			}
			
			if(limitFlag){
				VmResourceBo vrb = boTopo(vpt);
				vrbList.add(vrb);
				
				List<VmResourceTreePo> temp = vmResourceTreeDao.getResourcePoolVM(vpt.getUuid());
				vrb.setVmTotalForPool(null==temp?0:temp.size());
				if(null!=hostUuid&&!"".equals(hostUuid)){
					VmResourceTreePo vstp = vmResourceTreeDao.selectByUuid(hostUuid);
					vrb.setExsi(vstp.getVmname());
				}
				if(null!=clusterUuid&&!"".equals(clusterUuid)){
					VmResourceTreePo vstp = vmResourceTreeDao.selectByUuid(clusterUuid);
					vrb.setCluster(vstp.getVmname());
				}
			}
		}
		List<VmResourceBo> resultList = new ArrayList<VmResourceBo>();
		int startRow = (int)pageBo.getStartRow();
		int count = (int)pageBo.getRowCount();
		int totalSize = vrbList.size();
		int toIndex = (totalSize<(startRow+count))?totalSize:(startRow+count);
		resultList = vrbList.subList(startRow, toIndex);
		
		//设置数量
		List<ResourceCategoryBo> rcbList = setResourceCategoryBos(pageBo,risbListUnFilter);
		for(ResourceCategoryBo rcb:rcbList){
			if(rcb.getId().equals("VM")){
				List<ResourceCategoryBo> rcbChildrenList = rcb.getChildCategorys();
				for(ResourceCategoryBo rcbChild:rcbChildrenList){
					if(rcbChild.getId().equals("VMware") || 
						"VMware5.5".equals(rcbChild.getId()) ||
						"VMware6".equals(rcbChild.getId()) ||
						"VMware6.5".equals(rcbChild.getId())){
						ResourceCategoryBo pool = new ResourceCategoryBo();
						pool.setId("ResourcePool");
						pool.setName("资源池");
//						pool.setResourceNumber(totalSize);
						pool.setResourceNumber(getVMCountByType("ResourcePool",risbListUnFilter));
						
						if(null!=rcbChild.getChildCategorys()){
							rcbChild.getChildCategorys().add(pool);
						}else{
							List<ResourceCategoryBo> rcbListTemp = new ArrayList<ResourceCategoryBo>();
							rcbListTemp.add(pool);
							rcbChild.setChildCategorys(rcbListTemp);
						}
					}
				}
			}
		}
		pageBo.setResourceCategoryBos(rcbList);
		
		pageBo.setVmResources(resultList);
		pageBo.setTotalRecord(totalSize);
		return pageBo;
	}
	
	@Override
	public VmResourcePageBo getResourcePoolVMByPage(VmResourcePageBo pageBo){
		long startPostion = pageBo.getStartRow();
		long rowCount = pageBo.getRowCount();
		Page<VmResourceTreePo, VmResourceBo> page = new Page<VmResourceTreePo, VmResourceBo>();
		page.setCondition(pageBo.getCondition());
		page.setStartRow(startPostion);
		page.setRowCount(rowCount);
		
		vmResourceTreeDao.getResourcePoolVMByPage(page);
		List<VmResourceTreePo> vtpList = page.getDatas();
		if(null==vtpList || vtpList.size()==0){
			return pageBo;
		}
		
		List<Long> instanceIds = new ArrayList<Long>();
		List<VmResourceBo> vrbList = new ArrayList<VmResourceBo>();
		
		List<VmResourceBo> vrbList5_0 = new ArrayList<VmResourceBo>();
		List<VmResourceBo> vrbList5_5 = new ArrayList<VmResourceBo>();
		List<VmResourceBo> vrbList6_0 = new ArrayList<VmResourceBo>();
		List<VmResourceBo> vrbList6_5 = new ArrayList<VmResourceBo>();
		
		for(VmResourceTreePo vpt:vtpList){
			VmResourceBo vrb = boTopo(vpt);
			if("VirtualCluster".equals(vrb.getCategoryId()) || 
				"VirtualHost".equals(vrb.getCategoryId()) || 
				"VirtualVM".equals(vrb.getCategoryId()) || 
				"VirtualStorage".equals(vrb.getCategoryId())){
				vrbList5_0.add(vrb);
			}else if("VirtualCluster5.5".equals(vrb.getCategoryId()) || 
					"VirtualHost5.5".equals(vrb.getCategoryId()) || 
					"VirtualVM5.5".equals(vrb.getCategoryId()) || 
					"VirtualStorage5.5".equals(vrb.getCategoryId())){
				vrbList5_5.add(vrb);
			}else if("VirtualCluster6".equals(vrb.getCategoryId()) || 
					"VirtualHost6".equals(vrb.getCategoryId()) || 
					"VirtualVM6".equals(vrb.getCategoryId()) || 
					"VirtualStorage6".equals(vrb.getCategoryId())){
				vrbList6_0.add(vrb);
			}else if("VirtualCluster6.5".equals(vrb.getCategoryId()) || 
					"VirtualHost6.5".equals(vrb.getCategoryId()) || 
					"VirtualVM6.5".equals(vrb.getCategoryId()) || 
					"VirtualStorage6.5".equals(vrb.getCategoryId())){
				vrbList6_5.add(vrb);
			}
			//vrbList.add(vrb);
			instanceIds.add(vpt.getInstanceid());
		}
		
		// 查询资源状态
		List<InstanceStateData> instanceStateDataList = instanceStateService.findStates(instanceIds);
		// 组装资源状态
		Map<Long, InstanceStateEnum> instanceStateDataMap = new HashMap<Long, InstanceStateEnum>();
		for(InstanceStateData isd:instanceStateDataList){
			instanceStateDataMap.put(isd.getInstanceID(), isd.getState());
		}
		for(VmResourceBo vrb:vrbList5_0){
			if(instanceStateDataMap.containsKey(vrb.getId())){
				vrb.setResourceStatus(instanceStateToColor(instanceStateDataMap.get(vrb.getId())));
			}else{
				vrb.setResourceStatus("res_normal_nothing");
			}
		}
		for(VmResourceBo vrb:vrbList5_5){
			if(instanceStateDataMap.containsKey(vrb.getId())){
				vrb.setResourceStatus(instanceStateToColor(instanceStateDataMap.get(vrb.getId())));
			}else{
				vrb.setResourceStatus("res_normal_nothing");
			}
		}
		for(VmResourceBo vrb:vrbList6_0){
			if(instanceStateDataMap.containsKey(vrb.getId())){
				vrb.setResourceStatus(instanceStateToColor(instanceStateDataMap.get(vrb.getId())));
			}else{
				vrb.setResourceStatus("res_normal_nothing");
			}
		}
		for(VmResourceBo vrb:vrbList6_5){
			if(instanceStateDataMap.containsKey(vrb.getId())){
				vrb.setResourceStatus(instanceStateToColor(instanceStateDataMap.get(vrb.getId())));
			}else{
				vrb.setResourceStatus("res_normal_nothing");
			}
		}
		
		queryResourceMetricInfo(vrbList5_0, InstanceLifeStateEnum.MONITORED, "VirtualVM");
		queryResourceMetricInfo(vrbList5_5, InstanceLifeStateEnum.MONITORED, "VirtualVM5.5");
		queryResourceMetricInfo(vrbList6_0, InstanceLifeStateEnum.MONITORED, "VirtualVM6");
		queryResourceMetricInfo(vrbList6_5, InstanceLifeStateEnum.MONITORED, "VirtualVM6.5");
		
		vrbList.addAll(vrbList5_0);
		vrbList.addAll(vrbList5_5);
		vrbList.addAll(vrbList6_0);
		vrbList.addAll(vrbList6_5);
		
		pageBo.setVmResources(vrbList);
		pageBo.setTotalRecord(page.getTotalRecord());
		return pageBo;
	}
	
	private VmResourceBo boTopo(VmResourceTreePo po){
		VmResourceBo bo = new VmResourceBo();
		bo.setSourceName(po.getVmname());
		bo.setShowName(po.getVmname());
		bo.setResourceId(po.getResourceid());
		
		bo.setUuid(po.getUuid());
		bo.setExsi(po.getHostuuid());
		bo.setCluster(po.getClusteruuid());
		if(null!=po.getInstanceid() && po.getInstanceid()>0){
			bo.setId(po.getInstanceid());
		}
		return bo;
	}
	
	private List<Long> deleteVmResource(long instanceId, boolean isDelChildResource){
		List<Long> delInstanceIds = new ArrayList<Long>();
		// 查询出结构树下的所有资源
		List<VmResourceTreePo> childVmTreePos = vmResourceTreeDao.selectChildrenByInstanceId(instanceId);
		for (int i = 0; childVmTreePos != null && i < childVmTreePos.size(); i++) {
			VmResourceTreePo childVmTreePo = childVmTreePos.get(i);
			if(childVmTreePo.getInstanceid() != null){
				deleteVmResource(childVmTreePo.getInstanceid(), isDelChildResource);
			}
			else if(childVmTreePo.getVmtype().equals("ResourcePool")){
				//删除host cluster时,需要删除其下的资源池
				vmResourceTreeDao.delByUuid(childVmTreePo.getUuid());
			}
			if(isDelChildResource && childVmTreePo.getInstanceid() != null){
				delInstanceIds.add(childVmTreePo.getInstanceid());
			}
		}
		
		// 删除树结构上的主节点
		vmResourceTreeDao.delByInstanceId(instanceId);
		return delInstanceIds;
	}
	
}
