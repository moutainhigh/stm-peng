package com.mainsteam.stm.home.workbench.resource.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.home.workbench.resource.bo.PageResource;
import com.mainsteam.stm.home.workbench.resource.bo.WorkbenchResourceInstance;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.system.resource.bo.CategoryBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;

/**
 * <li>文件名称: ResourceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */
@Service("stm_home_workbench_resourceApi")
public class ResourceImpl implements IResourceApi{
	
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	
	@Autowired
	private CapacityService capacityService;
	
	@Autowired
	@Qualifier("customResourceGroupApi")
	private ICustomResourceGroupApi resourceGroupApi;
	
	@Autowired
	@Qualifier("stm_system_resourceApi")
	private com.mainsteam.stm.system.resource.api.IResourceApi systemResourceApi;
	
	private static final Log logger = LogFactory.getLog(ResourceImpl.class);
	
	private CategoryBo empty=new CategoryBo();
	
	@Override
	public CategoryBo getAllCategory() {
		try {
			return systemResourceApi.getTreeCategory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empty;
	}
	
	List<ResourceInstance> tempRs=new ArrayList<ResourceInstance>();
	@Override
	public List<ResourceInstanceBo> getResources(String catogaryId) {
		List<ResourceInstanceBo> rBos=new ArrayList<ResourceInstanceBo>();
		try {
			List<ResourceInstance> rs=resourceInstanceService.getParentInstanceByCategoryId(catogaryId);
			if(rs==null)rs=tempRs;
			ResourceInstance r;
			ResourceInstanceBo rBo;
			for (int i = 0,len=rs.size(); i < len; i++) {
				r=rs.get(i);
				rBo=new ResourceInstanceBo();
				rBo.setCategoryId(r.getCategoryId());
				rBo.setDiscoverIP(r.getShowIP());
				rBo.setId(r.getId());
				rBo.setName(r.getName());
				rBo.setResourceId(r.getResourceId());
				rBo.setShowName(r.getShowName());
				rBos.add(rBo);
			}
			return rBos;
		} catch (InstancelibException e) {
			e.printStackTrace();
			return rBos;
		}
	}

	@Override
	public PageResource getResourceCount(String typeId,ILoginUser user, Long domainId) {
		PageResource pageResource=new PageResource();
		List<CategoryBo> children = new ArrayList<CategoryBo>();
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setDomainIds(Arrays.asList(domainId));
		if("App".equals(typeId)){
			List<String> appCategroIds =  getCategorIds("App");
			for(String type :appCategroIds){
				CategoryBo category = systemResourceApi.getTreeCategory(type);
				if(category==null||category.getChildren()==null){
					continue;
				}
				children.addAll(Arrays.asList(category.getChildren()));
			}
			queryBo.setCategoryIds(appCategroIds);
			
		}else{
			CategoryBo category = systemResourceApi.getTreeCategory(typeId);
			if(category==null||category.getChildren()==null){
				return pageResource;
			}
			children.addAll(Arrays.asList(category.getChildren()));
			queryBo.setCategoryIds(Arrays.asList(typeId));
		}
		
		List<ResourceInstanceBo>  resources = systemResourceApi.getResources(queryBo);
		if(resources==null){
			return pageResource;
		}
		
		Map<String, Integer> dicCategory = new HashMap<String, Integer>();
		for(ResourceInstanceBo resource : resources){
			String categoryId = resource.getCategoryId();
			dicCategory.put(categoryId, dicCategory.containsKey(categoryId) ? dicCategory.get(categoryId)+1 : 1);
		}
		int count = 0;
		int total=0;
		List<Integer> data=new ArrayList<Integer>();
		List<String> categories=new ArrayList<String>();
		for(CategoryBo categoryBo : children){
			categories.add(categoryBo.getName());
			Set<String> subCategoryIds = categoryBo.getSubCategoryIds();
			for(String categoryId : subCategoryIds){
				Integer dicCount = dicCategory.get(categoryId);
				count += dicCount!=null ? dicCount : 0;
			}
			data.add(count);
			total+=count;
			count = 0;
		}
		pageResource.setCategories(categories);
		pageResource.setData(data);
		pageResource.setTotal(total);
		return pageResource;
	}
	
	@Override
	public PageResource getResourceCount(String typeId, ILoginUser user, Long... domainId) {
		PageResource pageResource=new PageResource();
		List<CategoryBo> children = new ArrayList<CategoryBo>();
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setDomainIds(Arrays.asList(domainId));
		if("App".equals(typeId)){
			List<String> appCategroIds =  getCategorIds("App");
			for(String type :appCategroIds){
				CategoryBo category = systemResourceApi.getTreeCategory(type);
				if(category==null||category.getChildren()==null){
					continue;
				}
				children.addAll(Arrays.asList(category.getChildren()));
			}
			queryBo.setCategoryIds(appCategroIds);
			
		}else{
			CategoryBo category = systemResourceApi.getTreeCategory(typeId);
			if(category==null||category.getChildren()==null){
				return pageResource;
			}
			children.addAll(Arrays.asList(category.getChildren()));
			queryBo.setCategoryIds(Arrays.asList(typeId));
		}
		
		List<ResourceInstanceBo>  resources = systemResourceApi.getResources(queryBo);
		if(resources==null){
			return pageResource;
		}
		
		Map<String, Integer> dicCategory = new HashMap<String, Integer>();
		for(ResourceInstanceBo resource : resources){
			String categoryId = resource.getCategoryId();
			dicCategory.put(categoryId, dicCategory.containsKey(categoryId) ? dicCategory.get(categoryId)+1 : 1);
		}
		int count = 0;
		int total=0;
		List<Integer> data=new ArrayList<Integer>();
		List<String> categories=new ArrayList<String>();
		for(CategoryBo categoryBo : children){
			categories.add(categoryBo.getName());
			Set<String> subCategoryIds = categoryBo.getSubCategoryIds();
			for(String categoryId : subCategoryIds){
				Integer dicCount = dicCategory.get(categoryId);
				count += dicCount!=null ? dicCount : 0;
			}
			data.add(count);
			total+=count;
			count = 0;
		}
		pageResource.setCategories(categories);
		pageResource.setData(data);
		pageResource.setTotal(total);
		return pageResource;
	}
	
	@Override
	public List<Long> queryGroupResourceByDomain(String type,ILoginUser user,long groupId,Long... domainId){
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		if(domainId != null){
			queryBo.setDomainIds(Arrays.asList(domainId));
		}
		//获取用户、域、资源类别下的资源实例
		//List<Long> resourceInstanceIds = new ArrayList<Long>();
		Set<Long> parentInstaceIdSet = new HashSet<Long>();
		if(type!=null && !type.equals("")){
			//通过“主机、网络、应用”获取子资源类别
			List<String> categories = this.getCategorIds(type);
			queryBo.setCategoryIds(categories);
		}
		
		//*按域查询告警信息,这段代码用于校验getResources()该方法是否正确返回指定域数据//
		List<ResourceInstanceBo> instanceBos = systemResourceApi.getResources(queryBo);
		
		for(int j = 0; instanceBos != null && j < instanceBos.size(); j++){
			ResourceInstanceBo instance = instanceBos.get(j);
			if(instance!=null && instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)){
				parentInstaceIdSet.add(instance.getId());
			}
		}
		
		List<Long> resultInstanceIds = new ArrayList<Long>();
		//如果设置了资源组，在资源组中过虑资源
		if(groupId!=0){
			Set<Long> groupInstanceIdSet = new HashSet<Long>();
			List<CustomGroupBo> boList = resourceGroupApi.getList(user.getId());
			List<Long> resourceIds = new ArrayList<Long>();
			CustomGroupBo groupBo = resourceGroupApi.getCustomGroup(groupId);
			boList2Tree(groupBo, boList, resourceIds);
			List<String> resourceDatas= new ArrayList<String>();
			for (Long id : resourceIds) {
				resourceDatas.add(String.valueOf(id));
			}
			List<String> groupInstanceIds = resourceDatas != null ? resourceDatas : null;//资源组里的所有资源
		//	List<String> groupInstanceIds = groupBo != null ? groupBo.getResourceInstanceIds() : null;//资源组里的所有资源
			if(!parentInstaceIdSet.isEmpty() && groupInstanceIds != null){
				for(Long parentInstanceId : parentInstaceIdSet){
					if(groupInstanceIds.contains(String.valueOf(parentInstanceId))){
						groupInstanceIdSet.add(parentInstanceId);
						resultInstanceIds.add(parentInstanceId);
					}
				}
			/*	if(!groupInstanceIdSet.isEmpty()){
					List<Long> childInstanceIdList = resourceInstanceService.getAllChildrenInstanceIdbyParentId(groupInstanceIdSet,InstanceLifeStateEnum.MONITORED);
					for(int j = 0; childInstanceIdList != null && j < childInstanceIdList.size(); j++){
						resultInstanceIds.add(childInstanceIdList.get(j));
					}
				}*/
			}
		}else{
			if(!parentInstaceIdSet.isEmpty()){
				for(Long parentInstanceId : parentInstaceIdSet){
					resultInstanceIds.add(parentInstanceId);
				}

			/*	List<Long> childInstanceIdList = resourceInstanceService.getAllChildrenInstanceIdbyParentId(parentInstaceIdSet,InstanceLifeStateEnum.MONITORED);
				for(int j = 0; childInstanceIdList != null && j < childInstanceIdList.size(); j++){
					resultInstanceIds.add(childInstanceIdList.get(j));
				}*/

			}
		}
	//	logger.error("resultInstanceIds: "+Arrays.asList(resultInstanceIds));
		return resultInstanceIds;
	
	}
	
