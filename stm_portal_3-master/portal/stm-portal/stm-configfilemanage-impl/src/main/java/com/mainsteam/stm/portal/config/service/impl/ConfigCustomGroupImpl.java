package com.mainsteam.stm.portal.config.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.config.api.IConfigCustomGroupApi;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupBo;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupResourceBo;
import com.mainsteam.stm.portal.config.bo.ResourceCategoryBo;
import com.mainsteam.stm.portal.config.dao.IConfigCustomGroupDao;
import com.mainsteam.stm.portal.config.dao.IConfigDeviceDao;

public class ConfigCustomGroupImpl implements IConfigCustomGroupApi{
	private static Logger logger = Logger.getLogger(ConfigCustomGroupImpl.class);
	private IConfigCustomGroupDao configCustomGroupDao;
	private IConfigDeviceDao configDeviceDao;
	private ISequence seq;
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Override
	public void selectByPage(Page<ConfigCustomGroupBo, ConfigCustomGroupBo> page) throws Exception{
		configCustomGroupDao.selectByPage(page);
	} 
	@Override
	public int moveIntoGroup(long[] groupIds, long[] resourceInstanceIds) throws Exception{
		int count = 0;
		List<ConfigCustomGroupResourceBo> poList=new ArrayList<ConfigCustomGroupResourceBo>();
		for(Long groupId: groupIds){
			List<String> resourceIDs = configCustomGroupDao.getGroupResourceIdsByGroup(groupId);
			if(resourceIDs.size()==0 || null==resourceIDs){
				for(Long resourceId: resourceInstanceIds){
					ConfigCustomGroupResourceBo cgrPo=new ConfigCustomGroupResourceBo();
					cgrPo.setGroupId(groupId);
					cgrPo.setResourceID(String.valueOf(resourceId));
					poList.add(cgrPo);
				}
			}else{
				for(Long newResourceId: resourceInstanceIds){
					boolean flag = true;
					for(String oldResourceId:resourceIDs){
						if(oldResourceId.equals(String.valueOf(newResourceId))){
							flag = false;
							break;
						}
					}
					if(flag){
						ConfigCustomGroupResourceBo cgrPo=new ConfigCustomGroupResourceBo();
						cgrPo.setGroupId(groupId);
						cgrPo.setResourceID(String.valueOf(newResourceId));
						poList.add(cgrPo);
					}
				}
			}
		}
		count = configCustomGroupDao.batchInsert(poList);
		return count;
	}
	@Override
	public int insert(ConfigCustomGroupBo bo) throws Exception{
		//获取主键
		long groupId=seq.next();
		bo.setId(groupId);
		bo.setEntryDateTime(new Date());
		List<ConfigCustomGroupBo> existDatas = configCustomGroupDao.checkGroupNameIsExsit(bo.getName());
		if(existDatas!=null&&existDatas.size()>0){
			return -1;
		}
		//存储Group
		int count = configCustomGroupDao.insert(bo);
		List<String> resourceInstances=bo.getResourceInstanceIds();
		List<ConfigCustomGroupResourceBo> poList=new ArrayList<ConfigCustomGroupResourceBo>();
		for(String id:resourceInstances){
			ConfigCustomGroupResourceBo cgrPo=new ConfigCustomGroupResourceBo();
			cgrPo.setGroupId(groupId);
			cgrPo.setResourceID(id);
			poList.add(cgrPo);
		}
		//存储Group 绑定的resource
		configCustomGroupDao.batchInsert(poList);
		if(count > 0){
			return (int)groupId;
		}else{
			return 0;
		}
	}
	
	@Override
	public int del(long id) throws Exception{
		//删除对应关系
		int count = configCustomGroupDao.deleteGroupByGroup(id);
		//删除资源组
		configCustomGroupDao.deleteResourceIDsByGroup(id);
		return count;
	}
	@Override
	public int update(ConfigCustomGroupBo customGroupBo) throws Exception{
		List<ConfigCustomGroupBo> existDatas = configCustomGroupDao.checkGroupNameIsExsit(customGroupBo.getName());
		if(existDatas!=null&&existDatas.size()>0&&!(existDatas.get(0).getId().equals(customGroupBo.getId()))){
			return -1;
		}
		int count = configCustomGroupDao.updateGroup(customGroupBo);
		//在查找与之对应的资源ID列表，若没有改变，则不更新数据库
		List<String> resourceIDs = configCustomGroupDao.getGroupResourceIdsByGroup(customGroupBo.getId());
		Set<String> newResourceIDs = new HashSet<String>(resourceIDs);
		Set<String> oldResourceIDs = new HashSet<String>(customGroupBo.getResourceInstanceIds());
		if(!newResourceIDs.equals(oldResourceIDs)){
			logger.info("Update custom resource and group relationship!");
			//更新资源组对应的资源ID
			//1.先删除旧对应关系
			configCustomGroupDao.deleteResourceIDsByGroup(customGroupBo.getId());
			//2.插入新的对应关系
			List<ConfigCustomGroupResourceBo> poList=new ArrayList<ConfigCustomGroupResourceBo>();
			for(String id : customGroupBo.getResourceInstanceIds()){
				ConfigCustomGroupResourceBo cgrPo=new ConfigCustomGroupResourceBo();
				cgrPo.setGroupId(customGroupBo.getId());
				cgrPo.setResourceID(id);
				poList.add(cgrPo);
			}
			configCustomGroupDao.batchInsert(poList);
			
		}
		return count;
	}
	@Override
	public List<ConfigCustomGroupBo> getList(long userId) throws Exception{
		List<ConfigCustomGroupBo> boList = configCustomGroupDao.getList(userId);
		for(ConfigCustomGroupBo bo : boList){
			List<String> resourceInstanceIds = configCustomGroupDao.getGroupResourceIdsByGroup(bo.getId());
			List<String> ids = new ArrayList<String>();
			if(null!=resourceInstanceIds&&resourceInstanceIds.size()>0){
				for(String s : resourceInstanceIds){
					ResourceInstance instance = resourceInstanceService.getResourceInstance(Long.parseLong(s));
					if(instance==null) continue;
					ids.add(s);
				}
			}
			bo.setResourceInstanceIds(ids);
		}
		return boList;
	}
	
