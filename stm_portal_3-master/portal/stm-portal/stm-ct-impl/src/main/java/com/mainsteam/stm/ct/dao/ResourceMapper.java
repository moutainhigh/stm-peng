package com.mainsteam.stm.ct.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ct.bo.MsResourceMain;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ResourceMapper{

    public void getResourceList(Page<MsResourceMain,MsResourceMain> page);

    MsResourceMain getFullResource(@Param("msResourceParams") MsResourceMain msResourceParams);

    List<String> getResourceIdList(@Param("msResourceParams") MsResourceMain msResourceParams);

	public int insert(MsResourceMain msResourceParams);

	public int updateById(MsResourceMain msResourceParams);

	public int removeById(String id);

	public int removeByIds(Long[] temps);

	public MsResourceMain getById(@Param("id")String id);

	public List<MsResourceMain> getResourceIdAndTestName(@Param("testWay")String testWay);
	
	public int success(@Param("id") String id);

	public int fail(@Param("id") String id);
}
