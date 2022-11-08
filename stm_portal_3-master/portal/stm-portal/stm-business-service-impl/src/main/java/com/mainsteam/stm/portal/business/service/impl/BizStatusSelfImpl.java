package com.mainsteam.stm.portal.business.service.impl;


import java.util.List;

import org.apache.log4j.Logger;

import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.IBizStatusSelfApi;
import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;
import com.mainsteam.stm.portal.business.dao.IBizStatusSelfDao;

public class BizStatusSelfImpl implements IBizStatusSelfApi {
	private static Logger logger = Logger.getLogger(BizStatusSelfImpl.class);
	private IBizStatusSelfDao bizStatusSelfDao;
	private ISequence seq;
	
	/**
	 */
	@Override
	public int insert(BizStatusSelfBo bizStatusSelfBo) {
		bizStatusSelfBo.setId(seq.next());
		int count = bizStatusSelfDao.insert(bizStatusSelfBo);
		return count;
	}
	
	@Override
	public List<BizStatusSelfBo> getByBizSerId(long bizSerId) {
		List<BizStatusSelfBo> bizStatusSelfBos = bizStatusSelfDao.getByBizSerId(bizSerId);
		return bizStatusSelfBos;
	}
	
	@Override
	public void delByBizSerId(long bizSerId) {
		bizStatusSelfDao.delByBizSerId(bizSerId);
	}

	public IBizStatusSelfDao getBizStatusSelfDao() {
		return bizStatusSelfDao;
	}

	public void setBizStatusSelfDao(IBizStatusSelfDao bizStatusSelfDao) {
		this.bizStatusSelfDao = bizStatusSelfDao;
	}

	public ISequence getSeq() {
		return seq;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}

	@Override
	public void delByBizSerIdAndInstanceIds(long bizSerId, Long[] instanceIds) {
		bizStatusSelfDao.delByBizSerIdAndInstanceIds(bizSerId,instanceIds);
	}

}
