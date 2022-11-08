package com.mainsteam.stm.portal.business.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.instancelib.InstanceRelationService;
import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.IBizDepApi;
import com.mainsteam.stm.portal.business.bo.BizDepBo;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.dao.IBizDepDao;
import com.mainsteam.stm.portal.business.dao.IBizSelfDao;
import com.mainsteam.stm.portal.business.dao.IBizServiceDao;

public class BizDepImpl implements IBizDepApi,InstancelibListener {
	private static Logger logger = Logger.getLogger(BizDepImpl.class);
	private IBizDepDao bizDepDao;
	private IBizServiceDao bizServiceDao;
	private IBizSelfDao bizSelfDao;
	private ISequence seq;
	@Resource
	private RelationService relationService;
	@Resource
	private InstanceRelationService instanceRelationService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private InstanceStateService instanceStateService;
	@Autowired
	CapacityService capacityService;
	
	/**
	 * 业务单位新增
	 */
	@Override
	public BizDepBo insert(BizDepBo bizDepBo) {
		int nameIsExsit = bizDepDao.checkGroupNameIsExsit(bizDepBo.getName(),null,bizDepBo.getType());
		if(nameIsExsit > 0){
			bizDepBo.setId(-1l);
			return bizDepBo;
		}
		bizDepBo.setId(seq.next());
		bizDepBo.setEntryDateTime(new Date());
		bizDepDao.insert(bizDepBo);
		return bizDepBo;
	}

	/**
	 * 查询所有用户信息
	 */
	@Override
	public List<BizDepBo> getList(Integer type) {
		List<BizDepBo> bizDepBoList = bizDepDao.getList(type);
		return bizDepBoList;
	}

	public IBizDepDao getBizDepDao() {
		return bizDepDao;
	}

	public void setBizDepDao(IBizDepDao bizDepDao) {
		this.bizDepDao = bizDepDao;
	}

	public ISequence getSeq() {
		return seq;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}

