package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.bo.CustomGroupResourceBo;
import com.mainsteam.stm.portal.resource.dao.ICustomResourceGroupDao;
import com.mainsteam.stm.portal.resource.po.CustomGroupPo;
import com.mainsteam.stm.portal.resource.po.CustomGroupResourcePo;

@Service("customResourceGroupApi")
public class CustomResourceGroupImpl implements ICustomResourceGroupApi{
	
	private static Logger logger = Logger.getLogger(CustomResourceGroupImpl.class);
	
	@Autowired
	private ICustomResourceGroupDao customResourceGroupDao;
	
	
	private ISequence seq;

	@Autowired
	public CustomResourceGroupImpl(SequenceFactory sequenceFactory){
		seq = sequenceFactory.getSeq(SEQ.SEQNAME_STM_RESOURCE_GROUP);
	}
	
	@Override
	public int insert(CustomGroupBo bo) {
		CustomGroupPo po=new CustomGroupPo();
		//获取主键
		long groupId=seq.next();
		
		po.setId(groupId);
		po.setName(bo.getName());
		po.setEntryId(bo.getEntryId());
		po.setEntryDatetime(bo.getEntryDatetime());
		po.setGroupType(bo.getGroupType());
		po.setPid(bo.getPid());
		po.setSort(bo.getSort());
		int nameIsExsit = customResourceGroupDao.checkGroupNameIsExsit(po);
		
		if(nameIsExsit > 0){
			
			return -1;
			
		}
		
		//存储Group
		int count = customResourceGroupDao.insert(po);
		
		List<String> resourceInstances=bo.getResourceInstanceIds();
		List<CustomGroupResourcePo> poList=new ArrayList<CustomGroupResourcePo>();

		for(String id:resourceInstances){
			CustomGroupResourcePo cgrPo=new CustomGroupResourcePo();
			cgrPo.setGroupId(groupId);
			cgrPo.setResourceID(id);
			poList.add(cgrPo);
		}
		
		//存储Group 绑定的resource
		customResourceGroupDao.batchInsert(poList);
		
		if(count > 0){
			
			return (int)groupId;
			
		}else{
			
			return 0;
		}
		
	}
	
