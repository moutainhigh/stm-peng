package com.mainsteam.stm.system.resource.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.exception.OriginalException;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.resource.api.Filter;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.CategoryBo;
import com.mainsteam.stm.system.resource.bo.ResourceModuleBo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.system.um.resourcegroup.api.IResourceGroupApi;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;

/**
 * <li>文件名称: ResourceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月20日
 * @author   ziwenwen
 */
@Service("stm_system_resourceApi")
public class ResourceImpl implements IResourceApi {
	
	@Autowired
	ResourceInstanceService RIService;

	@Autowired
	IUserApi userApi;

	@Autowired
	IResourceGroupApi resourceGroupApi;
	
	@Autowired
	CapacityService capacityService;

	private static final List<ResourceInstanceBo> emptyData=new ArrayList<ResourceInstanceBo>();
	
	private static final Logger LOG=Logger.getLogger(ResourceImpl.class);
	
	@Override
	public List<ResourceInstanceBo> getAllParentInstance(){
		List<ResourceInstance> resourceInstances=null;
		try {
			resourceInstances=RIService.getAllParentInstance();
		} catch (InstancelibException e) {
			LOG.error(e);
		}
		List<ResourceInstanceBo> bos=toBos(null,resourceInstances);
		return filter(bos);
	}
	
	@Override
	public List<ResourceInstanceBo> getResources(ILoginUser user) {
		List<ResourceInstance> resourceInstances=null;
		if(user.isSystemUser()){
			try {
				resourceInstances=RIService.getAllParentInstance();
			} catch (InstancelibException e) {
				LOG.error(e);
			}
		}else{
			Set<Long> domainManageIds=new HashSet<Long>(),commonDomainIds=new HashSet<Long>();

			saxDomains(user,domainManageIds,commonDomainIds,null);
			
			resourceInstances=getResourceInstances(user.getId(), domainManageIds, commonDomainIds);
		}
		List<ResourceInstanceBo> bos=toBos(user.getId(),resourceInstances);
		return filter(bos);
	}
	
	@Override
	public List<ResourceInstanceBo> getResources(ILoginUser user, Long domainId) {
		List<ResourceInstance> resourceInstances=null;
		if(user.isSystemUser()){
			try {
				HashSet<Long> domains=new HashSet<Long>();
				domains.add(domainId);
				resourceInstances=RIService.getParentResourceInstanceByDomainIds(domains);
			} catch (InstancelibException e) {
				LOG.error(e);
			}
		}else{
			Set<Long> domainManageIds=new HashSet<Long>(),commonDomainIds=new HashSet<Long>();

			Set<Long> dids=new HashSet<Long>();
			dids.add(domainId);
			saxDomains(user,domainManageIds,commonDomainIds,dids);
			
			resourceInstances=getResourceInstances(user.getId(), domainManageIds, commonDomainIds);
		}
		List<ResourceInstanceBo> bos=toBos(user.getId(),resourceInstances);
		return bos;
	}
	
	@Override
	public List<ResourceInstanceBo> getResources(ILoginUser user,
			List<Long> domainIds) {
		List<ResourceInstance> resourceInstances=null;
		if(user.isSystemUser()){
			try {
				resourceInstances=RIService.getParentResourceInstanceByDomainIds(new HashSet<Long>(domainIds));
			} catch (InstancelibException e) {
				LOG.error(e);
			}
		}else{
			Set<Long> domainManageIds=new HashSet<Long>(),commonDomainIds=new HashSet<Long>();
			
			saxDomains(user,domainManageIds,commonDomainIds,domainIds);
			
			resourceInstances=getResourceInstances(user.getId(), domainManageIds, commonDomainIds);
		}
		List<ResourceInstanceBo> bos=toBos(user.getId(),resourceInstances);
		return bos;
	}

	private void saxDomains(ILoginUser user,
			Set<Long> manageDomainIds,Set<Long> commonDomainIds,Collection<Long> domainIds){
		Set<IDomain> manageDomains=new HashSet<IDomain>();
		Set<IDomain> commonDomains=user.getCommonDomains();

		if(user.getDomainManageDomains()!=null)manageDomains.addAll(user.getDomainManageDomains());
		if(user.getManageDomains()!=null)manageDomains.addAll(user.getManageDomains());

		boolean flag=(domainIds!=null);
		for(IDomain domain:manageDomains){
			if(flag&&!domainIds.contains(domain.getId()))continue;
			manageDomainIds.add(domain.getId());
		}

		if(commonDomainIds!=null){
			for(IDomain domain:commonDomains){
				if(flag&&!domainIds.contains(domain.getId()))continue;
				commonDomainIds.add(domain.getId());
			}
		}
	}
	
