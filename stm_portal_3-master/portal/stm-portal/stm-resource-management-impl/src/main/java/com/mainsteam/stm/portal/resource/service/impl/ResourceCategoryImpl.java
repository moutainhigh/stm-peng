package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;
import com.mainsteam.stm.portal.resource.bo.ResourceDefBo;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;

/**
 * <li>文件名称: ResourceCategoryApi.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有:版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li> 
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月28日
 * @author pengl
 */
public class ResourceCategoryImpl implements ResourceCategoryApi {

	private static final Log logger = LogFactory.getLog(ResourceCategoryImpl.class);
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IResourceApi stm_system_resourceApi;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	/**
	 * 获取所有一级资源类别
	 * @return
	 */
	@Override
	public List<ResourceCategoryBo> getFirstStageResourceCategoryList() {

		// 调用能力库接口获取根资源类别
		CategoryDef categoryDef = capacityService.getRootCategory();

		// 获取一级资源类别
		CategoryDef[] categoryDefs = categoryDef.getChildCategorys();

		List<ResourceCategoryBo> categoryBoOneLevelList = new ArrayList<ResourceCategoryBo>();

		for (CategoryDef singleCategory : categoryDefs) {

			if(!singleCategory.isDisplay()){
				continue;
			}
			
			if(!licenseCapacityCategory.isAllowCategory(singleCategory.getId())){
				continue;
			}
			
			ResourceCategoryBo categoryBo = this
					.categoryDefToResourceCategoryBo(singleCategory);

			// 获取二级资源类别
			CategoryDef[] categoryDefs_secondLevel = singleCategory
					.getChildCategorys();

			if (categoryDefs_secondLevel == null
					|| categoryDefs_secondLevel.length <= 0) {
				categoryBo.setChildCategorys(null);
			} else {

				List<ResourceCategoryBo> categoryBoTwoLevelList = new ArrayList<ResourceCategoryBo>();

				for (CategoryDef lastCategory : categoryDefs_secondLevel) {

					ResourceCategoryBo categoryVo_2 = this
							.categoryDefToResourceCategoryBo(lastCategory);

					categoryBoTwoLevelList.add(categoryVo_2);

				}

				categoryBo.setChildCategorys(categoryBoTwoLevelList);

			}

			categoryBoOneLevelList.add(categoryBo);

		}

		return categoryBoOneLevelList;

	}
	
	private ResourceCategoryBo createAllCategoryBo(CategoryDef categoryDef, List<String> requiredCategory){
		if(!categoryDef.isDisplay() && !requiredCategory.contains(categoryDef.getId())){
			return null;
		}
		if(!licenseCapacityCategory.isAllowCategory(categoryDef.getId())){
			return null;
		}
		ResourceCategoryBo rcb = new ResourceCategoryBo();
		rcb.setId(categoryDef.getId());
		rcb.setName(categoryDef.getName());
		rcb.setResourceNumber(0);
		List<ResourceCategoryBo> childRcbList = new ArrayList<ResourceCategoryBo>();
		if(categoryDef.getChildCategorys() != null){
			for(int i = 0; i < categoryDef.getChildCategorys().length; i ++){
				CategoryDef childCategoryDef = categoryDef.getChildCategorys()[i];
				ResourceCategoryBo childRcb = createAllCategoryBo(childCategoryDef, requiredCategory);
				if(childRcb != null)
					childRcbList.add(childRcb);
			}
		}
		rcb.setChildCategorys(childRcbList);
		return rcb;
	}
	
	private int countResCategory(ResourceCategoryBo rcb, ResourceInstanceBo rib) {
		if (rcb.getId().equals(rib.getCategoryId())) {
			rcb.setResourceNumber(rcb.getResourceNumber() + 1);
		} else {
			int amountCount = 0;
			for (int i = 0; i < rcb.getChildCategorys().size(); i++) {
				amountCount += countResCategory(rcb.getChildCategorys().get(i), rib);
			}
			if(amountCount > 0){
				rcb.setResourceNumber(amountCount);
			}
		}
		return rcb.getResourceNumber();
	}
	
