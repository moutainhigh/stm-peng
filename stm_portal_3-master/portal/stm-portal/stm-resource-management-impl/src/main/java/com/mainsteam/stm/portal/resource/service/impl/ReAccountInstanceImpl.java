package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.bo.ReAccountInstanceBo;
import com.mainsteam.stm.portal.resource.dao.IReAccountInstanceDao;
import com.mainsteam.stm.portal.resource.po.ReAccountInstancePo;

public class ReAccountInstanceImpl implements IReAccountInstanceApi, InstancelibListener {
	private static Logger logger = Logger.getLogger(ReAccountInstanceImpl.class);
	@Resource(name="protalResourceReAccountInstanceDao")
	private IReAccountInstanceDao reAccountInstanceDao;
	@Resource(name="ocProtalResourceReAccountInstanceSeq")
	private ISequence seq;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Override
	public int insert(ReAccountInstanceBo reAccountInstanceBo) {
		ReAccountInstancePo reAccountInstancePo = new ReAccountInstancePo();
		BeanUtils.copyProperties(reAccountInstanceBo, reAccountInstancePo);
		return reAccountInstanceDao.insert(reAccountInstancePo);
	}

	@Override
	public List<ReAccountInstanceBo> getList() {
		List<ReAccountInstancePo> poList = reAccountInstanceDao.getList();
		List<ReAccountInstanceBo> boList = new ArrayList<ReAccountInstanceBo>();
		for (int i = 0; i < poList.size(); i++) {
			ReAccountInstanceBo reAccountInstanceBo = new ReAccountInstanceBo();
			BeanUtils.copyProperties(poList.get(i), reAccountInstanceBo);
			boList.add(reAccountInstanceBo);
		}
		return boList;
	}

	@Override
	public List<ReAccountInstanceBo> getByAccountId(long accountId) {
		List<ReAccountInstancePo> poList = reAccountInstanceDao
				.getByAccountId(accountId);
		List<ReAccountInstanceBo> boList = new ArrayList<ReAccountInstanceBo>();
		for (int i = 0; i < poList.size(); i++) {
			ReAccountInstanceBo reAccountInstanceBo = new ReAccountInstanceBo();
			BeanUtils.copyProperties(poList.get(i), reAccountInstanceBo);
			try {
				long begin=System.currentTimeMillis();
				ResourceInstance ri = resourceInstanceService.getResourceInstance(reAccountInstanceBo.getInstanceid());
				
				if(logger.isDebugEnabled()){
					logger.info("[Remote]Class:ResourceInstanceService; Method:getResourceInstance;Runtime:"+(System.currentTimeMillis()-begin));
				}
				reAccountInstanceBo.setInstancename(ri.getShowName());
			} catch (InstancelibException e) {
				logger.error(e);
			}
			boList.add(reAccountInstanceBo);
		}
		return boList;
	}

	public IReAccountInstanceDao getReAccountInstanceDao() {
		return reAccountInstanceDao;
	}

	public void setReAccountInstanceDao(
			IReAccountInstanceDao reAccountInstanceDao) {
		this.reAccountInstanceDao = reAccountInstanceDao;
	}

	public ISequence getSeq() {
		return seq;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}

	@Override
	public int deleteResourceAndAccountRelation(long[] instanceIds) {
		return reAccountInstanceDao.deleteResourceAndAccountRelation(instanceIds);
	}

	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType()==EventEnum.INSTANCE_DELETE_EVENT){
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			long[] instanceids = new long[deleteIds.size()];
			for(int i = 0; i < deleteIds.size(); i ++){
				instanceids[i] = deleteIds.get(i);
			}
			reAccountInstanceDao.deleteResourceAndAccountRelation(instanceids);
		}
	}

}