	@Override
	public ResourceInstanceBo getResource(Long resourceId) {
		try {
			return toBo(null, RIService.getResourceInstance(resourceId));
		} catch (InstancelibException e) {
			LOG.error(e);
			return null;
		}
	}
	
	@Override
	public CategoryBo getTreeCategory() {
		CategoryDef root=capacityService.getRootCategory();
		return parseCategory(root);
	}

	
	@Override
	public CategoryBo getTreeCategory(String categoryId) {
		CategoryBo categoryBo=categories.get(categoryId);
		if(categoryBo==null){
			categoryBo=parseCategory(capacityService.getCategoryById(categoryId));
		}
		return categoryBo;
	}

	@Override
	public List<ResourceInstanceBo> getResources(ResourceQueryBo queryBo) {
		List<ResourceInstanceBo> resourceInstanceBos=null;
		if(queryBo.getDomainIds()==null){
			resourceInstanceBos=getResources(queryBo.getUser());
		}else{
			resourceInstanceBos=getResources(queryBo.getUser(),queryBo.getDomainIds());
		}

		Set<String> categoryIds=null;
		if(queryBo.getCategoryIds()!=null){
			categoryIds=new HashSet<String>();
			CategoryBo categoryBo;
			for(String categoryId:queryBo.getCategoryIds()){
				categoryIds.add(categoryId);
				categoryBo=getTreeCategory(categoryId);
				if(categoryBo!=null){
					categoryIds.addAll(categoryBo.getSubCategoryIds());
				}
			}
		}
		
		//过滤
		List<ResourceInstanceBo> temp=new ArrayList<ResourceInstanceBo>();

		Filter filter=queryBo.getFilter();
		
		boolean categoryIdFlag=(categoryIds!=null),moduleIdFlag=(queryBo.getModuleId()!=null),
				nameFlag=(queryBo.getInstanceName()!=null),ipFlag=(queryBo.getInstanceIp()!=null),
				filterFlag=(filter!=null),discoverWayEnumsFlag=queryBo.getDiscoverWayEnums()!=null,
				lifeStateFlag=queryBo.getLifeState()!=null;
		String moduleId=queryBo.getModuleId(),name=queryBo.getInstanceName(),ip=queryBo.getInstanceIp();
		Collection<DiscoverWayEnum> discoverWayEnums=queryBo.getDiscoverWayEnums();
		InstanceLifeStateEnum lifeState=queryBo.getLifeState();
		
		ResourceInstanceBo bo;
		for(int i=0,len=resourceInstanceBos.size();i<len;i++){
			bo=resourceInstanceBos.get(i);
			
			if(categoryIdFlag&&((!categoryIds.contains(bo.getCategoryId())))){
				if(!categoryIds.contains(bo.getResourceId())){
					continue;
				}
			};
			
			if(lifeStateFlag&&bo.getLifeState()!=lifeState)continue;
			
			if(nameFlag&&!name.equals(bo.getShowName()))continue;
			
			if(ipFlag&&!ip.equals(bo.getDiscoverIP()))continue;
			
			if(moduleIdFlag&&!moduleId.equals(bo.getResourceId()))continue;
			
			if(discoverWayEnumsFlag&&!discoverWayEnums.contains(bo.getDiscoverWay()))continue;
			
			if(filterFlag&&!filter.filter(bo))continue;
			
			temp.add(bo);
		}
		
		return temp;
	}
	
	@Override
	public List<ResourceInstanceBo> getResource(List<Long> resourceIds) {
		List<ResourceInstance> resourceInstances;
		try {
			resourceInstances = RIService.getResourceInstances(resourceIds);
			return toBos(null,resourceInstances);
		} catch (InstancelibException e) {
			LOG.error(e);
		}
		return emptyData;
	}