	/**
	 * 获取含有资源的一级资源类别
	 * @return
	 */

	public List<ResourceCategoryBo> getResourceCategoryList(ILoginUser user) throws Exception {
		List<ResourceInstanceBo> userResourceList = stm_system_resourceApi.getResources(user);
		return getResourceCategoryListByResources(userResourceList, new ArrayList<String>());
	}
	
	public List<ResourceCategoryBo> getResourceCategoryListByResources(List<ResourceInstanceBo> userResourceList, List<String> requiredCategory) {
		// 调用能力库接口获取根资源类别
		ResourceCategoryBo mainRcb = createAllCategoryBo(capacityService.getRootCategory(), requiredCategory);
		for (int i = 0; i < userResourceList.size(); i++) {
			ResourceInstanceBo userResource = userResourceList.get(i);
			if(InstanceLifeStateEnum.MONITORED.equals(userResource.getLifeState())
					|| InstanceLifeStateEnum.NOT_MONITORED.equals(userResource.getLifeState())){
				countResCategory(mainRcb, userResource);
			}
		}
		// 过滤没有资源的类型
		List<ResourceCategoryBo> childRcbList = new ArrayList<ResourceCategoryBo>();
		for(ResourceCategoryBo childRcb : mainRcb.getChildCategorys()){
			if(childRcb.getResourceNumber() > 0){
				childRcbList.add(childRcb);
			}
		}
		return childRcbList;
	}
	
