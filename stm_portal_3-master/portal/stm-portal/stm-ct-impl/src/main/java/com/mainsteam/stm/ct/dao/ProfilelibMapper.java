package com.mainsteam.stm.ct.dao;

import com.mainsteam.stm.ct.bo.MsProfilelibMain;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ProfilelibMapper  {

    public void getProfilelibList(Page<MsProfilelibMain,MsProfilelibMain> page);

	public int insert(MsProfilelibMain msProfilelibParams);

	public int updateById(MsProfilelibMain msProfilelibParams);

	public int removeById(String id);

	public int removeByIds(Long[] temps);

	public MsProfilelibMain getById(String id);


}
