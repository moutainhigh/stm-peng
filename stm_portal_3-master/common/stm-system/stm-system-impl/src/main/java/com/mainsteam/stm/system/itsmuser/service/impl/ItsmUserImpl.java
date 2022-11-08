package com.mainsteam.stm.system.itsmuser.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.system.itsmuser.api.IItsmUserApi;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;
import com.mainsteam.stm.system.itsmuser.dao.IItsmSystemDao;

@Service
public class ItsmUserImpl implements IItsmUserApi {

	@Autowired
	private IItsmSystemDao itsmSystemDao;

	private ISequence sequence;

	@Autowired
	public ItsmUserImpl(SequenceFactory sequenceFactory) {
		this.sequence = sequenceFactory.getSeq("STM_SYSTEM_ITSM");
	}

	@Override
	public int saveItsmSystem(ItsmSystemBo itsmSystemBo) {
		itsmSystemBo.setId(sequence.next());
		return itsmSystemDao.saveItsmSystem(itsmSystemBo);
	}

	@Override
	public List<ItsmSystemBo> queryItsmSystem(Page<ItsmSystemBo, ItsmSystemBo> page) {
		return itsmSystemDao.queryItsmSystem(page);
	}

	@Override
	public int batchDel(Long[] ids) {
		return this.itsmSystemDao.batchDel(ids);
	}

	@Override
	public ItsmSystemBo getItsmSystemById(Long id) {
		return this.itsmSystemDao.getItsmSystemById(id);
	}

	@Override
	public int updateItsmSystem(ItsmSystemBo itsmSystemBo) {
		return this.itsmSystemDao.updateItsmSystem(itsmSystemBo);
	}

	@Override
	public int updateSystemStartState(Long[] ids, int startState) {
		return this.itsmSystemDao.updateSystemStartState(ids, startState);
	}

	@Override
	public List<ItsmSystemBo> queryAllItsmSystem() {
		return this.itsmSystemDao.queryAllItsmSystem();
	}

	@Override
	public int updateSyncState(ItsmSystemBo itsmSystemBo) {
		return this.itsmSystemDao.updateSyncState(itsmSystemBo);
	}

	@Override
	public boolean isWsdlURLExist(ItsmSystemBo itsmSystemBo) {
		return this.itsmSystemDao.isWsdlURLExist(itsmSystemBo);
	}
}