	@Override
	public List<Long> accessFilter(ResourceQueryBo queryBo,Collection<Long> parentResourceIds) {
		List<ResourceInstanceBo> riBos=getResources(queryBo);
		Set<Long> ids=new HashSet<Long>();
		for(int i=0,len=riBos.size();i<len;i++){
			ids.add(riBos.get(i).getId());
		}
		
		List<Long> allowIds=new ArrayList<Long>();
		for(Long id:parentResourceIds){
			if(ids.contains(id))allowIds.add(id);
		}
		return allowIds;
	}

	@Override
	public List<ResourceModuleBo> getModules(String categoryId) {
		CategoryDef categoryDef=capacityService.getCategoryById(categoryId);
		List<ResourceModuleBo> moduleBos =new ArrayList<ResourceModuleBo>();
		parseModule(categoryDef, moduleBos);
		return moduleBos;
	}
	
	private List<ResourceInstanceBo> toBos(Long userId,List<ResourceInstance> source){
		List<ResourceInstanceBo> ribs=new ArrayList<ResourceInstanceBo>();
		if(source==null)return ribs;
		ResourceInstance r;
		for(int i=0,len=source.size();i<len;i++){
			r=source.get(i);
			ribs.add(toBo(userId, r));
		}
		return ribs;
	}

	private ResourceInstanceBo toBo(Long userId,ResourceInstance source){
		ResourceInstanceBo rBo=new ResourceInstanceBo();
		rBo.setUserId(userId);
		if(source==null)return rBo;
		rBo.setCategoryId(source.getCategoryId());
		rBo.setDiscoverIP(source.getShowIP());
		rBo.setId(source.getId());
		rBo.setName(source.getName());
		rBo.setResourceId(source.getResourceId());
		rBo.setShowName(source.getShowName());
		rBo.setDiscoverNode(source.getDiscoverNode());
		rBo.setDiscoverWay(source.getDiscoverWay());
		rBo.setDomainId(source.getDomainId());
		rBo.setLifeState(source.getLifeState());
		rBo.setParentId(source.getParentId());
		rBo.setSrc(source);
		return rBo;
	}
	
	/**
	 * <pre>
	 * 根据用户与资源及资源组关系获取资源实例信息
	 * </pre>
	 * @return
	 */
	private List<ResourceInstance> getResourceInstanceByUserResourceRel(List<UserResourceRel> urrs){
		List<Long> resourceIds=new ArrayList<Long>();
		UserResourceRel urr;
		for(int i=0,len=urrs.size();i<len;i++){
			urr=urrs.get(i);
			if(urr.getType()==UserResourceRel.TYPE_RESOURCE){
				resourceIds.add(urr.getResourceId());
			}else{
				DomainResourceGroupRel drgr=resourceGroupApi.getResourceGroupRel(urr.getResourceId());
				drgr.copyResInsIdsToList(resourceIds);
			}
		}
		try {
			List<ResourceInstance> ris=RIService.getResourceInstances(resourceIds);
			return ris;
		} catch (InstancelibException e) {
			throw new OriginalException(e);
		}
	}

	//键为资源类型id
	Map<String, CategoryBo> categories=new HashMap<String, CategoryBo>();

	//键为资源类型id 值为资源类型名称
	Map<String, String> categoriesMapper=new HashMap<String, String>();

	@Override
	public String[] getAllCategoryParents(String categoryId) {
		String arr=this.parseAllCategoryParents(categoryId);
		if(arr==null){
			LOG.error("getAllCategoryParents,传入的资源类型："+categoryId);
			return null;
		}
		return arr.split(",");
	}
	
	private String parseAllCategoryParents(String categoryId){
		String parentCategoryId=categoriesAllParents.get(categoryId);
		if(parentCategoryId==null){
			return categoryId;
		}else{
			return categoryId+','+parseAllCategoryParents(parentCategoryId);
		}
	}
	
	@Override
	public String[] getCategoryParents(String categoryId) {
		String arr=this.parseCategoryParents(categoryId);
		if(arr==null){
			LOG.error("getCategoryParents,传入的资源类型："+categoryId);
			return null;
		}
		return arr.split(",");
	}
	
	private String parseCategoryParents(String categoryId){
		String parentCategoryId=categoriesParents.get(categoryId);
		if(parentCategoryId==null){
			return categoryId;
		}else{
			return categoryId+','+parseCategoryParents(parentCategoryId);
		}
	}
	
	@PostConstruct
	public void init(){
		getTreeCategory();
	}


