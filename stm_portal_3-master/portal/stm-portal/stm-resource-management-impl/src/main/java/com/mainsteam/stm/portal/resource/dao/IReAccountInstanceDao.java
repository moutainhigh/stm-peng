package com.mainsteam.stm.portal.resource.dao;

import java.util.List;

import com.mainsteam.stm.portal.resource.po.ReAccountInstancePo;

public interface IReAccountInstanceDao {
	int insert(ReAccountInstancePo reAccountInstancePo);
	List<ReAccountInstancePo> getList();
	List<ReAccountInstancePo> getByAccountId(long accountId);
	int deleteResourceAndAccountRelation(long[] instanceIds);
}
