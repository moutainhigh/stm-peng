package com.mainsteam.stm.ct.dao;

import java.util.List;

import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ProbeMapper{

    public void getProbeList(Page<MsProbe,MsProbe> page);

	public int addProbe(MsProbe msProbeParams);

	public int updateById(MsProbe msProbeParams);

	public int removeById(String id);

	public int removeByIds(Long [] ids);

	public MsProbe getById(Integer id);


}