	//键为资源类型id 值为父资源类型ID
	private static final Map<String, String> categoriesParents=new HashMap<String, String>();

	//键为资源类型id 值为父资源类型ID
	private static final Map<String, String> categoriesAllParents=new HashMap<String, String>();

	private CategoryBo parseCategory(CategoryDef category){
		if((category==null))return null;
		CategoryBo categoryBo=new CategoryBo();
		categoryBo.setId(category.getId());
		categoryBo.setName(category.getName());
		categoryBo.setPid(category.getParentCategory()==null?null:category.getParentCategory().getId());
		
		categoryBo.addSubCategoryId(categoryBo.getId());

		int i,len;
		
		//解析模型
//		ResourceDef[] resourceDefs=category.getResourceDefs();
//		if(resourceDefs!=null){
//			for(i=0,len=resourceDefs.length;i<len;i++){
//				categoryBo.addSubModuleId(resourceDefs[i].getId());
//			}
//		}
		
		//解析子资源类型
		CategoryDef[] categoryDefs=category.getChildCategorys();
		if(categoryDefs!=null){
			CategoryBo subCategoryBo;
			CategoryBo[] categoryBos=new CategoryBo[len=categoryDefs.length],temp;
			int j=0;
			for(i=0;i<len;i++){
				subCategoryBo=parseCategory(categoryDefs[i]);
				if(subCategoryBo==null)continue;
				categoryBo.addSubCategoryId(subCategoryBo.getId());
				categoryBo.addSubCategoryId(subCategoryBo.getSubCategoryIds());
//				categoryBo.addSubModuleId(subCategoryBo.getSubModuleIds());
				if(category.isDisplay()){
					categoriesParents.put(subCategoryBo.getId(),categoryBo.getId());
				}
				categoriesAllParents.put(subCategoryBo.getId(),categoryBo.getId());
				categoryBos[j++]=subCategoryBo;
			}
			temp=new CategoryBo[j];
			for(j--;j>-1;j--){
				temp[j]=categoryBos[j];
			}
			categoryBo.setChildren(temp);
		}
		categories.put(categoryBo.getId(),categoryBo);
		categoriesMapper.put(categoryBo.getId(), categoryBo.getName());
		return categoryBo;
	}
	
	private void parseModule(CategoryDef category,List<ResourceModuleBo> target){
		int i,len;
		
		//解析模型
		ResourceDef[] resourceDefs=category.getResourceDefs();
		if(resourceDefs!=null){
			ResourceModuleBo moduleBo;
			ResourceDef resourceDef;
			for(i=0,len=resourceDefs.length;i<len;i++){
				resourceDef=resourceDefs[i];
				moduleBo=new ResourceModuleBo();
				moduleBo.setId(resourceDef.getId());
				moduleBo.setName(resourceDef.getName());
				target.add(moduleBo);
			}
		}
		
		//解析子资源类型
		CategoryDef[] categoryDefs=category.getChildCategorys();
		if(categoryDefs!=null){
			CategoryDef categoryDef;
			for(i=0,len=categoryDefs.length;i<len;i++){
				categoryDef=categoryDefs[i];
				parseModule(categoryDef, target);
			}
		}
	}

	private List<ResourceInstance> getResourceInstances(Long userId,
			Collection<Long> domainManageIds,Collection<Long> commonDomainIds){
		List<ResourceInstance> resourceInstances=new ArrayList<ResourceInstance>();
		
		if(domainManageIds!=null){
			try {
				List<ResourceInstance> resultInstances = RIService.getParentResourceInstanceByDomainIds(new HashSet<Long>(domainManageIds));
				if(resultInstances!=null){
					resourceInstances.addAll(resultInstances);
				}
			} catch (InstancelibException e) {
				LOG.error(e);
			}
		}
		
		if(commonDomainIds!=null){
			List<ResourceInstance> resultInstances = getResourceInstanceByUserResourceRel(userApi.getUserResources(userId, commonDomainIds));
			if(resultInstances!=null){
				resourceInstances.addAll(resultInstances);
			}
		}
		return resourceInstances;
	}
	
	private List<ResourceInstanceBo> filter(List<ResourceInstanceBo> source){
		return new ArrayList<ResourceInstanceBo>(new HashSet<ResourceInstanceBo>(source));
	}

	@Override
	public Map<String, String> getCategoryMapper() {
		return categoriesMapper;
	}
}


