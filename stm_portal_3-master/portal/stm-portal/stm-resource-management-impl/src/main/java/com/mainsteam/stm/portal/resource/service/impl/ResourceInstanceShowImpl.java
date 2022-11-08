package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.resource.api.ResourceInstanceShowApi;
import com.mainsteam.stm.portal.resource.bo.ResourceInstancePageBo;

public class ResourceInstanceShowImpl implements ResourceInstanceShowApi {

	private static final Log logger = LogFactory.getLog(ResourceInstanceShowImpl.class);
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Override
	public ResourceInstancePageBo pageSelect(long startRecord, long pageSize,
			ResourceInstance resourceInstance) {
		
		List<ResourceInstance> resourceAllList = null;
		
		if(resourceInstance.getCategoryId() != null && !resourceInstance.getCategoryId().trim().equals("")){
			
			//根据类别ID查询资源实例列表
			resourceAllList = this.getPageDataByCategroyId(startRecord, pageSize, resourceInstance.getCategoryId());
		}else{
			
			//根据多个资源实例ID查询资源实例
			resourceAllList = this.getPageDataByResourceIds(startRecord, pageSize, resourceInstance.getResourceId());
			
		}
		
		ResourceInstancePageBo pageBo = new ResourceInstancePageBo();
		if(resourceAllList == null || resourceAllList.size() <= 0){
			
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(0);
			pageBo.setRowCount(pageSize);
			pageBo.setResources(null);
			
		}else{
			
			
			pageBo.setStartRow(startRecord);
			pageBo.setTotalRecord(resourceAllList.size());
			pageBo.setRowCount(pageSize);
			
			List<ResourceInstance> resourceList = new ArrayList<ResourceInstance>();
			
			if((pageSize + startRecord) > resourceAllList.size()){
				resourceList = resourceAllList.subList((int)startRecord, resourceAllList.size());
			}else{
				resourceList = resourceAllList.subList((int)startRecord, (int)(pageSize + startRecord));
			}
			
			pageBo.setResources(resourceList);
			
		}
		
		return pageBo;
		
	}
	
	private List<ResourceInstance> getPageDataByCategroyId(long startRecord, long pageSize,String categoryId){
		
		List<ResourceInstance> resourceAllList = null;
		try {
			resourceAllList = resourceInstanceService.getParentInstanceByCategoryId(categoryId);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}
		
		return resourceAllList;
		
	}
	
	private List<ResourceInstance> getPageDataByResourceIds(long startRecord, long pageSize,String resourceIds){
		
		String[] resourceIdArray = resourceIds.split(",");
		
		List<ResourceInstance> resourceAllList = new ArrayList<ResourceInstance>();
		
		for(int i = 0 ; i < resourceIdArray.length ; i ++){
			
			if(resourceIdArray[i].trim().equals("")){
				continue;
			}
			long resourceInstanceId = Long.parseLong(resourceIdArray[i]);
			
			ResourceInstance resourceInstance = null;
			try {
				resourceInstance = resourceInstanceService.getResourceInstance(resourceInstanceId);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			
			resourceAllList.add(resourceInstance);
			
		}
		
		return resourceAllList;
		
	}

}