	@Override
	public int deleteGroupAndResourceRelation(long[] ids) throws Exception{
		//删除对应关系
		int result = configCustomGroupDao.deleteGroupAndResourceRelationById(ids);
		//查询目前没有对应关系的配置组
		List<Long> idList = configCustomGroupDao.selectResourceNumberIsZeroGroup();
		if(idList != null && idList.size() > 0){
			for(long resourceId : idList){
				configCustomGroupDao.deleteGroupByGroup(resourceId);
			}
		}
		return result;
	}
	
	@Override
	public int deleteResourceFromCustomGroup(ConfigCustomGroupBo groupBo) throws Exception{
		int count = configCustomGroupDao.deleteResourceFromCustomGroupByIds(groupBo);
		return count;
	}

	public IConfigCustomGroupDao getConfigCustomGroupDao() {
		return configCustomGroupDao;
	}

	public void setConfigCustomGroupDao(IConfigCustomGroupDao configCustomGroupDao) {
		this.configCustomGroupDao = configCustomGroupDao;
	}

	public ISequence getSeq() {
		return seq;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	
	/**
	 * 获取单个用户自定义配置组
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public ConfigCustomGroupBo getCustomGroup(long id) {
		return configCustomGroupDao.getCustomGroup(id);
	}
	
	@Override
	public List<ResourceInstance> getAllResourceInstanceList() {
		List<ResourceInstance> resourceInstanceList = new ArrayList<ResourceInstance>();
		try {
			List<Long> resourceIds = configDeviceDao.getAllResourceIds();
			for(Long id: resourceIds){
				if(null!=resourceInstanceService.getResourceInstance(id)){
					resourceInstanceList.add(resourceInstanceService.getResourceInstance(id));
				}
			}
//			resourceInstanceList = resourceInstanceService.getParentInstanceByCategoryId("Switch");
//			resourceInstanceList = resourceInstanceService.getAllParentInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return resourceInstanceList;
	}
	
	@Override
	public List<ResourceInstance> getResourceInstanceListByIds(String ids) {
		String idArray[] = ids.split(",");
		List<ResourceInstance> resourceInstanceList = new ArrayList<ResourceInstance>();
		for(int i = 0 ; i < idArray.length ; i ++){
			try {
				resourceInstanceList.add(resourceInstanceService.getResourceInstance(Long.parseLong(idArray[i])));
			} catch (NumberFormatException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return resourceInstanceList;
	}

	@Override
	public List<ResourceInstance> getExceptResourceInstanceListByIds(String ids) {
		List<ResourceInstance> resourceInstanceList = new ArrayList<ResourceInstance>();
		try {
			List<Long> resourceIds = configDeviceDao.getAllResourceIds();
			for(Long id: resourceIds){
				if(null!=resourceInstanceService.getResourceInstance(id)){
					resourceInstanceList.add(resourceInstanceService.getResourceInstance(id));
				}
			}
//			resourceInstanceList = resourceInstanceService.getParentInstanceByCategoryId("Switch");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		String idArray[] = ids.split(",");
		List<ResourceInstance> resultResourceInstanceList = new ArrayList<ResourceInstance>();
		if(resourceInstanceList == null || resourceInstanceList.size() <= 0){
			return resultResourceInstanceList;
		}
		listFor : for(int j = 0 ; j < resourceInstanceList.size() ; j ++){
			for(int i = 0 ; i < idArray.length ; i ++){
				long idArrayItem = -1L;
				try {
					idArrayItem = Long.parseLong(idArray[i]);
				} catch (NumberFormatException e) {
					logger.error(e.getMessage());
					continue;
				}
				if(idArrayItem == resourceInstanceList.get(j).getId()){
					continue listFor;
				}
			}
			resultResourceInstanceList.add(resourceInstanceList.get(j));
		}
		return resultResourceInstanceList;
	}
	
	private ResourceCategoryBo categoryDefToResourceCategoryBo(CategoryDef categoryDef){
		ResourceCategoryBo categoryBo = new ResourceCategoryBo();
		categoryBo.setId(categoryDef.getId());
		categoryBo.setName(categoryDef.getName());
		return categoryBo;
	}

	public IConfigDeviceDao getConfigDeviceDao() {
		return configDeviceDao;
	}

	public void setConfigDeviceDao(IConfigDeviceDao configDeviceDao) {
		this.configDeviceDao = configDeviceDao;
	}
}
