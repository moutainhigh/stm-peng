package com.mainsteam.stm.ct.impl;


import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.ct.api.IProbeService;
import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.ct.dao.ProbeMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


public class ProbeServiceImpl implements IProbeService{
    @Autowired
    private ProbeMapper probeMapper;

	@Override
	public int deleteProbe(String id) {
		// TODO Auto-generated method stub
		return probeMapper.removeById(id);
	}

	@Override
	public int deleteBatchProbe(String ids) {
		// TODO Auto-generated method stub
		String[] split = ids.split(",");
		Long [] temps=new Long[split.length];
		for (int i = 0; i < split.length; i++) {
			temps[i]=Long.parseLong(split[i]);
		}
		return probeMapper.removeByIds(temps);
	}

	

	@Override
	public void getProbeList(Page<MsProbe, MsProbe> page) {
		// TODO Auto-generated method stub
		probeMapper.getProbeList(page);
	}

	@Override
	public int addProbe(MsProbe msProbeParams) {
		// TODO Auto-generated method stub
		return probeMapper.addProbe(msProbeParams);
	}

	@Override
	public int editProbe(MsProbe msProbeParams) {
		// TODO Auto-generated method stub
		return probeMapper.updateById(msProbeParams);
	}

	@Override
	public MsProbe getById(Integer probeId) {
		// TODO Auto-generated method stub
		return probeMapper.getById(probeId);
	}
}
