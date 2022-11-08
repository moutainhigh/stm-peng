package com.mainsteam.stm.portal.business.service.impl;

import java.util.List;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.IBizSelfApi;
import com.mainsteam.stm.portal.business.bo.BizSelfBo;
import com.mainsteam.stm.portal.business.dao.IBizSelfDao;

public class BizSelfImpl implements IBizSelfApi{
	private IBizSelfDao bizSelfDao;
	private ISequence seq;
	
	@Override
	public void insert(BizSelfBo bizSelfBo) {
		bizSelfBo.setId(seq.next());
		bizSelfDao.insert(bizSelfBo);
	}
	@Override
	public List<BizSelfBo> getList() {
		return bizSelfDao.getList();
	}
	@Override
	public void deleteById(long id) {
		bizSelfDao.deleteById(id);
	}
	public void setBizSelfDao(IBizSelfDao bizSelfDao) {
		this.bizSelfDao = bizSelfDao;
	}
	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	public IBizSelfDao getBizSelfDao() {
		return bizSelfDao;
	}
	public ISequence getSeq() {
		return seq;
	}
	
}