	private void boList2Tree(CustomGroupBo parentBo, List<CustomGroupBo> boList, List<Long> resourceIds){
		for (int i = 0; parentBo != null && parentBo.getResourceInstanceIds() != null && i < parentBo.getResourceInstanceIds().size(); i++) {
			resourceIds.add(Long.valueOf(parentBo.getResourceInstanceIds().get(i)));
		}
		for (int i = 0; i < boList.size(); i++) {
			CustomGroupBo bo = boList.get(i);
			if(bo.getPid() != null && parentBo != null && parentBo.getId().longValue() == bo.getPid().longValue()){
				boList2Tree(bo, boList, resourceIds);
			}
		}
	}
	@Override
	public List<Long> queryGroupParentResourceByDomain(String type,ILoginUser user,long groupId,Long... domainId){
		
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		if(domainId != null){
			queryBo.setDomainIds(Arrays.asList(domainId));
		}
		//获取用户、域、资源类别下的资源实例
		List<Long> resourceInstanceIds = new ArrayList<Long>();
		if(type!=null && !type.equals("")){
			//通过“主机、网络、应用”获取子资源类别
			List<String> categories = this.getCategorIds(type);
			queryBo.setCategoryIds(categories);
		}
		queryBo.setLifeState(InstanceLifeStateEnum.MONITORED.toString());
		List<ResourceInstanceBo> instanceBos = systemResourceApi.getResources(queryBo);
		for (ResourceInstanceBo instance : instanceBos) {
			if(instance.getLifeState() == InstanceLifeStateEnum.MONITORED){
				resourceInstanceIds.add(instance.getId());
			}
		}
		List<Long> resultInstanceIds = new ArrayList<Long>();
		//如果设置了资源组，在资源组中过虑资源
		if(groupId!=0){
			CustomGroupBo groupBo = resourceGroupApi.getCustomGroup(groupId);
			List<String> groupInstanceIds = groupBo.getResourceInstanceIds();//资源组里的所有资源
			for (Long instanceId : resourceInstanceIds) {
				if(groupInstanceIds.contains(String.valueOf(instanceId))){
					resultInstanceIds.add(instanceId);
				}
			}
		}else{
            resultInstanceIds.addAll(resourceInstanceIds);
			/*for (Long instanceId : resourceInstanceIds) {
				resultInstanceIds.add(instanceId);
			}*/
		}
		return resultInstanceIds;
	}