	@Override
	public Object getAllRelationsByIdAndType(Long id, Integer type) throws Exception {
		List<Relation> relations = relationService.getRelationByInstanceType(InstanceTypeEnum.BUSINESS_SERVICE);
		if(null==relations) relations = new ArrayList<Relation>();
		List<Relation> relList = relationService.getRelationByInstanceType(InstanceTypeEnum.BUSINESS_UNIT);
		if(null!=relList) relations.addAll(relList);
		Set<Long> bizDepIds = new HashSet<Long>();
		Set<Long> bizSerIds = new HashSet<Long>();
		Set<Long> bizMainIds = new HashSet<Long>();
		//查询业务单位时存在多个业务服务（每个业务服务与业务应用之间的关系）
		Map<Long, Set<Long>> bizSerRelationBizMains = new HashMap<Long, Set<Long>>();
		for(Relation relation:relations){
			PathRelation path = (PathRelation)relation;
			if(id==path.getToInstanceId() || id==path.getFromInstanceId()){
				bizMainIds.add(path.getInstanceId());
				if(InstanceTypeEnum.BUSINESS_UNIT.equals(path.getToInstanceType())){
					bizDepIds.add(path.getToInstanceId());
				}
				if(InstanceTypeEnum.BUSINESS_UNIT.equals(path.getFromInstanceType())){
					bizDepIds.add(path.getFromInstanceId());
				}
				if(InstanceTypeEnum.BUSINESS_SERVICE.equals(path.getToInstanceType())){
					bizSerIds.add(path.getToInstanceId());
					if(bizSerRelationBizMains.containsKey(path.getToInstanceId())){
						bizSerRelationBizMains.get(path.getToInstanceId()).add(path.getInstanceId());
					}else{
						Set<Long> set = new HashSet<Long>();
						set.add(path.getInstanceId());
						bizSerRelationBizMains.put(new Long(path.getToInstanceId()),set);
					} 
				}
				if(InstanceTypeEnum.BUSINESS_SERVICE.equals(path.getFromInstanceType())){
					bizSerIds.add(path.getFromInstanceId());
					if(bizSerRelationBizMains.containsKey(path.getFromInstanceId())){
						bizSerRelationBizMains.get(path.getFromInstanceId()).add(path.getInstanceId());
					}else{
						Set<Long> set = new HashSet<Long>();
						set.add(path.getInstanceId());
						bizSerRelationBizMains.put(new Long(path.getFromInstanceId()),set);
					} 
				}
			}
		}
		if(type==0){
			for(Long l:bizSerRelationBizMains.keySet()){
				for(Relation relation:relations){
					PathRelation path = (PathRelation)relation;
					if((l.longValue()==path.getFromInstanceId() 
							&& InstanceTypeEnum.BUSINESS_SERVICE.equals(path.getFromInstanceType()))
							|| (l.longValue()==path.getToInstanceId() 
							&& InstanceTypeEnum.BUSINESS_SERVICE.equals(path.getToInstanceType()))){
						bizSerRelationBizMains.get(l).add(path.getInstanceId());
						bizMainIds.add(path.getInstanceId());
					}
				}
			}
			bizDepIds.add(id);
		}else if(type==1){
			bizSerIds.add(id);
		}
		List<Long>  bizDepIdLists = new ArrayList<Long>();
		bizDepIdLists.addAll(bizDepIds);
		List<Long>  bizSerIdLists = new ArrayList<Long>();
		bizSerIdLists.addAll(bizSerIds);
		List<Long>  bizMainIdLists = new ArrayList<Long>();
		Set<Long> tempBizMainIds = new HashSet<Long>();
		for(Long l : bizMainIds){
			List<Relation> cuRelations = relationService.getRelationByInstanceId(l);
			if(null!=cuRelations&&cuRelations.size()>0){
				boolean flag = true;
				for(Relation r : cuRelations){
					PathRelation pc = (PathRelation) r;
					if(pc.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_SERVICE)
							&&pc.getToInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)){
						if((type==0 && bizSerIds.contains(pc.getFromInstanceId())) 
								|| (type==1 && pc.getFromInstanceId()==id.longValue())){
							flag = false;
							break;
						}
					}
				}
				if(flag) tempBizMainIds.add(l);
			}
		}
		bizMainIds.removeAll(tempBizMainIds);
		bizMainIdLists.addAll(bizMainIds);
		List<BizDepBo> bizDepBos = bizDepDao.getListByIds(bizDepIdLists);
		List<BizDepBo> bizSerBos = bizDepDao.getListByIds(bizSerIdLists);
		List<BizServiceBo> bizServiceBos = bizServiceDao.getListByIds(bizMainIdLists);
		if(null!=bizServiceBos && bizServiceBos.size()>0){
			for(BizServiceBo bizServiceBo: bizServiceBos){
				List<Relation> currRelations = relationService.getRelationByInstanceId(bizServiceBo.getId());
				if(null!=currRelations && currRelations.size()>0){
					List<Long> instanceIds = new ArrayList<Long>();
					for(Relation c:currRelations){
						PathRelation pr = (PathRelation) c;
						if(pr.getFromInstanceId()==bizServiceBo.getId() 
								&& pr.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
								&& pr.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
							instanceIds.add(pr.getToInstanceId());
						}
					}
					bizServiceBo.setResourceInstances(getResourceInstanceByIds(instanceIds));
				}
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("unit",bizDepBos);
		for(BizDepBo bo: bizSerBos){
			bo.setBizMainIds(bizSerRelationBizMains.get(bo.getId()));
		}
		result.put("service",bizSerBos);
		result.put("app",bizServiceBos);
		return result;
	}
	
	/**
	 * 根据资源ids获取资源
	 * @param instanceIds
	 * @return
	 * @throws Exception
	 */
	private List<com.mainsteam.stm.portal.business.bo.ResourceInstance> 
		getResourceInstanceByIds(List<Long> instanceIds) throws Exception{
		List<ResourceInstance> resourceInstances = resourceInstanceService.getResourceInstances(instanceIds);
		List<com.mainsteam.stm.portal.business.bo.ResourceInstance> list = 
				new ArrayList<com.mainsteam.stm.portal.business.bo.ResourceInstance>();
		if(null!=resourceInstances && resourceInstances.size()>0){
			for(ResourceInstance instance:resourceInstances){
				com.mainsteam.stm.portal.business.bo.ResourceInstance instanceVo = 
						new com.mainsteam.stm.portal.business.bo.ResourceInstance();
				instanceVo.setId(String.valueOf(instance.getId()));
				instanceVo.setText(instance.getShowName());
				instanceVo.setState("open");
				instanceVo.setChecked(false);
				HashMap<String, String> attributes = new HashMap<String, String>();
				attributes.put("ipAddress", instance.getShowIP());
				attributes.put("resourceId", instance.getResourceId());
				CategoryDef cd = capacityService.getCategoryById(instance.getCategoryId());
				if(cd!=null){
					attributes.put("categoryId",cd.getId());
					attributes.put("parentCategoryId",cd.getParentCategory().getId());
				} 
				if(null == instanceStateService.getState(instance.getId())){
					attributes.put("status", InstanceStateEnum.NORMAL.name());
				}else{
					attributes.put("status", instanceStateService.getState(instance.getId()).getState().name());
				}
				instanceVo.setAttributes(attributes);
				list.add(instanceVo);
			}
		}
		return list;
	}

	public IBizServiceDao getBizServiceDao() {
		return bizServiceDao;
	}

	public void setBizServiceDao(IBizServiceDao bizServiceDao) {
		this.bizServiceDao = bizServiceDao;
	}

	@Override
	public int del(long id,int type) throws Exception{
		relationService.removeRelation(id, type==0?
				InstanceTypeEnum.BUSINESS_UNIT:InstanceTypeEnum.BUSINESS_SERVICE);
		//调server接口，删除关系
		int count = bizDepDao.del(id);
		return count;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getListByIds(List<Long> ids,List<Long> resourceIds,List<Long> picIds) throws Exception {
		List list = new ArrayList();
		list.add(bizDepDao.getListByIds(ids));
		list.add(getResourceInstanceByIds(resourceIds));
		list.add(bizSelfDao.getListByIds(picIds));
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		try {
			if(instancelibEvent.getEventType()==EventEnum.INSTANCE_DELETE_EVENT){
				// deleteIds 需要删除的资源实例Id 集合
				List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
				//模块实现自己删除资源实例相关操作逻辑
				if(deleteIds!=null && deleteIds.size()>0){
					for(int i=0;i<deleteIds.size();i++){
						//删除资源关联的关系数据
						relationService.removeRelation(deleteIds.get(i),InstanceTypeEnum.RESOURCE);
					}
					logger.info("del resource relations of stm_instancelib_relation successful");
				}
			}
		} catch (Exception e) {
			logger.error("del resource relations of stm_instancelib_relation failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}	
	}

	@Override
	public BizDepBo get(long id) throws Exception {
		return bizDepDao.get(id);
	}

	@Override
	public int update(BizDepBo bo) {
		int nameIsExsit = bizDepDao.checkGroupNameIsExsit(bo.getName(),bo.getOldName(),bo.getType());
		if(nameIsExsit > 0){
			return -1;
		}
		return bizDepDao.update(bo);
	}

	public IBizSelfDao getBizSelfDao() {
		return bizSelfDao;
	}

	public void setBizSelfDao(IBizSelfDao bizSelfDao) {
		this.bizSelfDao = bizSelfDao;
	}
}
