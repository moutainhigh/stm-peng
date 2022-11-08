package com.mainsteam.stm.ct.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.ct.dao.ProbeMapper;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ProbeMapperImpl extends BaseDao<MsProbe> implements ProbeMapper {

	public ProbeMapperImpl(SqlSessionTemplate session) {
		super(session, ProbeMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getProbeList(Page<MsProbe, MsProbe> page) {
		// TODO Auto-generated method stub
		super.select("getProbeList",page);
	}

	@Override
	public int addProbe(MsProbe msProbeParams) {
		// TODO Auto-generated method stub
		return super.insert(msProbeParams);
	}

	@Override
	public int updateById(MsProbe msProbeParams) {
		// TODO Auto-generated method stub
		return super.update(msProbeParams);
	}

	@Override
	public int removeById(String id) {
		// TODO Auto-generated method stub
		return super.del(Long.parseLong(id));
	}

	@Override
	public int removeByIds(Long [] ids) {
		// TODO Auto-generated method stub
		
		return super.batchDel(ids);
	}

	@Override
	public MsProbe getById(Integer id) {
		// TODO Auto-generated method stub
		return super.get(id);
	}

}