	@Override
	public List<Long> getCategoriesInstanceIds(String resource) {
		try {
			//通过类型获取所有categories
			List<String> categories = this.getCategorIds(resource);
			//通过categorId获取父资源实例ＩＤ
			List<Long> instanceIds = new ArrayList<Long>();//所有资源实例ID集合
			if(null!=categories){
				for (String cat : categories) {
					CategoryDef categoryDef = capacityService.getCategoryById(cat);//获取父资源类别
					CategoryDef[] childCategorys = categoryDef==null?null:categoryDef.getChildCategorys();//获取子资源类别
					if(childCategorys!=null){
						for (CategoryDef chilDef : childCategorys) {
							List<ResourceInstance> resourceInstances = resourceInstanceService.getParentInstanceByCategoryId(chilDef.getId());
							if(null!=resourceInstances){
								for (ResourceInstance instance : resourceInstances) {
									instanceIds.add(instance.getId());
								}
							}
						}
					}
				}
			}
			return instanceIds;
		} catch (InstancelibException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<String> getCategorIds(String type){
		List<String> categories = null;
		if(null!=type){
			if(type.equals("All")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.HOST);
				categories.add(CapacityConst.NETWORK_DEVICE);
				//categories.add(CapacityConst.STORAGE);
				categories.add(CapacityConst.DATABASE);
				categories.add(CapacityConst.MIDDLEWARE);
				categories.add(CapacityConst.J2EEAPPSERVER);
				categories.add(CapacityConst.WEBSERVER);
				categories.add(CapacityConst.STANDARDSERVICE);
				categories.add(CapacityConst.MAILSERVER);
				categories.add(CapacityConst.DIRECTORY);
				categories.add(CapacityConst.LOTUSDOMINO);
				categories.add(CapacityConst.VM);
				categories.add(CapacityConst.SNMPOTHERS);
				categories.add("UniversalModel");
				categories.add(CapacityConst.LINK);//链路
			} else if(type.equals("Host")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.HOST);
			}else if(type.equals("NetworkDevice")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.NETWORK_DEVICE);
				categories.add(CapacityConst.SNMPOTHERS);
				categories.add(CapacityConst.LINK);
			} else if(type.equals("App")){
				categories = new ArrayList<String>();
				//categories.add(CapacityConst.STORAGE);
				categories.add(CapacityConst.DATABASE);//
				categories.add(CapacityConst.MIDDLEWARE);//
				categories.add(CapacityConst.J2EEAPPSERVER);//
				categories.add(CapacityConst.WEBSERVER);//
				categories.add(CapacityConst.STANDARDSERVICE);//
				categories.add(CapacityConst.MAILSERVER);//
				categories.add(CapacityConst.DIRECTORY);//
				categories.add(CapacityConst.LOTUSDOMINO);//
				categories.add(CapacityConst.VM);
				categories.add("UniversalModel");
			}else if(type.equals("Link")){
				categories = new ArrayList<String>();
				categories.add(CapacityConst.LINK);
			}else{
				categories = new ArrayList<String>();
				categories.add(type);
			}
		}
		return categories;
	}

	/**
	 * 根据资源类别ID获取主资源实例
	 */
	@Override
	public List<WorkbenchResourceInstance> getResourceInstanceByCategoryId(String categoryId,Long[] domainId,int startNum,int pageSize,String content,ILoginUser user) {
		List<WorkbenchResourceInstance> instanceList = new ArrayList<WorkbenchResourceInstance>();
		CategoryDef def = capacityService.getCategoryById(categoryId);
		if(def == null){
			if(logger.isErrorEnabled()){
				logger.error("Get categoryDef is null,categoryId = " + categoryId);
			}
			return instanceList;
		}
		
		//获取叶子类别
		List<CategoryDef> leafCategoryList = capacityService.getLeafCategoryList(def);
		List<String> leafCategoryNameList = new ArrayList<String>();
		
		if(leafCategoryList == null){
			if(logger.isErrorEnabled()){
				logger.error("Get leafCategryList is null,categoryId = " + categoryId);
			}
			return instanceList;
		}
		
		for(CategoryDef leafDef : leafCategoryList){
			leafCategoryNameList.add(leafDef.getId());
		}
		
		ResourceQueryBo queryBo = new ResourceQueryBo(user);
		queryBo.setCategoryIds(leafCategoryNameList);
		List<Long> domainIds = new ArrayList<Long>();
		for (int i = 0; i < domainId.length; i++) {
			domainIds.add(domainId[i]);
		}
		queryBo.setDomainIds(domainIds);
		List<ResourceInstanceBo> instances = systemResourceApi.getResources(queryBo);
		if(instances != null && instances.size() > 0){
			for(ResourceInstanceBo instance : instances){
				//判断实例是否监控和是否属于当前用户域
				if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
					continue;
				}
				
				WorkbenchResourceInstance reportInstance = new WorkbenchResourceInstance();
				BeanUtils.copyProperties(instance, reportInstance);
				if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
					reportInstance.setShowName(instance.getName());
				}
				
				ResourceDef resourceDef = capacityService.getResourceDefById(reportInstance.getResourceId());
				if(resourceDef == null){
					if(logger.isErrorEnabled()){
						logger.error("Get resourceDef is null ,resourceId : " + reportInstance.getResourceId());
					}
				}else{
					reportInstance.setResourceName(resourceDef.getName());
					
					if(content != null && !content.trim().equals("")){
						if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
								!reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) && 
								!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
							continue;
						}
					}
					
					instanceList.add(reportInstance);
				}
			}
		}
		
		if((startNum + pageSize) > instanceList.size()){
			instanceList = instanceList.subList(startNum, instanceList.size());
		}else{
			instanceList = instanceList.subList(startNum, (startNum + pageSize));
		}
		
		return instanceList;
	}

	/**
	 * 根据资源获取资源实例
	 */
	@Override
	public List<WorkbenchResourceInstance> getInstanceByResource(String resourceId,Long[] domainId,int startNum,int pageSize,String content,ILoginUser user) {
		List<WorkbenchResourceInstance> instances = new ArrayList<WorkbenchResourceInstance>();
		
		if(resourceId.contains(",")){
			//有多个资源
			int index = 0;
			
			out : for(String singleId : resourceId.split(",")){
				try {
					List<ResourceInstance> instanceList = null;
					try{
						instanceList = resourceInstanceService.getResourceInstanceByResourceId(singleId);
					}catch(Exception e){}
					
					if(instanceList == null || instanceList.size() <= 0){
						continue;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					for (int i = 0; i < domainId.length; i++) {
						domainIds.add(domainId[i]);
					}
					queryBo.setDomainIds(domainIds);
					List<Long> accessMainResourceIntancesIds = systemResourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					
					ResourceDef def = capacityService.getResourceDefById(singleId);
					if(def == null){
						if(logger.isErrorEnabled()){
							logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + singleId);
						}
						continue;
					}
					
					for(int i = 0 ; i < instanceList.size() ; i++){
						
						if(instances.size() >= pageSize){
							break out;
						}
						
						ResourceInstance instance = instanceList.get(i);
						
						//判断该子资源是否属于用户
						if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
							continue;
						}
						//判断实例是否监控和是否属于当前用户域
						if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
							continue;
						}
						
						WorkbenchResourceInstance reportInstance = new WorkbenchResourceInstance();
						reportInstance.setId(instance.getId());
						reportInstance.setResourceId(singleId);
						reportInstance.setShowName(instance.getShowName());
						if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
							reportInstance.setShowName(instance.getName());
						}

						reportInstance.setResourceName(def.getName());
						
						reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
						
						if(content != null && !content.trim().equals("")){
							if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
									(reportInstance.getDiscoverIP()!= null && !reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase())) && 
									!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
								continue;
							}
						}
						
						if(index < startNum){
							index++;
							continue;
						}
						
						instances.add(reportInstance);
					}
				} catch (InstancelibException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
				}
			}
		}else{
			//一个资源
			try {
				ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
				if(resourceDef.getParentResourceDef() != null){
					//子资源
					List<ResourceInstance> instanceList = resourceInstanceService.getResourceInstanceByResourceId(resourceId);
					if(instanceList == null || instanceList.size() <= 0){
						return instances;
					}
					List<Long> mainResourceIntanceIds = new ArrayList<Long>();
					Set<Long> mainResourceIntanceIdsSet = new HashSet<Long>();
					
					int index = 0;
					
					for(ResourceInstance instance : instanceList){
						//找出所有主资源实例
						mainResourceIntanceIdsSet.add(instance.getParentId());
					}
					mainResourceIntanceIds = new ArrayList<Long>(mainResourceIntanceIdsSet);
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					HashMap<Long, Long> mapDomains = new HashMap<>();
					for (int i = 0; i < domainId.length; i++) {
						domainIds.add(domainId[i]);
						mapDomains.put(domainId[i], domainId[i]);
					}
					queryBo.setDomainIds(domainIds);
					
					List<Long> accessMainResourceIntancesIds = systemResourceApi.accessFilter(queryBo, mainResourceIntanceIds);
					if(instanceList != null && instanceList.size() >0){
						for(ResourceInstance instance : instanceList){
							
							if(instances.size() >= pageSize){
								break;
							}
							
							//判断该子资源是否属于用户
							if(!accessMainResourceIntancesIds.contains(instance.getParentId())){
								continue;
							}
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED || !mapDomains.containsKey(instance.getDomainId())){
								continue;
							}
							WorkbenchResourceInstance reportInstance = new WorkbenchResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
								reportInstance.setShowName(instance.getName());
							}
							
							ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
								}
								continue;
							}
							reportInstance.setResourceName(def.getName());
							
							if(content != null && !content.trim().equals("")){
								if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
									continue;
								}
							}
							
							if(instance.getParentId() > 0){
								reportInstance.setDiscoverIP(resourceInstanceService.getResourceInstance(instance.getParentId()).getShowIP());
							}
							
							if(index < startNum){
								index++;
								continue;
							}
							
							instances.add(reportInstance);
						}
					}
				}else{
					//主资源
					ResourceQueryBo queryBo = new ResourceQueryBo(user);
					List<Long> domainIds = new ArrayList<Long>();
					for (int i = 0; i < domainId.length; i++) {
						domainIds.add(domainId[i]);
					}
					queryBo.setModuleId(resourceId);
					queryBo.setDomainIds(domainIds);
					List<ResourceInstanceBo> instanceList = systemResourceApi.getResources(queryBo);
					if(instanceList != null && instanceList.size() >0){
						
						int index = 0;
						
						for(ResourceInstanceBo instance : instanceList){
							
							if(instances.size() >= pageSize){
								break;
							}
							
							//判断实例是否监控和是否属于当前用户域
							if(instance.getLifeState() != InstanceLifeStateEnum.MONITORED){
								continue;
							}
							WorkbenchResourceInstance reportInstance = new WorkbenchResourceInstance();
							BeanUtils.copyProperties(instance, reportInstance);
							if(reportInstance.getShowName() == null || reportInstance.getShowName().equals("")){
								reportInstance.setShowName(instance.getName());
							}
							ResourceDef def = capacityService.getResourceDefById(reportInstance.getResourceId());
							if(def == null){
								if(logger.isErrorEnabled()){
									logger.error("Get resourceDef is null by capacityService.getResourceDefById , id = " + reportInstance.getResourceId());
								}
								continue;
							}
							reportInstance.setResourceName(def.getName());
							
							if(content != null && !content.trim().equals("")){
								if(!reportInstance.getShowName().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getDiscoverIP().toLowerCase().contains(content.toLowerCase()) && 
										!reportInstance.getResourceName().toLowerCase().contains(content.toLowerCase())){
									continue;
								}
							}
							
							if(instances.size() >= pageSize){
								break;
							}
							
							if(index < startNum){
								index++;
								continue;
							}
							
							instances.add(reportInstance);
						}
					}
				}
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return instances;
	}

}