	@Override
	public int del(long id) {
		
		//删除对应关系
		int count = customResourceGroupDao.deleteGroupByGroup(id);
		
		//删除资源组
		customResourceGroupDao.deleteResourceIDsByGroup(id);
		
		return count;
	}
	@Override
	public int update(CustomGroupBo customGroupBo){
		
		CustomGroupPo po = this.toPo(customGroupBo);
		
		//先检查是否修改了名字
		CustomGroupPo oldPo = customResourceGroupDao.getCustomGroup(customGroupBo.getId());
		String oldName = oldPo.getName();
		// 设置Pid
		po.setPid(oldPo.getPid());
		
		int count = 0;
		
		if(oldName != null && !oldName.equals("") && oldName.equals(customGroupBo.getName())){
			//没有修改名字
			count = customResourceGroupDao.updateGroup(po);
		}else{
			//修改了名字
			if(customResourceGroupDao.checkGroupNameIsExsit(po) > 0){
				return -1;
			}else{
				count = customResourceGroupDao.updateGroup(po);
			}
		}
		
		
		//在查找与之对应的资源ID列表，若没有改变，则不更新数据库
		List<String> resourceIDs = customResourceGroupDao.getGroupResourceIdsByGroup(po.getId());
		
		Set<String> newResourceIDs = new HashSet<String>(resourceIDs);
		Set<String> oldResourceIDs = new HashSet<String>(customGroupBo.getResourceInstanceIds());
		
		if(!newResourceIDs.equals(oldResourceIDs)){
			
			logger.info("Update custom resource and group relationship!");
			
			//更新资源组对应的资源ID
			
			//1.先删除旧对应关系
			customResourceGroupDao.deleteResourceIDsByGroup(po.getId());
			//2.插入新的对应关系
			List<CustomGroupResourcePo> poList=new ArrayList<CustomGroupResourcePo>();
			
			for(String id : customGroupBo.getResourceInstanceIds()){
				CustomGroupResourcePo cgrPo=new CustomGroupResourcePo();
				cgrPo.setGroupId(po.getId());
				cgrPo.setResourceID(id);
				poList.add(cgrPo);
			}
			customResourceGroupDao.batchInsert(poList);
			
		}

		
		return count;
	}
	@Override
	public CustomGroupResourceBo get(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> getCustomGroupInstanceIds(long groupId){
		return customResourceGroupDao.getGroupResourceIdsByGroup(groupId);
	}
	
	
	@Override
	public List<CustomGroupBo> getList(long userId) {
		List<CustomGroupPo> poList = customResourceGroupDao.getList(userId);
		List<CustomGroupBo> boList = new ArrayList<CustomGroupBo>();
		
		for(CustomGroupPo po : poList){
			
			CustomGroupBo bo = this.toBo(po);
			bo.setResourceInstanceIds(customResourceGroupDao.getGroupResourceIdsByGroup(po.getId()));
			boList.add(bo);
			
		}
		
		return boList;
	}
	
	

	@Override
	public int batchDel(long[] id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int deleteGroupAndResourceRelation(long[] ids) {
		// 删除对应关系
		int result = customResourceGroupDao.deleteGroupAndResourceRelationById(ids);
		//查询目前没有对应关系的资源组
		/** 现在允许资源组可以没有资源则不删除资源组
		List<Long> idList = customResourceGroupDao.selectResourceNumberIsZeroGroup();
		if(idList != null && idList.size() > 0){
			for(long resourceId : idList){
				customResourceGroupDao.deleteGroupByGroup(resourceId);
			}
		}
		*/
		return result;
	}
	
	@Override
	public int deleteResourceFromCustomGroup(CustomGroupBo groupBo) {
		int count = customResourceGroupDao.deleteResourceFromCustomGroupByIds(groupBo);
		return count;
	}

	public ICustomResourceGroupDao getCustomResourceGroupDao() {
		return customResourceGroupDao;
	}

	public void setCustomResourceGroupDao(
			ICustomResourceGroupDao customResourceGroupDao) {
		this.customResourceGroupDao = customResourceGroupDao;
	}

	public ISequence getSeq() {
		return seq;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	
	
	private CustomGroupBo toBo(CustomGroupPo po){
		CustomGroupBo bo=new CustomGroupBo();
		bo.setId(po.getId());
		bo.setName(po.getName());
		bo.setEntryId(po.getEntryId());
		bo.setEntryDatetime(po.getEntryDatetime());
		bo.setGroupType(po.getGroupType());
		bo.setPid(po.getPid());
		bo.setSort(po.getSort());
		return bo;
	}
	
	private CustomGroupPo toPo(CustomGroupBo bo){
		CustomGroupPo po=new CustomGroupPo();
		po.setId(bo.getId());
		po.setName(bo.getName());
		po.setEntryId(bo.getEntryId());
		po.setEntryDatetime(bo.getEntryDatetime());
		po.setGroupType(bo.getGroupType());
		po.setPid(bo.getPid());
		po.setSort(bo.getSort());
		return po;
	}

	/**
	 * 获取单个用户自定义资源组
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public CustomGroupBo getCustomGroup(long id) {
		CustomGroupPo customGroupPo = customResourceGroupDao.getCustomGroup(id);
		if(customGroupPo == null){
			logger.error("Get customGroupPo error , id : " + id);
			return null;
		}
		CustomGroupBo customGroupbo = toBo(customGroupPo);
		customGroupbo.setResourceInstanceIds(customResourceGroupDao.getGroupResourceIdsByGroup(id));
		return customGroupbo;
	}
	

	@Override
	public int insertGroupAndResourceRelation(long groupId, String resourceID) {
		//2.插入新的对应关系
		List<CustomGroupResourcePo> poList=new ArrayList<CustomGroupResourcePo>();
		CustomGroupResourcePo cgrPo=new CustomGroupResourcePo();
		cgrPo.setGroupId(groupId);
		cgrPo.setResourceID(resourceID);
		poList.add(cgrPo);
		int count =  customResourceGroupDao.batchInsert(poList);
		
		return count;
	}

	@Override
	public boolean insertIntoGroupForInstances(String instanceIds,String groupIds) {
		
		boolean isSuccess = false;
		
		String[] instanceIdArray = instanceIds.split(",");
		String[] groupIdArray = groupIds.split(",");
		
		List<CustomGroupResourcePo> poList = new ArrayList<CustomGroupResourcePo>();

		for(String instanceId : instanceIdArray){
			for (String groupId : groupIdArray) {
				CustomGroupResourcePo cgrPo=new CustomGroupResourcePo();
				cgrPo.setGroupId(Long.parseLong(groupId));
				cgrPo.setResourceID(instanceId);
				if(customResourceGroupDao.checkGroupResourceRelationIsExsit(cgrPo) > 0){
					//该关系已存在
					continue;
				}
				poList.add(cgrPo);
			}
		}
		
		if(poList == null || poList.size() <= 0){
			logger.debug("InsertIntoGroupForInstances all exsit!");
			isSuccess = true;
		}else{
			//存储Group 绑定的resource
			if(customResourceGroupDao.batchInsert(poList) > 0){
				isSuccess = true;
			}
		}
		
		return isSuccess;
	}
	
	public int getMaxSortByEntryId(long entryId) {
		return customResourceGroupDao.getMaxSortByEntryId(entryId);
	}

	@Override
	public int updateGroupSort(int sort, long groupId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("sort", sort + "");
		map.put("groupId", groupId + "");
		return customResourceGroupDao.updateGroupSort(map);
	}

	@Override
	public List<CustomGroupBo> getChildGroupsById(Long entryId, Long parentGroupId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("entryId", entryId + "");
		map.put("parentGroupId", parentGroupId == null ? null : parentGroupId+"");
		List<CustomGroupPo> pos = customResourceGroupDao.getChildGroupsById(map);
		List<CustomGroupBo> bos = new ArrayList<CustomGroupBo>();
		for (int i = 0; i < pos.size(); i++) {
			CustomGroupBo bo = this.toBo(pos.get(i));
			bos.add(bo);
		}
		return bos;
	}
	
}