	@Override
	public List<ResourceInstanceBo> getAllResourceInstanceList(ILoginUser user) {
		
		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		List<ResourceInstanceBo> result = new ArrayList<ResourceInstanceBo>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user);
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			if(logger.isErrorEnabled()){
				logger.error("Get getAllParentInstance error");
			}
			return null;
		}
		
		for(ResourceInstanceBo bo : resourceInstanceReultList){
			if(bo.getLifeState().equals(InstanceLifeStateEnum.MONITORED) || bo.getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED)){
				result.add(bo);
			}
		}
		
		return result;
	}
	
	@Override
	public List<ResourceInstanceBo> getAllResourceInstanceList(long domainId,ILoginUser user) {
		
		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		List<ResourceInstanceBo> result = new ArrayList<ResourceInstanceBo>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user,domainId);
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			if(logger.isErrorEnabled()){
				logger.error("Get getAllParentInstance error");
			}
			return null;
		}
		
		for(ResourceInstanceBo bo : resourceInstanceReultList){
			if(bo.getLifeState().equals(InstanceLifeStateEnum.MONITORED) || bo.getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED)){
				result.add(bo);
			}
		}

		return result;
	}
	
	@Override
	public List<ResourceInstanceBo> getResourceInstanceListByIds(String ids) {
		
		List<ResourceInstanceBo> resourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(ids == null || ids.trim().equals("")){
			return resourceInstanceList;
		}
		String idArray[] = ids.split(",");
		
		for(int i = 0 ; i < idArray.length ; i ++){

			resourceInstanceList.add(stm_system_resourceApi.getResource(Long.parseLong(idArray[i])));
			
		}

		return resourceInstanceList;
	}

	@Override
	public List<ResourceInstanceBo> getExceptResourceInstanceListByIds(String ids,ILoginUser user) {

		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user);
		
		String idArray[] = ids.split(",");
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			return resultResourceInstanceList;
		}
		
		listFor : for(int j = 0 ; j < resourceInstanceReultList.size() ; j ++){
			
			if(!(resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)
					|| resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED))){
				continue;
			}
			
			for(int i = 0 ; i < idArray.length ; i ++){
				
				long idArrayItem = -1L;
				try {
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					continue;
				}
				
				if(idArrayItem == resourceInstanceReultList.get(j).getId()){
					
					continue listFor;
					
				}
				
			}
				
			resultResourceInstanceList.add(resourceInstanceReultList.get(j));
			
		}
			

		return resultResourceInstanceList;
	}
	
	@Override
	public List<ResourceInstanceBo> getExceptResourceInstanceListByIds(String ids,long domainId,ILoginUser user) {

		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user,domainId);
		
		String idArray[] = ids.split(",");
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			return resultResourceInstanceList;
		}
		
		listFor : for(int j = 0 ; j < resourceInstanceReultList.size() ; j ++){
			
			if(!(resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)
					|| resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED))){
				continue;
			}
			
			for(int i = 0 ; i < idArray.length ; i ++){
				
				long idArrayItem = -1L;
				try {
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					continue;
				}
				
				if(idArrayItem == resourceInstanceReultList.get(j).getId()){
					
					continue listFor;
					
				}
				
			}
				
			resultResourceInstanceList.add(resourceInstanceReultList.get(j));
			
		}
			

		return resultResourceInstanceList;
	}
	
	//搜索左边资源实例
	@Override
	public List<ResourceInstanceBo> getExceptResourceInstanceListByIdsAndSearchContent(String ids,ILoginUser user,String searchContent) {

		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user);
		
		String idArray[] = ids.split(",");
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			return resultResourceInstanceList;
		}
		
		listFor : for(int j = 0 ; j < resourceInstanceReultList.size() ; j ++){
			
			if(!(resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)
					|| resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED))){
				continue;
			}
			
			for(int i = 0 ; i < idArray.length ; i ++){
				
				long idArrayItem = -1L;
				try {
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					continue;
				}
				
				if(idArrayItem == resourceInstanceReultList.get(j).getId()){
					
					continue listFor;
					
				}
				
			}
			
			if(searchContent != null && !searchContent.trim().equals("")){
				searchContent = searchContent.trim();
				if((resourceInstanceReultList.get(j).getShowName() != null && resourceInstanceReultList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())) ||
						(resourceInstanceReultList.get(j).getDiscoverIP() != null && resourceInstanceReultList.get(j).getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase()))){
					resultResourceInstanceList.add(resourceInstanceReultList.get(j));
				}
			}else{
				//没有搜索关键字，查出所有
				resultResourceInstanceList.add(resourceInstanceReultList.get(j));
			}
			
			
		}
			

		return resultResourceInstanceList;
	}
	
	//搜索左边资源实例
	@Override
	public List<ResourceInstanceBo> getNewExceptResourceInstanceListByIdsAndSearchContent(
			String ids, ILoginUser user, String searchContent, int startNum,
			int pageSize) {
		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user);
		
		String[] idArray = ids.split(",");
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		List<ResourceInstanceBo> checkResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		List<ResourceInstanceBo> noCheckResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			return resultResourceInstanceList;
		}
		
		for(int j = 0 ; j < resourceInstanceReultList.size() ; j ++){
			
			if(!(resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)
					|| resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED))){
				continue;
			}
			
			if(searchContent != null && !searchContent.trim().equals("")){
				searchContent = searchContent.trim();
				if((resourceInstanceReultList.get(j).getShowName() != null && resourceInstanceReultList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())) ||
						(resourceInstanceReultList.get(j).getDiscoverIP() != null && resourceInstanceReultList.get(j).getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase()))){
					boolean isEqual = false;
					for(int i = 0 ; i < idArray.length ; ++i){
						long idArrayItem = -1L;
						try {
							idArrayItem = Long.parseLong(idArray[i]);
						} catch (NumberFormatException e) {
							if(logger.isErrorEnabled()){
								logger.error(e.getMessage(),e);
							}
							continue;
						}
						if(idArrayItem == resourceInstanceReultList.get(j).getId()){
							checkResourceInstanceList.add(resourceInstanceReultList.get(j));
							isEqual = true;
						}
					}
					if(!isEqual){
						noCheckResourceInstanceList.add(resourceInstanceReultList.get(j));
					}
				}
			}else{
				boolean isEqual = false;
				for(int i = 0 ; i < idArray.length ; ++i){
					long idArrayItem = -1L;
					try {
						idArrayItem = Long.parseLong(idArray[i]);
					} catch (NumberFormatException e) {
						if(logger.isErrorEnabled()){
							logger.error(e.getMessage(),e);
						}
						continue;
					}
					if(idArrayItem == resourceInstanceReultList.get(j).getId()){
						checkResourceInstanceList.add(resourceInstanceReultList.get(j));
						isEqual = true;
					}
				}
				if(!isEqual){
					noCheckResourceInstanceList.add(resourceInstanceReultList.get(j));
				}
			}
		}
		for(int i = 0 ; i < noCheckResourceInstanceList.size(); ++i){
			checkResourceInstanceList.add(noCheckResourceInstanceList.get(i));
		}
		resultResourceInstanceList = checkResourceInstanceList;
		
		
		return resultResourceInstanceList;
	}
	
	//搜索左边资源实例(系统管理)
	@Override
	public List<ResourceInstanceBo> getExceptResourceInstanceListByIdsAndSearchContent(String ids,long domainId,ILoginUser user,String searchContent) {

		List<ResourceInstanceBo> resourceInstanceReultList = new ArrayList<>();
		resourceInstanceReultList = stm_system_resourceApi.getResources(user,domainId);
		
		String idArray[] = ids.split(",");
		
		List<ResourceInstanceBo> resultResourceInstanceList = new ArrayList<ResourceInstanceBo>();
		
		if(resourceInstanceReultList == null || resourceInstanceReultList.size() <= 0){
			return resultResourceInstanceList;
		}
		
		listFor : for(int j = 0 ; j < resourceInstanceReultList.size() ; j ++){
			
			if(!(resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.MONITORED)
					|| resourceInstanceReultList.get(j).getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED))){
				continue;
			}
			
			for(int i = 0 ; i < idArray.length ; i ++){
				
				long idArrayItem = -1L;
				try {
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					if(logger.isErrorEnabled()){
						logger.error(e.getMessage(),e);
					}
					continue;
				}
				
				if(idArrayItem == resourceInstanceReultList.get(j).getId()){
					
					continue listFor;
					
				}
				
			}
				
			if(searchContent != null && !searchContent.trim().equals("")){
				searchContent = searchContent.trim();
				if((resourceInstanceReultList.get(j).getShowName() != null && resourceInstanceReultList.get(j).getShowName().toLowerCase().contains(searchContent.toLowerCase())) ||
						(resourceInstanceReultList.get(j).getDiscoverIP() != null && resourceInstanceReultList.get(j).getDiscoverIP().toLowerCase().contains(searchContent.toLowerCase()))){
					resultResourceInstanceList.add(resourceInstanceReultList.get(j));
				}
			}else{
				//没有搜索关键字，查出所有
				resultResourceInstanceList.add(resourceInstanceReultList.get(j));
			}
			
		}
			

		return resultResourceInstanceList;
	}
	
	private ResourceCategoryBo categoryDefToResourceCategoryBo(CategoryDef categoryDef){
		
		ResourceCategoryBo categoryBo = new ResourceCategoryBo();
		categoryBo.setId(categoryDef.getId());
		categoryBo.setName(categoryDef.getName());
		
		return categoryBo;
		
	}
	
	private ResourceDefBo resourceDefToResourceDefBo(ResourceDef resourceDef){

		ResourceDefBo resourceBo = new ResourceDefBo();
		resourceBo.setId(resourceDef.getId());
		resourceBo.setName(resourceDef.getName());
		resourceBo.setCategory(this.categoryDefToResourceCategoryBo(resourceDef.getCategory()));
		
		return resourceBo;
		
	}

}
